package ilapin.renderingdemo.data.scene_loader

import android.content.Context
import com.google.gson.Gson
import ilapin.common.kotlin.safeLet
import ilapin.engine3d.GameObject
import ilapin.engine3d.MaterialComponent
import ilapin.meshloader.MeshLoadingRepository
import ilapin.renderingdemo.domain.scene_loader.SceneGameObjects
import ilapin.renderingdemo.domain.scene_loader.SceneLoader
import ilapin.renderingengine.RenderingSettingsRepository
import ilapin.renderingengine.TextureLoadingRepository
import java.io.BufferedReader

/**
 * @author raynor on 21.01.20.
 */
class AndroidAssetsSceneLoader(
    private val context: Context,
    private val gson: Gson,
    private val meshLoadingRepository: MeshLoadingRepository,
    private val textureLoadingRepository: TextureLoadingRepository,
    private val renderingSettingsRepository: RenderingSettingsRepository
) : SceneLoader {

    override fun loadMesh(path: String): SceneGameObjects {
        val rootGameObject: GameObject? = null
        val gameObjectsMap = HashMap<String, GameObject>()

        val materialsMap = HashMap<String, MaterialComponent>()
        //val

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

        //sceneDto.

        return SceneGameObjects(
            rootGameObject ?: throw IllegalArgumentException("No root game object"), gameObjectsMap
        )
    }
}