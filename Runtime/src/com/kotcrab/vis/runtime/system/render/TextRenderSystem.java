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

package com.kotcrab.vis.runtime.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.kotcrab.vis.runtime.component.InvisibleComponent;
import com.kotcrab.vis.runtime.component.TextComponent;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;

/**
 * Renders entities with {@link TextComponent}
 * @author Kotcrab
 */
public class TextRenderSystem extends DeferredEntityProcessingSystem {
	private static final Matrix4 IDT_MATRIX = new Matrix4();

	private ComponentMapper<TextComponent> textCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;
	private ShaderProgram distanceFieldShader;

	public TextRenderSystem (EntityProcessPrincipal principal, ShaderProgram distanceFieldShader) {
		super(Aspect.all(TextComponent.class).exclude(InvisibleComponent.class), principal);
		this.distanceFieldShader = distanceFieldShader;
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		//TODO: optimize texts
		TextComponent text = textCm.get(entityId);
		batch.setTransformMatrix(text.translationMatrix);
		if (text.isDistanceFieldShaderEnabled()) batch.setShader(distanceFieldShader);
		text.getCache().draw(batch);
		if (text.isDistanceFieldShaderEnabled()) batch.setShader(null);
	}

	@Override
	protected void end () {
		batch.setTransformMatrix(IDT_MATRIX);
	}
}
