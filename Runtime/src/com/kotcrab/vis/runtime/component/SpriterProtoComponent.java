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

/** @author Kotcrab */
public class SpriterProtoComponent extends ProtoComponent {
	public float x, y;
	public float rotation;
	public float scale;
	public boolean flipX, flipY;

	public boolean playOnStart;
	public int defaultAnimation;

	public SpriterProtoComponent () {
	}

	public SpriterProtoComponent (SpriterComponent comp) {
		x = comp.getX();
		y = comp.getY();

		scale = comp.player.getScale();

		rotation = comp.getRotation();

		flipX = comp.isFlipX();
		flipY = comp.isFlipY();

		playOnStart = comp.playOnStart;
		defaultAnimation = comp.defaultAnimation;
	}

	public void fill (SpriterComponent comp) {
		comp.setPosition(x, y);
		comp.player.setScale(scale);
		comp.setRotation(rotation);
		comp.setFlip(flipX, flipY);
		comp.onDeserialize(playOnStart, defaultAnimation);
	}
}
