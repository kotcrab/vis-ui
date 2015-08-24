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
import com.kotcrab.vis.runtime.util.annotation.VisTag;
import com.kotcrab.vis.runtime.util.autotable.ATEnumProperty;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;

/** @author Kotcrab */
public class PhysicsPropertiesComponent extends Component {
	@VisTag(0) @ATEnumProperty(fieldName = "Body Type", uiNameProvider = BodyTypeEnumNameProvider.class)
	public BodyType bodyType = BodyType.StaticBody;

	@VisTag(1) @ATProperty(fieldName = "Friction")
	public float friction = 1;
	@VisTag(2) @ATProperty(fieldName = "Density")
	public float density = 1;
	@VisTag(3) @ATProperty(fieldName = "Restitution")
	public float restitution = 0;

	@VisTag(4) @ATProperty(fieldName = "Sensor")
	public boolean sensor;

	@VisTag(5) @ATProperty(fieldName = "Gravity Scale")
	public float gravityScale = 1;
	@VisTag(6) @ATProperty(fieldName = "Linear Damping")
	public float linearDamping = 0;
	@VisTag(7) @ATProperty(fieldName = "Angular Damping")
	public float angularDamping = 0;

	@VisTag(8) @ATProperty(fieldName = "Bullet")
	public boolean bullet;
	@VisTag(9) @ATProperty(fieldName = "Fixed Rotation")
	public boolean fixedRotation;
	@VisTag(10) @ATProperty(fieldName = "Active")
	public boolean active = true;
	@VisTag(11) @ATProperty(fieldName = "Sleeping Allowed")
	public boolean sleepingAllowed = true;
	@VisTag(12) @ATProperty(fieldName = "Auto Adjust Origin")
	public boolean adjustOrigin = true;

	@VisTag(13)
	public short maskBits = -1;
	@VisTag(14)
	public short categoryBits = 0x0001;
}
