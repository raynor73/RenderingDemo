package ilapin.renderingdemo.domain.menu_controller

sealed class MenuEvent {
    object SwitchCameraEvent : MenuEvent()
}