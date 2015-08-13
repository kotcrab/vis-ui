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
	public float friction;
	@ATEntityProperty(fieldName = "Density")
	public float density;
	@ATEntityProperty(fieldName = "Restitution")
	public float restitution;

	@ATEntityProperty(fieldName = "Mass")
	public float mass;
	@ATEntityProperty(fieldName = "Gravity Scale")
	public float gravityScale;

	@ATEntityProperty(fieldName = "Linear Damping")
	public float linearDamping;
	@ATEntityProperty(fieldName = "Angular Damping")
	public float angularDamping;

	@ATEntityProperty(fieldName = "Mask")
	public int mask;
	@ATEntityProperty(fieldName = "Category")
	public int category;

	@ATEntityProperty(fieldName = "Bullet")
	public boolean bullet;
	@ATEntityProperty(fieldName = "Sensor")
	public boolean sensor;

	@ATEntityProperty(fieldName = "Fixed Rotation")
	public boolean fixedRotation;
	@ATEntityProperty(fieldName = "Active")
	public boolean active;
}
