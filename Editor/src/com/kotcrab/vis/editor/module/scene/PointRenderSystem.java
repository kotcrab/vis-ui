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

package com.kotcrab.vis.editor.module.scene;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.runtime.component.InvisibleComponent;
import com.kotcrab.vis.runtime.component.PointComponent;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/** @author Kotcrab */
public class PointRenderSystem extends DeferredEntityProcessingSystem {
	public static final int ICON_SIZE = 76;

	private CameraModule camera;

	private ComponentMapper<PointComponent> pointCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;

	private TextureRegion icon;
	private float baseRenderSize;

	public PointRenderSystem (EntityProcessPrincipal principal, float pixelsPerUnit) {
		super(Aspect.all(PointComponent.class).exclude(InvisibleComponent.class), principal);
		icon = Assets.getIconRegion(Icons.POINT);

		baseRenderSize = ICON_SIZE / pixelsPerUnit;
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		PointComponent pos = pointCm.get(entityId);

		float renderSize = baseRenderSize * camera.getZoom();
		renderSize = Math.min(renderSize, baseRenderSize);
		batch.draw(icon, pos.x - renderSize / 2, pos.y - renderSize / 2, renderSize, renderSize);
	}
}
