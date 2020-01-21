package ilapin.renderingdemo.data.scene_loader

import android.content.Context
import com.google.gson.Gson
import ilapin.common.kotlin.safeLet
import ilapin.engine3d.*
import ilapin.meshloader.MeshLoadingRepository
import ilapin.renderingdemo.domain.scene_loader.PerspectiveCameraPartialConfig
import ilapin.renderingdemo.domain.scene_loader.SceneData
import ilapin.renderingdemo.domain.scene_loader.SceneLoader
import ilapin.renderingdemo.getCameraComponent
import ilapin.renderingengine.LightsRenderingRepository
import ilapin.renderingengine.MeshRenderingRepository
import ilapin.renderingengine.RenderingSettingsRepository
import ilapin.renderingengine.TextureLoadingRepository
import org.joml.Quaternionf
import org.joml.Vector3f
import java.io.BufferedReader

/**
 * @author raynor on 21.01.20.
 */
class AndroidAssetsSceneLoader(
    private val context: Context,
    private val gson: Gson,
    private val meshLoadingRepository: MeshLoadingRepository,
    private val meshRenderingRepository: MeshRenderingRepository,
    private val lightsRenderingRepository: LightsRenderingRepository,
    private val textureLoadingRepository: TextureLoadingRepository,
    private val renderingSettingsRepository: RenderingSettingsRepository
) : SceneLoader {

    override fun loadScene(path: String): SceneData {
        var rootGameObject: GameObject? = null
        val gameObjectsMap = HashMap<String, GameObject>()
        val perspectiveCamerasConfigs = HashMap<String, PerspectiveCameraPartialConfig>()

        val materialsMap = HashMap<String, MaterialComponent>()
        val meshesMap = HashMap<String, MeshComponent>()

        val sceneDto = gson.fromJson(
            context.assets.open(path).bufferedReader().use(BufferedReader::readText),
            SceneDto::class.java
        )

        sceneDto.textures?.forEach {
            safeLet(it.id, it.path) { id, path -> textureLoadingRepository.loadTexture(id, path) }
        }

        sceneDto.materials?.forEach { materialDto ->
            safeLet(materialDto.id, materialDto.textureName) { id, textureName ->
                materialsMap[id] = MaterialComponent(
                    textureName,
                    isDoubleSided = materialDto.isDoubleSided ?: false,
                    isWireframe = materialDto.isWireframe ?: false,
                    isUnlit = materialDto.isUnlit ?: false
                )
            }
        }

        sceneDto.meshes?.forEach {
            safeLet(it.id, it.path) { id, path -> meshesMap[id] = meshLoadingRepository.loadMesh(path) }
        }

        sceneDto.renderingSettings?.let {
            it.clearColor?.let { clearColor ->
                renderingSettingsRepository.setClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3])
            }
            it.ambientColor?.let { ambientColor ->
                renderingSettingsRepository.setAmbientColor(ambientColor[0], ambientColor[1], ambientColor[2])
            }
        }

        sceneDto.layers?.takeIf { it.isNotEmpty() }?.get(0)?.let { layerDto ->
            layerDto.gameObjects?.forEach { gameObjectDto ->
                val gameObjectName = gameObjectDto.name ?: throw IllegalArgumentException("No game object name")
                val gameObject = GameObject(gameObjectName)

                val positionDto = gameObjectDto.position ?: throw IllegalArgumentException("Position not found for game object $gameObjectName")
                val rotationDto = gameObjectDto.rotation ?: throw IllegalArgumentException("Rotation not found for game object $gameObjectName")
                val scaleDto = gameObjectDto.scale ?: throw IllegalArgumentException("Scale not found for game object $gameObjectName")
                gameObject.addComponent(TransformationComponent(
                    Vector3f(positionDto[0], positionDto[1], positionDto[2]),
                    Quaternionf().identity().rotationXYZ(
                        Math.toRadians(rotationDto[0].toDouble()).toFloat(),
                        Math.toRadians(rotationDto[1].toDouble()).toFloat(),
                        Math.toRadians(rotationDto[2].toDouble()).toFloat()
                    ),
                    Vector3f(scaleDto[0], scaleDto[1], scaleDto[2])
                ))

                gameObjectDto.components?.forEach {
                    when (it) {
                        is ComponentDto.DirectionalLightDto -> {
                            it.color?.let { colorComponents ->
                                val directionalLightComponent = DirectionalLightComponent(
                                    Vector3f(colorComponents[0], colorComponents[1], colorComponents[2])
                                )
                                gameObject.addComponent(directionalLightComponent)
                                it.cameraNames?.forEach { cameraName ->
                                    val cameraGameObject = gameObjectsMap[cameraName] ?: throw IllegalArgumentException("Unknown camera $cameraName")
                                    lightsRenderingRepository.addDirectionalLight(
                                        cameraGameObject.getCameraComponent() ?: throw IllegalArgumentException("Camera component not found for $cameraName"),
                                        directionalLightComponent
                                    )
                                }
                            }
                        }
                        is ComponentDto.MeshDto -> {
                            val meshComponent = meshesMap[it.meshName] ?: throw IllegalArgumentException("Unknown mesh ${it.meshName}")
                            gameObject.addComponent(meshComponent)
                            gameObject.addComponent(materialsMap[it.materialName] ?: throw IllegalArgumentException("Unknown material ${it.materialName}"))
                            it.cameraNames?.forEach { cameraName ->
                                val cameraGameObject = gameObjectsMap[cameraName] ?: throw IllegalArgumentException("Unknown camera $cameraName")
                                meshRenderingRepository.addMeshToRenderList(
                                    cameraGameObject.getCameraComponent() ?: throw IllegalArgumentException("Camera component not found for $cameraName"),
                                    meshComponent
                                )
                            }
                        }
                        is ComponentDto.PerspectiveCameraDto -> {
                            gameObject.addComponent(PerspectiveCameraComponent())
                            perspectiveCamerasConfigs[gameObjectName] = PerspectiveCameraPartialConfig(
                                it.fov ?: throw IllegalArgumentException("No Field of View parameter for camera $gameObjectName"),
                                it.zNear ?: throw IllegalArgumentException("No Z Near parameter for camera $gameObjectName"),
                                it.zFar ?: throw IllegalArgumentException("No Z Far parameter for camera $gameObjectName")
                            )
                        }
                    }
                }

                if (gameObjectName == ROOT_GAME_OBJECT_NAME) {
                    rootGameObject = gameObject
                } else {
                    val parentName = gameObjectDto.parent ?: throw IllegalArgumentException("No parent game object name for game object $gameObjectName")
                    val parentGameObject = gameObjectsMap[parentName] ?: throw IllegalArgumentException("Unknown parent game object $parentName")
                    parentGameObject.addChild(gameObject)
                }

                if (gameObjectsMap.containsKey(gameObjectName)) {
                    throw IllegalArgumentException("Duplicate game object $gameObjectName")
                }
                gameObjectsMap[gameObjectName] = gameObject
            }
        }

        return SceneData(
            rootGameObject ?: throw IllegalArgumentException("No root game object"),
            gameObjectsMap,
            perspectiveCamerasConfigs,
            sceneDto.initialCameras?.mapNotNull {
                val cameraGameObject = gameObjectsMap[it] ?: throw IllegalArgumentException("Unknown camera $it")
                cameraGameObject.getCameraComponent()
            }?.takeIf { it.isNotEmpty() } ?: throw IllegalArgumentException("Initial cameras not found")
        )
    }

    companion object {

        private const val ROOT_GAME_OBJECT_NAME = "root"
    }
}