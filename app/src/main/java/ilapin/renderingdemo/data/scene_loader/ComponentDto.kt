package ilapin.renderingdemo.data.scene_loader

/**
 * @author raynor on 21.01.20.
 */
sealed class ComponentDto {
    class DirectionalLightDto(val color: Array<Float>?, val cameraNames: Array<String>?) : ComponentDto()
    class MeshDto(val meshName: String?, val materialName: String?, val cameraNames: Array<String>?) : ComponentDto()
    class PerspectiveCameraDto(val fov: Float?, val zNear: Float?, val zFar: Float?) : ComponentDto()
}