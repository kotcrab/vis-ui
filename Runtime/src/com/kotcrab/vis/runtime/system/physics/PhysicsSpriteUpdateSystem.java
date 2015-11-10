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

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.runtime.component.PhysicsComponent;
import com.kotcrab.vis.runtime.component.SpriteComponent;

/** @author Kotcrab */
public class PhysicsSpriteUpdateSystem extends EntityProcessingSystem {
	private ComponentMapper<PhysicsComponent> physicsCm;
	private ComponentMapper<SpriteComponent> spriteCm;

	public PhysicsSpriteUpdateSystem () {
		super(Aspect.all(PhysicsComponent.class, SpriteComponent.class));
	}

	@Override
	protected void process (Entity e) {
		PhysicsComponent physics = physicsCm.get(e);
		SpriteComponent sprite = spriteCm.get(e);

		Vector2 pos = physics.body.getPosition();
		sprite.setPosition(pos.x, pos.y);
		sprite.setRotation(physics.body.getAngle() * MathUtils.radiansToDegrees);
	}
}
