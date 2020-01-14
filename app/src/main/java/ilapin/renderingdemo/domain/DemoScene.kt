package ilapin.renderingdemo.domain

import ilapin.engine3d.PerspectiveCameraComponent
import ilapin.engine3d.Scene

/**
 * @author Игорь on 14.01.2020.
 */
class DemoScene : Scene {

    private val perspectiveCamera = PerspectiveCameraComponent()

    override val cameras = listOf(perspectiveCamera)

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
}