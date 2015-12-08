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
import com.kotcrab.vis.runtime.properties.PositionOwner;
import com.kotcrab.vis.runtime.properties.RotationOwner;
import com.kotcrab.vis.runtime.properties.ScaleOwner;

/**
 * Holds entities position, scale and rotation. Note that it is not guaranteed that scale and rotation will be supported
 * by all others entities type, additionally depending on other components changes in this may not be reflected instantly and
 * you may need to add *Changed component to update cached values.
 * @author Kotcrab
 */
public class Transform extends Component implements PositionOwner, ScaleOwner, RotationOwner {
	public float x = 0, y = 0;
	public float scaleX = 1, scaleY = 1;
	public float rotation = 0;

	public Transform () {
	}

	@Override
	public float getX () {
		return x;
	}

	@Override
	public void setX (float x) {
		this.x = x;
	}

	@Override
	public float getY () {
		return y;
	}

	@Override
	public void setY (float y) {
		this.y = y;
	}

	@Override
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public float getRotation () {
		return rotation;
	}

	@Override
	public void setRotation (float rotation) {
		this.rotation = rotation;
	}

	@Override
	public float getScaleX () {
		return scaleX;
	}

	@Override
	public float getScaleY () {
		return scaleY;
	}

	@Override
	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
}
