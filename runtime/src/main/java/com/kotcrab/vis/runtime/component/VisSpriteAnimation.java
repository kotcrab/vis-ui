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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.runtime.system.render.SpriteAnimationUpdateSystem;
import com.kotcrab.vis.runtime.util.AnimationPlayModeEnumNameProvider;
import com.kotcrab.vis.runtime.util.autotable.ATEnumProperty;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;
import com.kotcrab.vis.runtime.util.autotable.ATUseGetterSetter;

/**
 * Entities having this component are using sprite animations. When exported from VisEditor this is used to together
 * with {@link VisSprite} and {@link SpriteAnimationUpdateSystem} to update {@link VisSprite} region according to actove
 * animation frame.
 * @author Kotcrab
 */
public class VisSpriteAnimation extends Component {
	@ATEnumProperty(fieldName = "Play Mode", uiNameProvider = AnimationPlayModeEnumNameProvider.class)
	@ATUseGetterSetter
	private Animation.PlayMode playMode = Animation.PlayMode.NORMAL;
	@ATProperty(fieldName = "Frame Duration", min = 0.00001f, tooltip = "Duration of single frame in animation")
	@ATUseGetterSetter
	private float frameDuration = 0.001f;
	@ATProperty(fieldName = "Play on start")
	private boolean playing;

	private String animationName;

	private transient Animation<TextureRegion> animation = new Animation<TextureRegion>(0);
	private transient float timer = 0;
	private transient boolean dirty = true;

	public VisSpriteAnimation () {
	}

	public VisSpriteAnimation (VisSpriteAnimation other) {
		this.playMode = other.playMode;
		this.frameDuration = other.frameDuration;
		this.playing = other.playing;
		this.animationName = other.animationName;
	}

	public String getAnimationName () {
		return animationName;
	}

	public void setAnimationName (String animationName) {
		this.animationName = animationName;
		dirty = true;
	}

	public float getFrameDuration () {
		return frameDuration;
	}

	public void setFrameDuration (float frameDuration) {
		this.frameDuration = frameDuration;
		animation.setFrameDuration(frameDuration);
	}

	public void setDirty (boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty () {
		return dirty;
	}

	public Animation.PlayMode getPlayMode () {
		return playMode;
	}

	public void setPlayMode (Animation.PlayMode playMode) {
		if (playMode == null) throw new IllegalArgumentException("playMode can't be null");
		this.playMode = playMode;
		animation.setPlayMode(playMode);
	}

	public void setAnimation (Animation animation) {
		if (animation == null) throw new IllegalArgumentException("animation can't be null");
		this.animation = animation;
		animation.setPlayMode(playMode);
		animation.setFrameDuration(frameDuration);
		dirty = true;
	}

	public void setPlaying (boolean playing) {
		this.playing = playing;
	}

	public boolean isPlaying () {
		return playing;
	}

	public void updateTimer (float delta) {
		timer += delta;
	}

	public void setTimer (float timer) {
		this.timer = timer;
	}

	public void resetTimer () {
		this.timer = 0;
	}

	public float getTimer () {
		return timer;
	}

	// Delegates

	public int getKeyFrameIndex () {
		return animation.getKeyFrameIndex(timer);
	}

	public TextureRegion getKeyFrame () {
		if (animation.getKeyFrames().length == 0) return null;
		if (timer <= 0) return animation.getKeyFrames()[0];
		return animation.getKeyFrame(timer);
	}

	public TextureRegion getKeyFrame (boolean looping) {
		if (animation.getKeyFrames().length == 0) return null;
		if (timer <= 0) return animation.getKeyFrames()[0];
		return animation.getKeyFrame(timer, looping);
	}

	public TextureRegion[] getKeyFrames () {
		return animation.getKeyFrames();
	}

	public boolean isAnimationFinished () {
		return animation.isAnimationFinished(timer);
	}

	public float getAnimationDuration () {
		return animation.getAnimationDuration();
	}
}
