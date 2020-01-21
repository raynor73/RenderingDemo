package ilapin.renderingdemo.ui

import android.content.Context
import com.google.gson.Gson
import ilapin.common.android.input.TouchEventFromMessageQueueRepository
import ilapin.common.android.time.LocalTimeRepository
import ilapin.common.android.ui.widgets.joystick_view.JoystickView
import ilapin.common.messagequeue.MessageQueue
import ilapin.engine3d.Scene
import ilapin.meshloader.android.ObjMeshLoadingRepository
import ilapin.renderingdemo.data.JoystickViewJoystick
import ilapin.renderingdemo.data.scene_loader.AndroidAssetsSceneLoader
import ilapin.renderingdemo.domain.DemoScene
import ilapin.renderingdemo.domain.MovementController
import ilapin.renderingdemo.domain.menu_controller.MenuEvent
import ilapin.renderingdemo.domain.scroll_controller.ScrollController
import ilapin.renderingengine.android.AndroidDisplayMetricsRepository
import ilapin.renderingengine.android.BaseGLSurfaceRenderer
import io.reactivex.Observable

/**
 * @author Игорь on 15.01.2020.
 */
class GLSurfaceViewRenderer(
    private val context: Context,
    private val gson: Gson,
    private val leftJoystickView: JoystickView,
    private val rightJoystickView: JoystickView
) : BaseGLSurfaceRenderer(context) {

    private val scrollController = ScrollController()

    override fun createScene(messageQueue: MessageQueue): Scene {
        TouchEventFromMessageQueueRepository(messageQueue)
            .touchEvents()
            .subscribe(scrollController.touchEventsObserver)

        return DemoScene(
            sceneLoader = AndroidAssetsSceneLoader(
                context = context,
                gson = gson,
                meshLoadingRepository = ObjMeshLoadingRepository(context),
                meshRenderingRepository = renderingEngine,
                lightsRenderingRepository = renderingEngine,
                textureLoadingRepository = renderingEngine,
                renderingSettingsRepository = renderingEngine
            ),
            displayMetricsRepository = AndroidDisplayMetricsRepository(context),
            scrollController = scrollController,
            menuEvent = messageQueue.messages().flatMap {
                if (it is MenuEvent) {
                    Observable.just(it)
                } else {
                    Observable.empty()
                }
            },
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