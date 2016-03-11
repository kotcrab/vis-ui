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

package com.kotcrab.vis.editor.module.scene.system.render;

import com.artemis.Aspect.Builder;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.editor.module.scene.CameraModule;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/** @author Kotcrab */
public abstract class AbstractIconRenderSystem extends DeferredEntityProcessingSystem {
	private CameraModule camera;
	private RenderBatchingSystem renderBatchingSystem;
	private ComponentMapper<Transform> transformCm;

	private Batch batch;

	private final float pixelsPerUnit;
	private final int iconSize;

	public AbstractIconRenderSystem (Builder aspect, EntityProcessPrincipal renderSystem, float pixelsPerUnit, int iconSize) {
		super(aspect, renderSystem);
		this.pixelsPerUnit = pixelsPerUnit;
		this.iconSize = iconSize;
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		Transform transform = transformCm.get(entityId);

		TextureRegion icon = getIconForEntity(entityId);
		float baseRenderSize = iconSize / pixelsPerUnit;

		float renderSize = baseRenderSize * camera.getZoom();
		renderSize = Math.min(renderSize, baseRenderSize);
		batch.draw(icon, transform.getX() - renderSize / 2, transform.getY() - renderSize / 2, renderSize, renderSize);
	}

	protected abstract TextureRegion getIconForEntity (int entityId);
}
