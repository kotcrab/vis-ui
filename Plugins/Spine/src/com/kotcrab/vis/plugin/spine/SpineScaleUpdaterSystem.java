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

package com.kotcrab.vis.plugin.spine;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.plugin.spine.runtime.SpineAssetDescriptor;
import com.kotcrab.vis.plugin.spine.runtime.SpineComponent;
import com.kotcrab.vis.runtime.component.AssetReference;

/** @author Kotcrab */
public class SpineScaleUpdaterSystem extends EntityProcessingSystem {
	private SpineCacheModule spineCache;

	private ComponentMapper<SpineComponent> spineCm;
	private ComponentMapper<AssetReference> assetCm;
	private ComponentMapper<SpineScaleComponent> scaleCm;
	private ComponentMapper<SpinePreviewComponent> previewCm;

	public SpineScaleUpdaterSystem () {
		super(Aspect.all(SpineComponent.class, SpineScaleComponent.class, AssetReference.class));
	}

	@Override
	protected void process (Entity e) {
		SpineScaleComponent scaleComponent = scaleCm.get(e);

		if (scaleComponent.updateScale) {
			scaleComponent.updateScale = false;

			SpineComponent spineComponent = spineCm.get(e);
			AssetReference assetRef = assetCm.get(e);
			SpinePreviewComponent previewComponent = previewCm.get(e);

			SpineAssetDescriptor old = (SpineAssetDescriptor) assetRef.asset;
			assetRef.asset = new SpineAssetDescriptor(old.getAtlasPath(), old.getSkeletonPath(), scaleComponent.scale);

			float x = spineComponent.getX(), y = spineComponent.getY();
			boolean flipX = spineComponent.isFlipX(), flipY = spineComponent.isFlipY();
			Color color = spineComponent.getTint();

			spineComponent.onDeserialize(spineCache.get(assetRef.asset));
			previewComponent.updateAnimation = true;

			spineComponent.setPosition(x, y);
			spineComponent.setFlip(flipX, flipY);
			spineComponent.setTint(color);
		}
	}
}
