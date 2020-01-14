package ilapin.renderingdemo.ui

import android.content.Context
import ilapin.common.messagequeue.MessageQueue
import ilapin.engine3d.Scene
import ilapin.renderingdemo.domain.DemoScene
import ilapin.renderingengine.android.BaseGLSurfaceRenderer

/**
 * @author Игорь on 15.01.2020.
 */
class GLSurfaceViewRenderer(context: Context) : BaseGLSurfaceRenderer(context) {

    override fun createScene(messageQueue: MessageQueue): Scene {
        return DemoScene()
    }
}