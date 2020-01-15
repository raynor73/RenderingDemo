package ilapin.renderingdemo.ui

import android.content.Context
import ilapin.common.android.input.TouchEventFromMessageQueueRepository
import ilapin.common.messagequeue.MessageQueue
import ilapin.engine3d.Scene
import ilapin.meshloader.android.ObjMeshLoadingRepository
import ilapin.renderingdemo.domain.DemoScene
import ilapin.renderingdemo.domain.ScrollController
import ilapin.renderingengine.android.AndroidDisplayMetricsRepository
import ilapin.renderingengine.android.BaseGLSurfaceRenderer

/**
 * @author Игорь on 15.01.2020.
 */
class GLSurfaceViewRenderer(private val context: Context) : BaseGLSurfaceRenderer(context) {

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
            scrollController = scrollController
        )
    }

    override fun onCleared() {
        super.onCleared()
        scrollController.dispose()
    }
}