/*
 * Copyright 2014-2016 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.runtime.scene;

import static com.kotcrab.vis.runtime.scene.SceneFeature.*;

/**
 * Couples multiple {@link SceneFeature} into groups organized by systems role ie. rendering, physics etc.
 * @author Kotcrab
 */
public enum SceneFeatureGroup {
	/** Most crucial system usually used by many others systems. */
	ESSENTIAL(CAMERA_MANAGER,
			ENTITY_ID_MANAGER,
			GROUP_ID_MANAGER,
			LAYER_MANAGER),

	/** Physics related systems. */
	PHYSICS(PHYSICS_SYSTEM,
			PHYSICS_BODY_MANAGER,
			PHYSICS_SPRITE_UPDATE_SYSTEM),

	/** Physics related systems useful for debugging. */
	PHYSICS_DEBUG(BOX2D_DEBUG_RENDER_SYSTEM),

	/** Systems performing entity inflation process. */
	INFLATER(INFLATER_SPRITE,
			INFLATER_SOUND,
			INFLATER_MUSIC,
			INFLATER_PARTICLE,
			INFLATER_TEXT,
			INFLATER_SHADER),

	/** Systems responsible for entity rendering. */
	RENDERER(RENDER_BATCHING_SYSTEM,
			SPRITE_RENDER_SYSTEM,
			TEXT_RENDER_SYSTEM,
			PARTICLE_RENDER_SYSTEM,
			SPRITE_ANIMATION_UPDATE_SYSTEM);

	final SceneFeature[] features;

	SceneFeatureGroup (SceneFeature... features) {
		this.features = features;
	}
}
