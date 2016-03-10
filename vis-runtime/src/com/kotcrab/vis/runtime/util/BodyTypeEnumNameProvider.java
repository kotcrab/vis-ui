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

package com.kotcrab.vis.runtime.util;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kotcrab.vis.runtime.util.autotable.EnumNameProvider;

/** @author Kotcrab */
public class BodyTypeEnumNameProvider implements EnumNameProvider<BodyType> {
	@Override
	public String getPrettyName (BodyType value) {
		switch (value) {
			case StaticBody:
				return "Static";
			case KinematicBody:
				return "Kinematic";
			case DynamicBody:
				return "Dynamic";
		}

		throw new IllegalStateException("Missing enum case for: " + value);
	}
}
