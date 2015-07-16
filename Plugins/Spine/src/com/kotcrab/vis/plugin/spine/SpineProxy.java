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

import com.artemis.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.*;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.plugin.spine.runtime.SpineComponent;
import com.kotcrab.vis.runtime.accessor.BasicPropertiesAccessor;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/** @author Kotcrab */
public class SpineProxy extends EntityProxy {

	private SpineComponent spineComponent;

	public SpineProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected BasicPropertiesAccessor initAccessors () {
		spineComponent = entity.getComponent(SpineComponent.class);
		enableColor(spineComponent);
		enableFlip(spineComponent);
		return new BasicPropertiesAccessor() {
			private Rectangle bounds;

			@Override
			public float getX () {
				return spineComponent.getX();
			}

			@Override
			public void setX (float x) {
				spineComponent.setX(x);
			}

			@Override
			public float getY () {
				return spineComponent.getY();
			}

			@Override
			public void setY (float y) {
				spineComponent.setY(y);
			}

			@Override
			public void setPosition (float x, float y) {
				spineComponent.setPosition(x, y);
			}

			@Override
			public float getWidth () {
				computeBoundingRectangle();
				return bounds.width;
			}

			@Override
			public float getHeight () {
				computeBoundingRectangle();
				return bounds.height;
			}

			@Override
			public Rectangle getBoundingRectangle () {
				computeBoundingRectangle();
				return bounds;
			}

			private void computeBoundingRectangle () {
				Array<Slot> slots = spineComponent.getSkeleton().getSlots();
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
		};
	}

	@Override
	protected String getEntityName () {
		return "SpineEntity";
	}

	@Override
	protected boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor) {
		return false;
	}
}
