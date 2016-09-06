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

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.esotericsoftware.spine.SkeletonMeshRenderer;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.kotcrab.vis.runtime.component.Invisible;
import com.kotcrab.vis.runtime.component.Tint;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/** @author Kotcrab */
public class SpineRenderSystem extends DeferredEntityProcessingSystem {
	private ComponentMapper<VisSpine> spineCm;
	private ComponentMapper<Transform> transformCm;
	private ComponentMapper<Tint> tintCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;

	private SkeletonRenderer skeletonRenderer;

	public SpineRenderSystem (EntityProcessPrincipal principal) {
		super(Aspect.all(VisSpine.class).exclude(Invisible.class), principal);
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
		if (batch instanceof PolygonSpriteBatch) {
			skeletonRenderer = new SkeletonMeshRenderer();
		} else {
			skeletonRenderer = new SkeletonRenderer();
		}
	}

	@Override
	protected void process (int entityId) {
		VisSpine spine = spineCm.get(entityId);
		Transform transform = transformCm.get(entityId);
		Tint tint = tintCm.get(entityId);

		if (transform.isDirty() || tint.isDirty()) {
			spine.updateValues(transform.getX(), transform.getY(), tint.getTint());
		}

		spine.state.update(world.delta);
		spine.state.apply(spine.skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
		spine.skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
		skeletonRenderer.draw(batch, spine.skeleton); // Draw the skeleton images.
	}
}
