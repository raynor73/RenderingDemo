package ilapin.renderingdemo.domain

import ilapin.engine3d.*
import ilapin.renderingengine.MeshRenderingRepository
import ilapin.renderingengine.RenderingSettingsRepository
import ilapin.renderingengine.TextureRepository
import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * @author Игорь on 14.01.2020.
 */
class DemoScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    private val textureRepository: TextureRepository,
    private val meshRenderingRepository: MeshRenderingRepository
) : Scene {

    private val perspectiveCamera = PerspectiveCameraComponent()

    private val rootGameObject = GameObject().apply {
        addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
    }

    override val cameras = listOf(perspectiveCamera)

    init {
        renderingSettingsRepository.setClearColor(0f, 0f, 0f, 1f)
        renderingSettingsRepository.setAmbientColor(1f, 1f, 1f)

        initPerspectiveCamera()
        initTextures()
        initTriangle()
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
    }

    private fun initTriangle() {
        val gameObject = GameObject()
        gameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        val mesh = MeshFactory.createVerticalQuad()
        gameObject.addComponent(mesh)
        gameObject.addComponent(MaterialComponent("colorGreen", isDoubleSided = true, isUnlit = true))
        meshRenderingRepository.addMeshToRenderList(perspectiveCamera, mesh)
        rootGameObject.addChild(gameObject)
    }
}