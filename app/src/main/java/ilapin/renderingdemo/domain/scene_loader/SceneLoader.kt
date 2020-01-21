package ilapin.renderingdemo.domain.scene_loader

import ilapin.renderingdemo.domain.scene_loader.SceneGameObjects

/**
 * @author raynor on 21.01.20.
 */
interface SceneLoader {

    fun loadMesh(path: String): SceneGameObjects
}