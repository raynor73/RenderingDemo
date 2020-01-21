package ilapin.renderingdemo.domain

import ilapin.common.kotlin.plusAssign
import ilapin.common.time.TimeRepository
import ilapin.engine3d.*
import ilapin.renderingdemo.domain.menu_controller.MenuEvent
import ilapin.renderingdemo.domain.scene_loader.SceneLoader
import ilapin.renderingdemo.domain.scroll_controller.ScrollController
import ilapin.renderingdemo.getCameraComponent
import ilapin.renderingengine.DisplayMetricsRepository
import io.reactivex.Observable
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
    menuEvent: Observable<MenuEvent>,
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

    private val rootGameObject: GameObject

    private val lightTransform: TransformationComponent

    private val initialForwardVector: Vector3fc = Vector3f(0f, 0f, -1f)
    private val initialRightVector: Vector3fc = Vector3f(1f, 0f, 0f)

    private var prevTimestamp: Long? = null

    private var shouldUseAlternateCameraSet = false
    private val camerasSet0: List<CameraComponent>
    private val camerasSet1: List<CameraComponent>
    override val cameras = ArrayList<CameraComponent>()

    init {
        camerasSet0 = sceneData.initialCameras
        camerasSet1 = listOf(sceneData.gameObjectsMap["camera1"]?.getCameraComponent() ?: throw IllegalArgumentException("camera1 not found"))
        cameras += camerasSet0

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

        subscriptions += menuEvent.subscribe { event ->
            cameras.clear()
            cameras += when (event) {
                MenuEvent.SwitchCameraEvent -> {
                    shouldUseAlternateCameraSet = !shouldUseAlternateCameraSet
                    if (shouldUseAlternateCameraSet) {
                        camerasSet1
                    } else {
                        camerasSet0
                    }
                }
            }
        }
    }

    override fun onCleared() {
        subscriptions.clear()
    }

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        (camerasSet0 + camerasSet1).forEach {
            when (it) {
                is PerspectiveCameraComponent -> {
                    val partialConfig = sceneData.perspectiveCamerasConfigs[it.gameObject?.name] ?: throw IllegalArgumentException("Camera's partial config not found")
                    it.config = PerspectiveCameraComponent.Config(
                        partialConfig.fieldOfView,
                        width.toFloat() / height.toFloat(),
                        partialConfig.zNear,
                        partialConfig.zFar
                    )
                }
                else -> throw IllegalArgumentException("Unexpected component type ${it.javaClass.simpleName}")
            }
        }
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
            val cameraGameObject = if (shouldUseAlternateCameraSet) {
                camerasSet1.first().gameObject
            } else {
                camerasSet0.first().gameObject
            } ?: throw IllegalArgumentException("Camera game object not found")
            val cameraTransform = cameraGameObject.getComponent(TransformationComponent::class.java) ?: throw IllegalArgumentException("Camera transformation component not found")
            cameraTransform.rotation.mul(tmpQuaternion, tmpQuaternion1)
            cameraTransform.rotation = tmpQuaternion1

            tmpVector.rotate(cameraTransform.rotation)
            tmpVector1.rotate(cameraTransform.rotation)

            tmpVector.add(cameraTransform.position)
            tmpVector.add(tmpVector1)
            cameraTransform.position = tmpVector
        }
        prevTimestamp = currentTimestamp
    }

    companion object {

        private const val CAMERA_MOVEMENT_SPEED = 1f
        private const val CAMERA_STEERING_SPEED = 1f
        private const val NANOS_IN_SECOND = 1e9f
    }
}