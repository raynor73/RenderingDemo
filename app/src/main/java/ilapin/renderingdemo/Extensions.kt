package ilapin.renderingdemo

import ilapin.engine3d.CameraComponent
import ilapin.engine3d.GameObject
import ilapin.engine3d.OrthoCameraComponent
import ilapin.engine3d.PerspectiveCameraComponent

fun GameObject.getCameraComponent(): CameraComponent? {
    return getComponent(PerspectiveCameraComponent::class.java) ?: getComponent(OrthoCameraComponent::class.java)
}