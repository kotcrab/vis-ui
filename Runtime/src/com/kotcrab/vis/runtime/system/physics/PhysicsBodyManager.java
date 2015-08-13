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

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.kotcrab.vis.runtime.component.PhysicsComponent;
import com.kotcrab.vis.runtime.component.PhysicsPropertiesComponent;
import com.kotcrab.vis.runtime.component.PolygonComponent;
import com.kotcrab.vis.runtime.component.SpriteComponent;

/** @author Kotcrab */
@Wire
public class PhysicsBodyManager extends Manager {
	private PhysicsSystem physicsSystem;

	private ComponentMapper<PhysicsPropertiesComponent> physicsCm;
	private ComponentMapper<PolygonComponent> polygonCm;
	private ComponentMapper<SpriteComponent> spriteCm;

	private World world;

	@Override
	protected void initialize () {
		world = physicsSystem.getPhysicsWorld();
	}

	@Override
	public void added (Entity e) {
		if (physicsCm.has(e) == false || polygonCm.has(e) == false || spriteCm.has(e) == false) return;

		PhysicsPropertiesComponent physicsProperties = physicsCm.get(e);
		PolygonComponent polygon = polygonCm.get(e);
		SpriteComponent sprite = spriteCm.get(e);

		Vector2 worldPos = new Vector2(sprite.getX(), sprite.getY());

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(worldPos);

		Body body = world.createBody(bodyDef);
		body.setType(physicsProperties.bodyType);
		body.setUserData(e);

		body.setGravityScale(physicsProperties.gravityScale);
		body.setLinearDamping(physicsProperties.linearDamping);
		body.setAngularDamping(physicsProperties.angularDamping);

		body.setBullet(physicsProperties.bullet);
		body.setFixedRotation(physicsProperties.fixedRotation);
		body.setSleepingAllowed(physicsProperties.sleepingAllowed);
		body.setActive(physicsProperties.active);

		for (Vector2[] vs : polygon.faces) {
			for (Vector2 v : vs) { //polygon component stores data in world cords, we need to convert it to local cords
				v.sub(worldPos);
			}

			PolygonShape shape = new PolygonShape();
			shape.set(vs);

			FixtureDef fd = new FixtureDef();
			fd.density = physicsProperties.density;
			fd.friction = physicsProperties.friction;
			fd.restitution = physicsProperties.restitution;
			fd.isSensor = physicsProperties.sensor;
			fd.shape = shape;
			fd.filter.maskBits = physicsProperties.maskBits;
			fd.filter.categoryBits = physicsProperties.categoryBits;

			body.createFixture(fd);
			shape.dispose();
		}

		e.edit().add(new PhysicsComponent(body));
	}
}
