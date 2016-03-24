package com.kotcrab.vis.editor.module.scene;

import com.kotcrab.vis.editor.scene.EditorScene;

/** @author Kotcrab */
public class SceneAccessModule extends SceneModule {
	public EditorScene getScene () {
		return sceneContainer.getScene();
	}
}
