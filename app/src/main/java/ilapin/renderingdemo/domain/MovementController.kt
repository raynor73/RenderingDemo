package ilapin.renderingdemo.domain

import ilapin.common.kotlin.plusAssign
import io.reactivex.disposables.CompositeDisposable

/**
 * @author raynor on 16.01.20.
 */
class MovementController(leftJoystick: Joystick, rightJoystick: Joystick) {

    private val joystickSubscriptions = CompositeDisposable()

    private var _movingFraction = 0f
    private var _strafingFraction = 0f
    private var _horizontalSteeringFraction = 0f
    private var _verticalSteeringFraction = 0f

    init {
        joystickSubscriptions += leftJoystick.position.subscribe { position ->
            _movingFraction = -position.y
            _strafingFraction = position.x
        }

        joystickSubscriptions += rightJoystick.position.subscribe { position ->
            _horizontalSteeringFraction = -position.x
            _verticalSteeringFraction = -position.y
        }
    }

    val movingFraction: Float
        get() = _movingFraction

    val strafingFraction: Float
        get() = _strafingFraction

    val horizontalSteeringFraction: Float
        get() = _horizontalSteeringFraction

    val verticalSteeringFraction: Float
        get() = _verticalSteeringFraction

    fun deinit() {
        joystickSubscriptions.clear()
    }
}