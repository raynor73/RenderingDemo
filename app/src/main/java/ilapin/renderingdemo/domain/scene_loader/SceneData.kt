package ilapin.renderingdemo.domain.scene_loader

import ilapin.engine3d.CameraComponent
import ilapin.engine3d.GameObject

/**
 * @author raynor on 21.01.20.
 */
class SceneData(
    val rootGameObject: GameObject,
    val gameObjectsMap: Map<String, GameObject>,
    val perspectiveCamerasConfigs: Map<String, PerspectiveCameraPartialConfig>,
    val initialCameras: List<CameraComponent>
)