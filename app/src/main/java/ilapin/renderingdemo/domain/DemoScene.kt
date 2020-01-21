package ilapin.renderingdemo.domain

import ilapin.common.kotlin.plusAssign
import ilapin.common.time.TimeRepository
import ilapin.engine3d.*
import ilapin.renderingdemo.domain.scene_loader.SceneLoader
import ilapin.renderingengine.DisplayMetricsRepository
import io.reactivex.disposables.CompositeDisposable
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector3fc

/**
 * @author Игорь on 14.01.2020.
 */
class DemoScene(
    sceneLoader: SceneLoader,
    displayMetricsRepository: DisplayMetricsRepository,
    scrollController: ScrollController,
    private val movementController: MovementController,
    private val timeRepository: TimeRepository
) : Scene {

    private val subscriptions = CompositeDisposable()

    private val tmpVector = Vector3f()
    private val tmpVector1 = Vector3f()
    private val tmpQuaternion = Quaternionf()
    private val tmpQuaternion1 = Quaternionf()

    private val pixelDensityFactor = displayMetricsRepository.getPixelDensityFactor()

    private val sceneData = sceneLoader.loadScene("shaders_scene.json")

    private val perspectiveCamera: PerspectiveCameraComponent
    private val perspectiveCameraTransform: TransformationComponent

    private val rootGameObject: GameObject

    private val lightTransform: TransformationComponent

    private val initialForwardVector: Vector3fc = Vector3f(0f, 0f, -1f)
    private val initialRightVector: Vector3fc = Vector3f(1f, 0f, 0f)

    private var prevTimestamp: Long? = null

    override val cameras: List<CameraComponent>

    init {
        val perspectiveCameraGameObject = sceneData.gameObjectsMap["camera0"] ?: throw IllegalArgumentException("Camera game object not found")
        perspectiveCamera = perspectiveCameraGameObject.getComponent(PerspectiveCameraComponent::class.java) ?: throw IllegalArgumentException("Camera component not found")
        perspectiveCameraTransform = perspectiveCameraGameObject.getComponent(TransformationComponent::class.java) ?: throw IllegalArgumentException("Camera transformation component not found")
        cameras = listOf(perspectiveCamera)

        rootGameObject = sceneData.rootGameObject

        lightTransform = sceneData.gameObjectsMap["light0"]?.getComponent(TransformationComponent::class.java) ?: throw IllegalArgumentException("Light not found")

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
        val partialConfig = sceneData.perspectiveCamerasConfigs["camera0"] ?: throw IllegalArgumentException("Camera's partial config not found")
        perspectiveCamera.config = PerspectiveCameraComponent.Config(
            partialConfig.fieldOfView,
            width.toFloat() / height.toFloat(),
            partialConfig.zNear,
            partialConfig.zFar
        )
    }

    override fun update() {
        val currentTimestamp = timeRepository.getTimestamp()
        prevTimestamp?.let {
            val dt = (currentTimestamp - it) / NANOS_IN_SECOND
            tmpVector.set(initialForwardVector)
            tmpVector.mul(movementController.movingFraction * CAMERA_MOVEMENT_SPEED * dt)
            tmpVector1.set(initialRightVector)
            tmpVector1.mul(movementController.strafingFraction* CAMERA_MOVEMENT_SPEED * dt)

            tmpQuaternion.identity()
            tmpQuaternion.rotateY(movementController.horizontalSteeringFraction * CAMERA_STEERING_SPEED * dt)
            tmpQuaternion.rotateLocalX(movementController.verticalSteeringFraction * CAMERA_STEERING_SPEED * dt)
            perspectiveCameraTransform.rotation.mul(tmpQuaternion, tmpQuaternion1)
            perspectiveCameraTransform.rotation = tmpQuaternion1

            tmpVector.rotate(perspectiveCameraTransform.rotation)
            tmpVector1.rotate(perspectiveCameraTransform.rotation)

            tmpVector.add(perspectiveCameraTransform.position)
            tmpVector.add(tmpVector1)
            perspectiveCameraTransform.position = tmpVector
        }
        prevTimestamp = currentTimestamp
    }

    companion object {

        private const val CAMERA_MOVEMENT_SPEED = 1f
        private const val CAMERA_STEERING_SPEED = 1f
        private const val NANOS_IN_SECOND = 1e9f
    }
}