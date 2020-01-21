package ilapin.renderingdemo.data.scene_loader

/**
 * @author raynor on 21.01.20.
 */
class SceneDto(
    val textures: Array<TextureDto>?,
    val materials: Array<MaterialDto>?,
    val meshes: Array<MeshDto>?,
    val initialCameras: Array<String>?,
    val renderingSettings: RenderingSettingsDto?,
    val layers: Array<LayerDto>?
)