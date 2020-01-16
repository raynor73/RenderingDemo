package ilapin.renderingdemo.ui

import android.content.Context
import ilapin.common.android.input.TouchEventFromMessageQueueRepository
import ilapin.common.android.time.LocalTimeRepository
import ilapin.common.android.ui.widgets.joystick_view.JoystickView
import ilapin.common.messagequeue.MessageQueue
import ilapin.engine3d.Scene
import ilapin.meshloader.android.ObjMeshLoadingRepository
import ilapin.renderingdemo.data.JoystickViewJoystick
import ilapin.renderingdemo.domain.DemoScene
import ilapin.renderingdemo.domain.MovementController
import ilapin.renderingdemo.domain.ScrollController
import ilapin.renderingengine.android.AndroidDisplayMetricsRepository
import ilapin.renderingengine.android.BaseGLSurfaceRenderer

/**
 * @author Игорь on 15.01.2020.
 */
class GLSurfaceViewRenderer(
    private val context: Context,
    private val leftJoystickView: JoystickView,
    private val rightJoystickView: JoystickView
) : BaseGLSurfaceRenderer(context) {

    private val scrollController = ScrollController()

    override fun createScene(messageQueue: MessageQueue): Scene {
        TouchEventFromMessageQueueRepository(messageQueue)
            .touchEvents()
            .subscribe(scrollController.touchEventsObserver)

        return DemoScene(
            renderingSettingsRepository = renderingEngine,
            lightsRenderingRepository = renderingEngine,
            textureRepository = renderingEngine,
            textureLoadingRepository = renderingEngine,
            meshRenderingRepository = renderingEngine,
            meshLoadingRepository = ObjMeshLoadingRepository(context),
            displayMetricsRepository = AndroidDisplayMetricsRepository(context),
            scrollController = scrollController,
            movementController = MovementController(
                JoystickViewJoystick(leftJoystickView),
                JoystickViewJoystick(rightJoystickView)
            ),
            timeRepository = LocalTimeRepository()
        )
    }

    override fun onCleared() {
        super.onCleared()
        scrollController.dispose()
    }
}