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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.*;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.plugin.spine.runtime.SpineAssetDescriptor;
import com.kotcrab.vis.plugin.spine.runtime.SpineEntity;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

public class SpineObject extends SpineEntity implements EditorObject {
	private transient boolean boundsRequested;
	private transient SpineCacheModule spineCache;

	private VisAssetDescriptor assetDescriptor;
	private boolean previewInEditor;
	private Rectangle bounds;

	public SpineObject (SpineAssetDescriptor asset, SkeletonData skeletonData, SkeletonRenderer renderer, SpineCacheModule spineCache) {
		super(null, skeletonData, renderer);
		this.spineCache = spineCache;

		bounds = new Rectangle();
		setAssetDescriptor(asset);
	}

	public SpineObject (SpineObject other) {
		super(other.getId(), other.getSkeleton().getData(), other.renderer);
		this.spineCache = other.spineCache;
		this.playOnStart = other.playOnStart;
		this.defaultAnimation = other.defaultAnimation;
		this.previewInEditor = other.previewInEditor;
		this.bounds = other.bounds;
		this.assetDescriptor = other.assetDescriptor;
		updateAnimation();
	}

	public void onDeserialize (SkeletonData skeletonData, SkeletonRenderer renderer, SpineCacheModule spineCache) {
		this.renderer = renderer;
		this.spineCache = spineCache;
		init(skeletonData);
		updateAnimation();
	}

	@Override
	public boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return assetDescriptor instanceof SpineAssetDescriptor;
	}

	@Override
	public VisAssetDescriptor getAssetDescriptor () {
		return assetDescriptor;
	}

	@Override
	public void setAssetDescriptor (VisAssetDescriptor assetDescriptor) {
		checkAssetDescriptor(assetDescriptor);
		this.assetDescriptor = assetDescriptor;
	}

	@Override
	public boolean isFlipSupported () {
		return true;
	}

	@Override
	public boolean isTintSupported () {
		return true;
	}

	@Override
	public float getWidth () {
		return bounds.width;
	}

	@Override
	public float getHeight () {
		return bounds.height;
	}

	public float getScale () {
		return ((SpineAssetDescriptor) assetDescriptor).getScale();
	}

	public void setScale (float scale) {
		SpineAssetDescriptor old = (SpineAssetDescriptor) assetDescriptor;
		setAssetDescriptor(new SpineAssetDescriptor(old.getAtlasPath(), old.getSkeletonPath(), scale));

		float x = getX(), y = getY();
		boolean flipX = isFlipX(), flipY = isFlipY();
		Color color = getColor();

		init(spineCache.get(assetDescriptor));
		updateAnimation();

		setPosition(x, y);
		setFlip(flipX, flipY);
		setColor(color);
	}

	private void updateAnimation () {
		state.clearTrack(0);
		skeleton.setToSetupPose();

		if (previewInEditor)
			state.setAnimation(0, getDefaultAnimation(), true);
	}

	public boolean isPreviewInEditor () {
		return previewInEditor;
	}

	public void setPreviewInEditor (boolean previewInEditor) {
		this.previewInEditor = previewInEditor;
		updateAnimation();
	}

	@Override
	public void setDefaultAnimation (String defaultAnimation) {
		super.setDefaultAnimation(defaultAnimation);
		updateAnimation();
	}

	@Override
	public void render (Batch batch) {
		super.render(batch);

		if (boundsRequested == true) {
			computeBoundingRectangle();
			boundsRequested = false;
		}
	}

	@Override
	public Rectangle getBoundingRectangle () {
		boundsRequested = true;
		return bounds;
	}

	private void computeBoundingRectangle () {
		Array<Slot> slots = skeleton.getSlots();
		bounds = null;

		for (Slot slot : slots) {
			Attachment attachment = slot.getAttachment();
			if (attachment == null) continue;

			float[] vertices = null;
			if (attachment instanceof BoundingBoxAttachment)
				vertices = ((BoundingBoxAttachment) attachment).getVertices();
			if (attachment instanceof RegionAttachment)
				vertices = ((RegionAttachment) attachment).getWorldVertices();
			if (attachment instanceof SkinnedMeshAttachment)
				vertices = ((SkinnedMeshAttachment) attachment).getWorldVertices();
			if (attachment instanceof MeshAttachment)
				vertices = ((MeshAttachment) attachment).getWorldVertices();

			if (vertices == null) continue;

			float minX = vertices[0];
			float minY = vertices[1];
			float maxX = vertices[0];
			float maxY = vertices[1];

			for (int i = 5; i < vertices.length; i += 5) {
				minX = minX > vertices[i] ? vertices[i] : minX;
				minY = minY > vertices[i + 1] ? vertices[i + 1] : minY;
				maxX = maxX < vertices[i] ? vertices[i] : maxX;
				maxY = maxY < vertices[i + 1] ? vertices[i + 1] : maxY;
			}

			Rectangle partBounds = new Rectangle();
			partBounds.x = minX;
			partBounds.y = minY;
			partBounds.width = maxX - minX;
			partBounds.height = maxY - minY;

			if (bounds == null)
				bounds = partBounds;
			else
				bounds.merge(partBounds);
		}
	}
}
