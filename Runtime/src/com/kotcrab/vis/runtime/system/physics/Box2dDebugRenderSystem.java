/*
 * Copyright 2014-2015 See AUTHORS file.
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
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.kotcrab.vis.runtime.system.CameraManager;

/** @author Kotcrab */
@Wire
public class Box2dDebugRenderSystem extends BaseSystem {
	private CameraManager cameraManager;
	private PhysicsSystem physicsSystem;

	private Box2DDebugRenderer debugRenderer;

	public Box2dDebugRenderSystem () {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				debugRenderer = new Box2DDebugRenderer();
			}
		});
	}

	@Override
	protected void processSystem () {
		if(debugRenderer != null) debugRenderer.render(physicsSystem.getPhysicsWorld(), cameraManager.getCombined());
	}
}
