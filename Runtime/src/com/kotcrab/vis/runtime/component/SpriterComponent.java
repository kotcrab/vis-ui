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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.kotcrab.vis.runtime.accessor.BasicPropertiesAccessor;
import com.kotcrab.vis.runtime.accessor.FlipPropertiesAccessor;
import com.kotcrab.vis.runtime.accessor.RotationPropertiesAccessor;
import com.kotcrab.vis.runtime.util.UsesProtoComponent;

/** @author Kotcrab */
public class SpriterComponent extends Component implements BasicPropertiesAccessor, FlipPropertiesAccessor, RotationPropertiesAccessor, UsesProtoComponent {
	public final Loader<Sprite> loader;
	public final Player player;

	public boolean playOnStart = false;
	public int defaultAnimation = 0;

	public boolean animationPlaying;

	public SpriterComponent (Loader<Sprite> loader, Data data, float scale) {
		this.loader = loader;
		player = new Player(data.getEntity(0));
		player.setScale(scale);
		player.update();
	}

	public void onDeserialize (boolean playOnStart, int defaultAnimation) {
		this.playOnStart = playOnStart;
		this.defaultAnimation = defaultAnimation;
		player.setAnimation(defaultAnimation);
		if (playOnStart) animationPlaying = true;
	}

	@Override
	public float getX () {
		return player.getX();
	}

	@Override
	public void setX (float x) {
		player.setPosition(x, player.getY());
	}

	@Override
	public float getY () {
		return player.getY();
	}

	@Override
	public void setY (float y) {
		player.setPosition(player.getX(), y);
	}

	@Override
	public void setPosition (float x, float y) {
		player.setPosition(x, y);
	}

	@Override
	public float getWidth () {
		return player.getBoudingRectangle(null).size.width;
	}

	@Override
	public float getHeight () {
		return player.getBoudingRectangle(null).size.height;
	}

	@Override
	public Rectangle getBoundingRectangle () {
		com.brashmonkey.spriter.Rectangle rect = player.getBoundingRectangle(null);
		return new Rectangle(rect.left, rect.bottom, rect.size.width, rect.size.height);
	}

	@Override
	public boolean isFlipX () {
		return player.flippedX() != 1;
	}

	@Override
	public boolean isFlipY () {
		return player.flippedY() != 1;
	}

	@Override
	public void setFlip (boolean x, boolean y) {
		if ((x && isFlipX() == false) || (x == false && isFlipX())) player.flipX();
		if ((y && isFlipY() == false) || (y == false && isFlipY())) player.flipY();
	}

	@Override
	public ProtoComponent getProtoComponent () {
		return new SpriterProtoComponent(this);
	}

	@Override
	public float getRotation () {
		return player.getAngle();
	}

	@Override
	public void setRotation (float rotation) {
		player.setAngle(rotation);
	}
}
