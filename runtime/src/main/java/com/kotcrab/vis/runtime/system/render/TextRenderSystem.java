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

package com.kotcrab.vis.runtime.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;

/**
 * Renders entities with {@link VisText}.
 * @author Kotcrab
 */
public class TextRenderSystem extends DeferredEntityProcessingSystem {
	private static final Matrix4 IDT_MATRIX = new Matrix4();

	private ComponentMapper<VisText> textCm;
	private ComponentMapper<Transform> transformCm;
	private ComponentMapper<Origin> originCm;
	private ComponentMapper<Tint> tintCm;

	private RenderBatchingSystem renderBatchingSystem;

	private Batch batch;
	private ShaderProgram distanceFieldShader;

	private Polygon polygon;
	private float[] polygonVerts = new float[8];

	//tmp vars
	private VisText text;
	private Transform transform;
	private Tint tint;
	private Origin origin;

	public TextRenderSystem (EntityProcessPrincipal principal, ShaderProgram distanceFieldShader) {
		super(Aspect.all(VisText.class).exclude(Invisible.class), principal);
		this.distanceFieldShader = distanceFieldShader;

		polygon = new Polygon(polygonVerts);
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		text = textCm.get(entityId);
		transform = transformCm.get(entityId);
		tint = tintCm.get(entityId);
		origin = originCm.get(entityId);

		if (text.isDirty() || tint.isDirty()) {
			text.updateCache(tint.getTint());
			updateText(entityId);
		} else if (transform.isDirty() || origin.isDirty()) {
			updateText(entityId);
		}

		//TODO: optimize texts
		VisText text = textCm.get(entityId);
		batch.setTransformMatrix(text.getTranslationMatrix());
		if (text.isDistanceFieldShaderEnabled()) batch.setShader(distanceFieldShader);
		text.getCache().draw(batch);
		if (text.isDistanceFieldShaderEnabled()) batch.setShader(null);
	}

	private void updateText (int entityId) {
		Matrix4 translationMatrix = text.getTranslationMatrix();
		GlyphLayout layout = text.getGlyphLayout();

		if (text.isAutoSetOriginToCenter()) {
			origin.setOrigin(layout.width / 2, layout.height / 2);
		}

		translationMatrix.idt();
		translationMatrix.translate(transform.getX() + origin.getOriginX(), transform.getY() + origin.getOriginY(), 0);
		translationMatrix.rotate(0, 0, 1, transform.getRotation());
		translationMatrix.scale(transform.getScaleX(), transform.getScaleY(), 1);
		translationMatrix.translate(-origin.getOriginX(), -origin.getOriginY(), 0);
		translationMatrix.translate(0, layout.height, 0);

		//assign vertices, similar to: (we can skip zeros)
		//Polygon polygon = new Polygon(new float[]{0, 0,
		// 											textLayout.width, 0,
		// 											textLayout.width, textLayout.height,
		// 											0, textLayout.height});
		polygonVerts[2] = layout.width;
		polygonVerts[4] = layout.width;
		polygonVerts[5] = layout.height;
		polygonVerts[7] = layout.height;

		polygon.setPosition(transform.getX(), transform.getY());
		polygon.setRotation(transform.getRotation());
		polygon.setScale(transform.getScaleX(), transform.getScaleY());
		polygon.setOrigin(origin.getOriginX(), origin.getOriginY());
		text.updateBounds(polygon.getBoundingRectangle());
	}

	@Override
	protected void end () {
		batch.setTransformMatrix(IDT_MATRIX);

		text = null;
		transform = null;
		tint = null;
		origin = null;
	}
}
