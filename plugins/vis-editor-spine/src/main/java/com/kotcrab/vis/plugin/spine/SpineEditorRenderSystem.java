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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonMeshRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.kotcrab.vis.plugin.spine.components.SpineBounds;
import com.kotcrab.vis.plugin.spine.runtime.VisSpine;
import com.kotcrab.vis.runtime.component.Invisible;
import com.kotcrab.vis.runtime.component.Tint;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/** @author Kotcrab */
public class SpineEditorRenderSystem extends DeferredEntityProcessingSystem {
	private ComponentMapper<VisSpine> spineCm;
	private ComponentMapper<SpineBounds> boundsCm;
	private ComponentMapper<Transform> transformCm;
	private ComponentMapper<Tint> tintCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;

	private SkeletonMeshRenderer skeletonRenderer;

	public SpineEditorRenderSystem (EntityProcessPrincipal principal) {
		super(Aspect.all(VisSpine.class, SpineBounds.class).exclude(Invisible.class), principal);
		skeletonRenderer = new SkeletonMeshRenderer();
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		VisSpine spine = spineCm.get(entityId);
		Transform transform = transformCm.get(entityId);
		Tint tint = tintCm.get(entityId);

		spine.state.update(world.delta);
		spine.state.apply(spine.skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
		spine.skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
		skeletonRenderer.draw((PolygonSpriteBatch) batch, spine.skeleton); // Draw the skeleton images.

		if (transform.isDirty() || tint.isDirty()) {
			spine.updateValues(transform.getX(), transform.getY(), tint.getTint());
		}

		SpineBounds boundsComponent = boundsCm.get(entityId);

		if (boundsComponent.boundsRequested) {
			boundsComponent.boundsRequested = false;

			Array<Slot> slots = spineCm.get(entityId).skeleton.getSlots();
			boundsComponent.bounds = null;

			for (Slot slot : slots) {
				Attachment attachment = slot.getAttachment();
				if (attachment == null) continue;

				float[] vertices = null;
				if (attachment instanceof BoundingBoxAttachment)
					vertices = ((BoundingBoxAttachment) attachment).getVertices();
				if (attachment instanceof RegionAttachment)
					vertices = ((RegionAttachment) attachment).getWorldVertices();
				if (attachment instanceof MeshAttachment)
					vertices = ((MeshAttachment) attachment).getWorldVertices();
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

				if (boundsComponent.bounds == null)
					boundsComponent.bounds = partBounds;
				else
					boundsComponent.bounds.merge(partBounds);
			}
		}
	}
}
