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

package com.kotcrab.vis.runtime.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.kotcrab.vis.runtime.component.*;

/** @author Kotcrab */
public class TextUpdateSystem extends IteratingSystem {
	private ComponentMapper<VisText> textCm;
	private ComponentMapper<VisTextChanged> changedCm;

	private ComponentMapper<VisSprite> spriteCm;
	private ComponentMapper<Transform> transformCm;
	private ComponentMapper<Origin> originCm;
	private ComponentMapper<Tint> tintCm;

	private Polygon polygon;
	private float[] polygonVerts = new float[8];

	public TextUpdateSystem () {
		super(Aspect.all(VisText.class, VisTextChanged.class));
		polygon = new Polygon(polygonVerts);
	}

	@Override
	protected void process (int entityId) {
		VisText text = textCm.get(entityId);
		VisTextChanged changed = changedCm.get(entityId);

		Transform transform = transformCm.get(entityId);
		Origin origin = originCm.get(entityId);

		if (changed.contentChanged) text.updateCache(tintCm.get(entityId).tint);

		Matrix4 translationMatrix = text.getTranslationMatrix();
		GlyphLayout layout = text.getGlyphLayout();

		if (text.isAutoSetOriginToCenter()) {
			origin.setOrigin(layout.width / 2, -layout.height / 2);
		}

		translationMatrix.idt();
		translationMatrix.translate(transform.x + origin.originX, transform.y + origin.originY, 0);
		translationMatrix.rotate(0, 0, 1, transform.rotation);
		translationMatrix.scale(transform.scaleX, transform.scaleY, 1);
		translationMatrix.translate(-origin.originX, -origin.originY, 0);
		translationMatrix.translate(0, layout.height, 0);

		//assign vertices, similar to
		//Polygon polygon = new Polygon(new float[]{0, 0,
		// 											textLayout.width, 0,
		// 											textLayout.width, textLayout.height,
		// 											0, textLayout.height});
		polygonVerts[2] = layout.width;
		polygonVerts[4] = layout.width;
		polygonVerts[5] = layout.height;
		polygonVerts[7] = layout.height;

		polygon.setPosition(transform.x, transform.y);
		polygon.setRotation(transform.rotation);
		polygon.setScale(transform.scaleX, transform.scaleY);
		polygon.setOrigin(origin.originX, origin.originY);
		text.updateBounds(polygon.getBoundingRectangle());

		changedCm.remove(entityId);
	}
}
