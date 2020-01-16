package ilapin.renderingdemo.data

import ilapin.common.android.ui.widgets.joystick_view.JoystickView
import ilapin.renderingdemo.domain.Joystick
import io.reactivex.Observable
import kotlin.math.abs

/**
 * @author raynor on 16.01.20.
 */
class JoystickViewJoystick(joystickView: JoystickView) : Joystick {

    override val position: Observable<Joystick.Position> = joystickView.positionObservable.map {
        val x = if (abs(it.x) > 0.01) {
            it.x
        } else {
            0f
        }
        val y = if (abs(it.y) > 0.01) {
            it.y
        } else {
            0f
        }
        Joystick.Position(x, y)
    }
}