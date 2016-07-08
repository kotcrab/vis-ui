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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kotcrab.vis.runtime.system.physics.PhysicsBodyManager;
import com.kotcrab.vis.runtime.util.BodyTypeEnumNameProvider;
import com.kotcrab.vis.runtime.util.autotable.ATEnumProperty;
import com.kotcrab.vis.runtime.util.autotable.ATFieldId;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;

/**
 * Stores entity physics properties. Note that after {@link PhysicsBody} is created by {@link PhysicsBodyManager} changing
 * those properties won't have any effect.
 * @author Kotcrab
 */
public class PhysicsProperties extends Component {
	@ATEnumProperty(fieldName = "Body Type", uiNameProvider = BodyTypeEnumNameProvider.class)
	public BodyType bodyType = BodyType.StaticBody;

	@ATProperty(fieldName = "Friction")
	public float friction = 1;
	@ATProperty(fieldName = "Density")
	public float density = 1;
	@ATProperty(fieldName = "Restitution")
	public float restitution = 0;

	@ATProperty(fieldName = "Sensor")
	public boolean sensor;

	@ATProperty(fieldName = "Gravity Scale")
	public float gravityScale = 1;
	@ATProperty(fieldName = "Linear Damping")
	public float linearDamping = 0;
	@ATProperty(fieldName = "Angular Damping")
	public float angularDamping = 0;

	@ATProperty(fieldName = "Bullet")
	public boolean bullet;
	@ATProperty(fieldName = "Fixed Rotation")
	public boolean fixedRotation;
	@ATProperty(fieldName = "Active")
	public boolean active = true;
	@ATProperty(fieldName = "Sleeping Allowed")
	public boolean sleepingAllowed = true;
	@ATFieldId(id = "adjustOrigin")
	@ATProperty(fieldName = "Auto Adjust Origin")
	public boolean adjustOrigin = true;

	//TODO: maskBits and categoryBits support in editor
	public short maskBits = -1;
	public short categoryBits = 0x0001;
}
