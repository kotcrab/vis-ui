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

package com.kotcrab.vis.runtime.component.proto;

import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.properties.FlipOwner;
import com.kotcrab.vis.runtime.properties.Resizable;
import com.kotcrab.vis.runtime.properties.SizeOwner;

/**
 * {@link ProtoComponent} for {@link VisSprite}.
 * @author Kotcrab
 */
public class ProtoVisSprite extends ProtoComponent<VisSprite> implements FlipOwner, Resizable, SizeOwner {
	public boolean flipX, flipY;
	public float width, height;

	public ProtoVisSprite () {
	}

	public ProtoVisSprite (VisSprite sprite) {
		this.flipX = sprite.isFlipX();
		this.flipY = sprite.isFlipY();
		this.width = sprite.getWidth();
		this.height = sprite.getHeight();
	}

	@Override
	public void fill (VisSprite target) {
		target.setFlip(flipX, flipY);
		target.setSize(width, height);
	}

	@Override
	public boolean isFlipX () {
		return flipX;
	}

	@Override
	public boolean isFlipY () {
		return flipY;
	}

	@Override
	public void setFlip (boolean flipX, boolean flipY) {
		this.flipX = flipX;
		this.flipY = flipY;
	}

	@Override
	public float getWidth () {
		return width;
	}

	@Override
	public float getHeight () {
		return height;
	}

	@Override
	public void setSize (float width, float height) {
		this.width = width;
		this.height = height;
	}
}
