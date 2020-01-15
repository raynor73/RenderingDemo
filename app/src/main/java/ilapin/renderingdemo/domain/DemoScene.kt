package ilapin.renderingdemo.domain

import ilapin.common.kotlin.plusAssign
import ilapin.engine3d.*
import ilapin.meshloader.MeshLoadingRepository
import ilapin.renderingengine.*
import io.reactivex.disposables.CompositeDisposable
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
    private val meshLoadingRepository: MeshLoadingRepository,
    displayMetricsRepository: DisplayMetricsRepository,
    scrollController: ScrollController
) : Scene {

    private val subscriptions = CompositeDisposable()

    private val tmpQuaternion = Quaternionf()

    private val pixelDensityFactor = displayMetricsRepository.getPixelDensityFactor()

    private val perspectiveCamera = PerspectiveCameraComponent()

    private val rootGameObject = GameObject().apply {
        addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
    }

    private val lightTransform = TransformationComponent(
        Vector3f(),
        Quaternionf()
            .identity()
            .rotateX((Math.PI / 4).toFloat())
            .rotateY((Math.PI / 8).toFloat()),
        Vector3f(1f, 1f, 1f)
    )

    override val cameras = listOf(perspectiveCamera)

    init {
        renderingSettingsRepository.setClearColor(0f, 0f, 0f, 1f)
        renderingSettingsRepository.setAmbientColor(0.1f, 0.1f, 0.1f)

        initPerspectiveCamera()
        initTextures()
        initLights()
        initEarthGlobe()
        //initTriangle()

        subscriptions += scrollController.scrollEvent.subscribe { scrollEvent ->
            val yAngle = Math.toRadians((scrollEvent.dx / pixelDensityFactor).toDouble())
            val xAngle = Math.toRadians((scrollEvent.dy / pixelDensityFactor).toDouble())
            tmpQuaternion.set(lightTransform.rotation)
            tmpQuaternion.rotateLocalY(yAngle.toFloat())
            tmpQuaternion.rotateLocalX(xAngle.toFloat())
            lightTransform.rotation = tmpQuaternion
        }
    }

    override fun onCleared() {
        subscriptions.clear()
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
            TransformationComponent(Vector3f(0f, 0f, 2.8f), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
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
        light1GameObject.addComponent(lightTransform)
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