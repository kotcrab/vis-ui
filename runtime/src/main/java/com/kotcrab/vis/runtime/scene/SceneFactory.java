package com.kotcrab.vis.runtime.scene;

import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

/**
 * You can set your descendant of this class in {@link SceneLoader} for providing custom way of Scene creation.
 *
 * @author dpotenko
 */
public class SceneFactory {

	protected Scene createScene(EntityEngineConfiguration engineConfiguration, SceneData sceneData) {
		return new Scene(engineConfiguration, sceneData);
	}
}
