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

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.properties.BoundsOwner;
import com.kotcrab.vis.runtime.properties.FlipOwner;
import com.kotcrab.vis.runtime.properties.SizeOwner;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.annotation.VisInternal;

/** @author Kotcrab */
public class VisSpriter extends Component implements SizeOwner, BoundsOwner, FlipOwner, UsesProtoComponent {
	private final Loader<Sprite> loader;
	private final Player player;

	private boolean playOnStart = false;
	private int defaultAnimation = 0;

	private boolean animationPlaying;

	@Deprecated
	public VisSpriter () {
		loader = null;
		player = null;
	}

	public VisSpriter (Loader<Sprite> loader, Data data, float scale) {
		this(loader, data, scale, 0);
	}

	public VisSpriter (Loader<Sprite> loader, Data data, float scale, int entityIndex) {
		if (entityIndex < 0) throw new IllegalArgumentException("entityIndex must be >= 0");
		this.loader = loader;
		player = new Player(data.getEntity(entityIndex));
		player.setScale(scale);
		player.update();
	}

	public void onDeserialize (boolean playOnStart, int defaultAnimation) {
		this.playOnStart = playOnStart;
		this.defaultAnimation = defaultAnimation;
		player.setAnimation(defaultAnimation);
		if (playOnStart) animationPlaying = true;
	}

	@VisInternal
	public void updateValues (float x, float y, float rotation) {
		player.setPosition(x, y);
		player.setAngle(rotation);
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
	public ProtoComponent<VisSpriter> toProtoComponent () {
		return new ProtoVisSpriter(this);
	}

	public Loader<Sprite> getLoader () {
		return loader;
	}

	public Player getPlayer () {
		return player;
	}

	public boolean isPlayOnStart () {
		return playOnStart;
	}

	public void setPlayOnStart (boolean playOnStart) {
		this.playOnStart = playOnStart;
	}

	public int getDefaultAnimation () {
		return defaultAnimation;
	}

	public void setDefaultAnimation (int defaultAnimation) {
		this.defaultAnimation = defaultAnimation;
	}

	public boolean isAnimationPlaying () {
		return animationPlaying;
	}

	public void setAnimationPlaying (boolean animationPlaying) {
		this.animationPlaying = animationPlaying;
	}
}
