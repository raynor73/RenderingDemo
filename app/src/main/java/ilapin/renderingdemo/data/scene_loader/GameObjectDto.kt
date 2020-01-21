package ilapin.renderingdemo.data.scene_loader

/**
 * @author raynor on 21.01.20.
 */
class GameObjectDto(
    val parent: String?,
    val name: String?,
    val position: Array<Float>?,
    val rotation: Array<Float>?,
    val scale: Array<Float>?,
    val components: Array<ComponentDto>?
)