package ilapin.renderingdemo.domain

import ilapin.engine3d.*
import ilapin.meshloader.MeshLoadingRepository
import ilapin.renderingengine.*
import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * @author Игорь on 14.01.2020.
 */
class DemoScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    private val lightsRenderingRepository: LightsRenderingRepository,
    private val textureRepository: TextureRepository,
    private val textureLoadingRepository: TextureLoadingRepository,
    private val meshRenderingRepository: MeshRenderingRepository,
    private val meshLoadingRepository: MeshLoadingRepository
) : Scene {

    private val perspectiveCamera = PerspectiveCameraComponent()

    private val rootGameObject = GameObject().apply {
        addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
    }

    override val cameras = listOf(perspectiveCamera)

    init {
        renderingSettingsRepository.setClearColor(0f, 0f, 0f, 1f)
        renderingSettingsRepository.setAmbientColor(0.1f, 0.1f, 0.1f)

        initPerspectiveCamera()
        initTextures()
        initLights()
        initEarthGlobe()
        //initTriangle()
    }

    override fun onCleared() {
        // do nothing
    }

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        perspectiveCamera.config = PerspectiveCameraComponent.Config(
            45f,
            width.toFloat() / height.toFloat(),
            0.1f,
            1000f
        )
    }

    override fun update() {
        // do nothing
    }

    private fun initPerspectiveCamera() {
        val cameraGameObject = GameObject()
        cameraGameObject.addComponent(
            TransformationComponent(Vector3f(0f, 0f, 5f), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        cameraGameObject.addComponent(perspectiveCamera)
        rootGameObject.addChild(cameraGameObject)
    }

    private fun initTextures() {
        textureRepository.createTexture("colorGreen", 1, 1, intArrayOf(0xff00ff00.toInt()))
        textureLoadingRepository.loadTexture("2k_earth_daymap.jpg")
    }

    private fun initLights() {
        val light1GameObject = GameObject()
        light1GameObject.addComponent(TransformationComponent(
            Vector3f(),
            Quaternionf()
                .identity()
                .rotateX((Math.PI / 4).toFloat())
                .rotateY((Math.PI / 8).toFloat()),
            Vector3f(1f, 1f, 1f)
        ))
        val light1Component = DirectionalLightComponent(Vector3f(1f, 1f, 1f))
        light1GameObject.addComponent(light1Component)
        rootGameObject.addChild(light1GameObject)
        lightsRenderingRepository.addDirectionalLight(perspectiveCamera, light1Component)
    }

    private fun initEarthGlobe() {
        val gameObject = GameObject()
        gameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        val mesh = meshLoadingRepository.loadMesh("earth.obj")
        gameObject.addComponent(mesh)
        gameObject.addComponent(MaterialComponent("2k_earth_daymap.jpg"))
        meshRenderingRepository.addMeshToRenderList(perspectiveCamera, mesh)
        rootGameObject.addChild(gameObject)
    }

    /*private fun initTriangle() {
        val gameObject = GameObject()
        gameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        val mesh = MeshFactory.createVerticalQuad()
        gameObject.addComponent(mesh)
        gameObject.addComponent(MaterialComponent("colorGreen"))
        meshRenderingRepository.addMeshToRenderList(perspectiveCamera, mesh)
        rootGameObject.addChild(gameObject)
    }*/
}