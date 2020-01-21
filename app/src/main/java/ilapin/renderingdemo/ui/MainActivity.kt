package ilapin.renderingdemo.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.google.gson.GsonBuilder
import ilapin.common.input.TouchEvent
import ilapin.renderingdemo.R
import ilapin.renderingdemo.data.scene_loader.ComponentDeserializer
import ilapin.renderingdemo.data.scene_loader.ComponentDto
import ilapin.renderingdemo.domain.menu_controller.MenuEvent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var renderer: GLSurfaceViewRenderer? = null

    private var isFullscreen = true

    private val permanentSubscriptions = CompositeDisposable()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val gson = GsonBuilder()
                .registerTypeAdapter(ComponentDto::class.java, ComponentDeserializer())
                .setLenient()
                .create()

            renderer = GLSurfaceViewRenderer(this, gson, leftJoystickView, rightJoystickView)
            val gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {

                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    toggleFullscreen()
                    return true
                }
            })
            val glView = GLSurfaceView(this)
            glView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                renderer?.putMessage(
                    TouchEvent(
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> TouchEvent.Action.DOWN
                            MotionEvent.ACTION_MOVE -> TouchEvent.Action.MOVE
                            MotionEvent.ACTION_UP -> TouchEvent.Action.UP
                            else -> TouchEvent.Action.CANCEL
                        },
                        event.x.toInt(),
                        event.y.toInt()
                    )
                )
                true
            }
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cameraMenuItem -> {
                renderer?.putMessage(MenuEvent.SwitchCameraEvent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        if (isFullscreen) {
            hideControls()
        } else {
            showControls()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        permanentSubscriptions.clear()
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            showControls()
        } else {
            hideControls()
        }
    }

    private fun showControls() {
        isFullscreen = false
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        controlsLayout.animate().alpha(1f).start()
    }

    private fun hideControls() {
        isFullscreen = true
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        controlsLayout.animate().alpha(0f).start()
    }
}