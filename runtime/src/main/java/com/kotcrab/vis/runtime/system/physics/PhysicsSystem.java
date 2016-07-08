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

package com.kotcrab.vis.runtime.system.physics;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.kotcrab.vis.runtime.data.PhysicsSettings;

/**
 * Responsible for creating and updating physics world.
 * @author Kotcrab
 */
public class PhysicsSystem extends BaseSystem {
	private static final float TIME_STEP = 1f / 60f;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;

	private World box2dWorld;
	private float timeAccumulator;

	public PhysicsSystem (PhysicsSettings physicsSettings) {
		box2dWorld = new World(new Vector2(physicsSettings.gravityX, physicsSettings.gravityY), physicsSettings.allowSleep);
	}

	@Override
	protected void processSystem () {
		float frameTime = Math.min(world.delta, 0.25f);
		timeAccumulator += frameTime;
		while (timeAccumulator >= TIME_STEP) {
			box2dWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			timeAccumulator -= TIME_STEP;
		}
	}

	@Override
	protected void dispose () {
		box2dWorld.dispose();
	}

	public World getPhysicsWorld () {
		return box2dWorld;
	}
}
