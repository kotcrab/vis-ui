/*
 * Spine Runtimes Software License
 * Version 2.3
 *
 * Copyright (c) 2013-2015, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to use, install, execute and perform the Spine
 * Runtimes Software (the "Software") and derivative works solely for personal
 * or internal use. Without the written permission of Esoteric Software (see
 * Section 2 of the Spine Software License Agreement), you may not (a) modify,
 * translate, adapt or otherwise create derivative works, improvements of the
 * Software or develop new applications using the Software or (b) remove,
 * delete, alter or obscure any trademarks or any copyright, trademark, patent
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.kotcrab.vis.plugin.spine.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.spine.*;
import com.kotcrab.vis.runtime.entity.Entity;

public class SpineEntity extends Entity {
	protected transient Skeleton skeleton;
	protected transient AnimationStateData stateData;
	protected transient AnimationState state;
	private transient SkeletonRenderer renderer; //TODO shared renderer

	private boolean playOnStart;
	private String defaultAnimation;

	public SpineEntity (String id, SkeletonData skeletonData) {
		super(id);

		init(skeletonData);
	}

	public SpineEntity (SpineEntity original) {
		super(original.getId());
		init(original.getSkeleton().getData());
	}

	protected void init (SkeletonData skeletonData) {
		skeleton = new Skeleton(skeletonData);

		stateData = new AnimationStateData(skeletonData);
		state = new AnimationState(stateData);

		renderer = new SkeletonRenderer();

		if (defaultAnimation == null)
			defaultAnimation = skeleton.getData().getAnimations().get(0).getName();

		if (playOnStart)
			state.setAnimation(0, defaultAnimation, true);
	}

	@Override
	public void render (Batch batch) {
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
		skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
		renderer.draw(batch, skeleton); // Draw the skeleton images.
	}

	public float getX () {
		return skeleton.getX();
	}

	public void setX (float x) {
		skeleton.setX(x);
	}

	public float getY () {
		return skeleton.getY();
	}

	public void setY (float y) {
		skeleton.setY(y);
	}

	public void setPosition (float x, float y) {
		skeleton.setPosition(x, y);
	}

	public void setFlip (boolean flipX, boolean flipY) {
		skeleton.setFlip(flipX, flipY);
	}

	public void setFlipY (boolean flipY) {
		skeleton.setFlipY(flipY);
	}

	public boolean isFlipY () {
		return skeleton.getFlipY();
	}

	public void setFlipX (boolean flipX) {
		skeleton.setFlipX(flipX);
	}

	public boolean isFlipX () {
		return skeleton.getFlipX();
	}

	public void setColor (Color color) {
		skeleton.setColor(color);
	}

	public Color getColor () {
		return skeleton.getColor();
	}

	public Skeleton getSkeleton () {
		return skeleton;
	}

	public String getDefaultAnimation () {
		return defaultAnimation;
	}

	public void setDefaultAnimation (String defaultAnimation) {
		this.defaultAnimation = defaultAnimation;
	}

	public boolean isPlayOnStart () {
		return playOnStart;
	}

	public void setPlayOnStart (boolean playOnStart) {
		this.playOnStart = playOnStart;
	}
}
