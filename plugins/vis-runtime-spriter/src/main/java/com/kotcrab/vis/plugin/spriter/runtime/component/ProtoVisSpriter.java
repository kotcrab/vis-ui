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

package com.kotcrab.vis.plugin.spriter.runtime.component;

import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.properties.FlipOwner;

/** @author Kotcrab */
public class ProtoVisSpriter extends ProtoComponent<VisSpriter> implements FlipOwner { //DO NOT MOVE THIS CLASS - see RuntimeJsonTags
	public float scale;
	public boolean flipX, flipY;

	public boolean playOnStart;
	public int defaultAnimation;

	public ProtoVisSpriter () {
	}

	public ProtoVisSpriter (VisSpriter comp) {
		scale = comp.getPlayer().getScale();

		flipX = comp.isFlipX();
		flipY = comp.isFlipY();

		playOnStart = comp.isPlayOnStart();
		defaultAnimation = comp.getDefaultAnimation();
	}

	@Override
	public void fill (VisSpriter comp) {
		comp.getPlayer().setScale(scale);
		comp.setFlip(flipX, flipY);
		comp.onDeserialize(playOnStart, defaultAnimation);
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
	public void setFlip (boolean x, boolean y) {
		this.flipX = x;
		this.flipY = y;
	}
}
