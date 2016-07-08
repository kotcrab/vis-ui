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

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.system.*;
import com.kotcrab.vis.runtime.system.inflater.*;
import com.kotcrab.vis.runtime.system.physics.Box2dDebugRenderSystem;
import com.kotcrab.vis.runtime.system.physics.PhysicsBodyManager;
import com.kotcrab.vis.runtime.system.physics.PhysicsSpriteUpdateSystem;
import com.kotcrab.vis.runtime.system.physics.PhysicsSystem;
import com.kotcrab.vis.runtime.system.render.*;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

/**
 * Enum for all VisRuntime default systems. Useful for disabling built-in systems, see {@link SceneConfig#disable(SceneFeature)}.
 * Note that disabling essential systems may result in unexpected runtime crashes. Read each feature Javadoc before disabling it.
 * If not specified otherwise, given feature is enabled by default.
 * @author Kotcrab
 */
public enum SceneFeature {
	/** Manages camera used for scene rendering. Disabling it will result in definite crash. */
	CAMERA_MANAGER(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new CameraManager(data.viewport, data.width, data.height, data.pixelsPerUnit);
		}
	}),

	/** Allows to get entities by their ID. Safe to disable. */
	ENTITY_ID_MANAGER(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new VisIDManager();
		}
	}),

	/** Allows to get entities groups by their ID. Disabled by default. */
	GROUP_ID_MANAGER(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new VisGroupManager(data.groupIds);
		}
	}),

	/** Allows to get layers data. Essential system, disabling it will result in definite crash. */
	LAYER_MANAGER(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new LayerManager(data.layers);
		}
	}),

	/** Inflates sprites proto components into actual sprites components. Can be disabled. */
	INFLATER_SPRITE(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new SpriteInflater(context.configuration, context.assetsManager, data.textureAtlasPath);
		}
	}),

	/** Inflates sound proto components into actual sound components. Can be disabled. */
	INFLATER_SOUND(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new SoundInflater(context.configuration, context.assetsManager);
		}
	}),

	/** Inflates music proto components into actual music components. Can be disabled. */
	INFLATER_MUSIC(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new MusicInflater(context.configuration, context.assetsManager);
		}
	}),

	/** Inflates particle proto components into actual particle components. Can be disabled. */
	INFLATER_PARTICLE(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new ParticleInflater(context.configuration, context.assetsManager, data.pixelsPerUnit);
		}
	}),

	/** Inflates text proto components into actual text components. Can be disabled. */
	INFLATER_TEXT(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new TextInflater(context.configuration, context.assetsManager, data.pixelsPerUnit);
		}
	}),

	/** Inflates shader proto components into actual shader components. Can be disabled. */
	INFLATER_SHADER(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new ShaderInflater(context.assetsManager);
		}
	}),

	/** Essential physics system managing box2d world. Cannot be disabled if physics is used. */
	PHYSICS_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new PhysicsSystem(data.physicsSettings);
		}
	}),

	/** Creates physics bodies from VisEditor properties components. Can be disabled. */
	PHYSICS_BODY_MANAGER(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new PhysicsBodyManager(context.configuration);
		}
	}),

	/** Manages updating positions of VisSprites that have physics components attached. Can be disabled. */
	PHYSICS_SPRITE_UPDATE_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new PhysicsSpriteUpdateSystem();
		}
	}),

	/** Essential system for other renderers, cannot be disabled when any other renderer is used. */
	RENDER_BATCHING_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new RenderBatchingSystem(context.batch, false);
		}
	}),

	/** Renders sprites. Can be disabled. */
	SPRITE_RENDER_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new SpriteRenderSystem(config.getSystem(RenderBatchingSystem.class));
		}
	}),

	/** Renders texts. Can be disabled. */
	TEXT_RENDER_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			ShaderProgram distanceFieldShader = null;
			if (context.assetsManager.isLoaded(SceneLoader.DISTANCE_FIELD_SHADER)) {
				distanceFieldShader = context.assetsManager.get(SceneLoader.DISTANCE_FIELD_SHADER, ShaderProgram.class);
			}

			return new TextRenderSystem(config.getSystem(RenderBatchingSystem.class), distanceFieldShader);
		}
	}),

	/** Renders particles. Can be disabled. */
	PARTICLE_RENDER_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new ParticleRenderSystem(config.getSystem(RenderBatchingSystem.class), false);
		}
	}),

	/** Updates sprite animations. Can be disabled. */
	SPRITE_ANIMATION_UPDATE_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new SpriteAnimationUpdateSystem(config.getSystem(RenderBatchingSystem.class), context.assetsManager, data.pixelsPerUnit);
		}
	}),

	/**
	 * Cleans dirty flags from transform, origin and tint components. Safe to disable however may cause serious
	 * performance issues when disabled.
	 */
	DIRTY_CLEANER_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new DirtyCleanerSystem();
		}
	}),

	/** Box2d debug renderer. Renders debug outline around box2d physics bodies. Disabled by default. */
	BOX2D_DEBUG_RENDER_SYSTEM(new SystemProvider() {
		@Override
		public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
			return new Box2dDebugRenderSystem();
		}
	});

	final SystemProvider defaultProvider;

	SceneFeature (SystemProvider defaultProvider) {
		this.defaultProvider = defaultProvider;
	}
}
