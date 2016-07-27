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

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.runtime.component.PhysicsBody;
import com.kotcrab.vis.runtime.component.OriginalRotation;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisSprite;

/**
 * Updates position of {@link VisSprite} from its physics body.
 * @author Kotcrab
 */
public class PhysicsSpriteUpdateSystem extends EntityProcessingSystem {
	private ComponentMapper<PhysicsBody> physicsCm;
	private ComponentMapper<OriginalRotation> originalRotationCm;
	private ComponentMapper<Transform> transformCm;

	public PhysicsSpriteUpdateSystem () {
		super(Aspect.all(PhysicsBody.class, OriginalRotation.class, VisSprite.class));
	}

	@Override
	protected void process (Entity e) {
		PhysicsBody physics = physicsCm.get(e);
		if (physics.body == null) return;
		OriginalRotation originalRotation = originalRotationCm.get(e);
		Transform transform = transformCm.get(e);

		Vector2 bodyPos = physics.body.getPosition();
		transform.setPosition(bodyPos.x, bodyPos.y);
		transform.setRotation(originalRotation.rotation + physics.body.getAngle() * MathUtils.radiansToDegrees);
	}
}
