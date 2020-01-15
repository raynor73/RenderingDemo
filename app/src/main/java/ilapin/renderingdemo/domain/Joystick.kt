package ilapin.renderingdemo.domain

import io.reactivex.Observable

/**
 * @author raynor on 16.01.20.
 */
interface Joystick {

    val position: Observable<Position>

    data class Position(val x: Float, val y: Float)
}