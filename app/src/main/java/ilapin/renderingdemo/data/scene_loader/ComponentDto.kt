package ilapin.renderingdemo.data.scene_loader

/**
 * @author raynor on 21.01.20.
 */
sealed class ComponentDto {
    class DirectionalLightDto(val color: String?) : ComponentDto()
    class MeshRendererDto(val meshName: String?, val matrialName: String?) : ComponentDto()
    class CameraDto(val fov: Float?, val zNear: Float?, val zFar: Float?) : ComponentDto()
}