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
import com.kotcrab.vis.runtime.properties.PositionOwner;
import com.kotcrab.vis.runtime.properties.RotationOwner;
import com.kotcrab.vis.runtime.properties.ScaleOwner;

/**
 * Holds entities position, scale and rotation. Note that it is not guaranteed that scale and rotation will be supported
 * by all others entities type.
 * @author Kotcrab
 */
public class Transform extends Component implements PositionOwner, ScaleOwner, RotationOwner {
	private transient boolean dirty = true;
	private float x = 0, y = 0;
	private float scaleX = 1, scaleY = 1;
	private float rotation = 0;

	public Transform () {
	}

	public Transform (float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Transform (float x, float y, float scaleX, float scaleY, float rotation) {
		this.x = x;
		this.y = y;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.rotation = rotation;
	}

	@Override
	public float getX () {
		return x;
	}

	@Override
	public void setX (float x) {
		this.x = x;
		dirty = true;
	}

	@Override
	public float getY () {
		return y;
	}

	@Override
	public void setY (float y) {
		this.y = y;
		dirty = true;
	}

	@Override
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		dirty = true;
	}

	@Override
	public float getRotation () {
		return rotation;
	}

	@Override
	public void setRotation (float rotation) {
		this.rotation = rotation;
		dirty = true;
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
		dirty = true;
	}

	public boolean isDirty () {
		return dirty;
	}

	public void setDirty (boolean dirty) {
		this.dirty = dirty;
	}
}
