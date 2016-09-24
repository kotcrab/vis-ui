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

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.properties.FlipOwner;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.annotation.VisInternal;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;

/** @author Kotcrab */
public class VisSpine extends Component implements FlipOwner, UsesProtoComponent {
	public transient Skeleton skeleton;
	public transient AnimationStateData stateData;
	public transient AnimationState state;

	@ATProperty(fieldName = "Play animation on start")
	public boolean playOnStart;
	public String defaultAnimation;

	@Deprecated
	public VisSpine () {
	}

	public VisSpine (SkeletonData skeletonData) {
		init(skeletonData);
	}

	public VisSpine (VisSpine other, SkeletonData skeletonData) {
		this.playOnStart = other.playOnStart;
		this.defaultAnimation = other.defaultAnimation;

		init(skeletonData);

		setFlip(other.isFlipX(), other.isFlipY());
	}

	private void init (SkeletonData skeletonData) {
		skeleton = new Skeleton(skeletonData);

		stateData = new AnimationStateData(skeletonData);
		state = new AnimationState(stateData);

		defaultAnimation = skeleton.getData().getAnimations().get(0).getName();
	}

	public void onDeserialize (SkeletonData skeletonData) {
		init(skeletonData);
		if (playOnStart) {
			state.setAnimation(0, defaultAnimation, true);
		}
	}

	@VisInternal
	public void updateValues (float x, float y, Color color) {
		skeleton.setPosition(x, y);
		skeleton.setColor(color);
	}

	@VisInternal
	public void updateDefaultAnimations () {
		if (defaultAnimation == null)
			defaultAnimation = skeleton.getData().getAnimations().get(0).getName();

		if (playOnStart)
			state.setAnimation(0, defaultAnimation, true);
	}

	@Override
	public void setFlip (boolean flipX, boolean flipY) {
		skeleton.setFlip(flipX, flipY);
	}

	@Override
	public boolean isFlipY () {
		return skeleton.getFlipY();
	}

	@Override
	public boolean isFlipX () {
		return skeleton.getFlipX();
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

	@Override
	public ProtoComponent<VisSpine> toProtoComponent () {
		return new ProtoVisSpine(this);
	}
}
