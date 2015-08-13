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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kotcrab.vis.runtime.util.BodyTypeEnumNameProvider;
import com.kotcrab.vis.runtime.util.autotable.ATEntityProperty;
import com.kotcrab.vis.runtime.util.autotable.ATEnumProperty;

/** @author Kotcrab */
public class PhysicsPropertiesComponent extends Component {
	@ATEnumProperty(fieldName = "Body Type", uiNameProvider = BodyTypeEnumNameProvider.class)
	public BodyType bodyType = BodyType.StaticBody;

	@ATEntityProperty(fieldName = "Friction")
	public float friction = 1;
	@ATEntityProperty(fieldName = "Density")
	public float density = 1;
	@ATEntityProperty(fieldName = "Restitution")
	public float restitution = 0;

	@ATEntityProperty(fieldName = "Sensor")
	public boolean sensor;

	@ATEntityProperty(fieldName = "Gravity Scale")
	public float gravityScale = 1;
	@ATEntityProperty(fieldName = "Linear Damping")
	public float linearDamping = 0;
	@ATEntityProperty(fieldName = "Angular Damping")
	public float angularDamping = 0;

	@ATEntityProperty(fieldName = "Bullet")
	public boolean bullet;
	@ATEntityProperty(fieldName = "Fixed Rotation")
	public boolean fixedRotation;
	@ATEntityProperty(fieldName = "Active")
	public boolean active = true;
	@ATEntityProperty(fieldName = "Sleeping Allowed")
	public boolean sleepingAllowed = true;
	@ATEntityProperty(fieldName = "Auto adjust origin")
	public boolean adjustOrigin = true;

	public short maskBits = -1;
	public short categoryBits = 0x0001;
}
