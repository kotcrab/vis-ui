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

package com.kotcrab.vis.editor.proxy;

import com.artemis.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.runtime.component.Origin;
import com.kotcrab.vis.runtime.component.Tint;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.properties.BoundsOwner;

/** @author Kotcrab */
public class SpriteProxy extends EntityProxy {
	private Accessor accessor;
	private Transform transform;
	private Origin origin;
	private VisSprite sprite;

	public SpriteProxy (Entity entity) {
		super(entity);
	}

	@Override
	protected void createAccessors () {
		accessor = new Accessor();
	}

	@Override
	protected void reloadAccessors () {
		sprite = getComponent(VisSprite.class);
		transform = getComponent(Transform.class);
		origin = getComponent(Origin.class);
		Tint tint = getComponent(Tint.class);

		enableBasicProperties(transform, sprite, accessor);
		enableOrigin(origin);
		enableScale(transform);
		enableTint(tint);
		enableRotation(transform);
		enableFlip(sprite);
	}

	@Override
	public String getEntityName () {
		return "Sprite";
	}

	private class Accessor implements BoundsOwner {
		private static final int X1 = 0;
		private static final int X2 = 1;
		private static final int X3 = 2;
		private static final int X4 = 3;
		private static final int Y1 = 4;
		private static final int Y2 = 5;
		private static final int Y3 = 6;
		private static final int Y4 = 7;

		private float[] vertices = new float[8];
		private Rectangle bounds = new Rectangle();

		@Override
		public Rectangle getBoundingRectangle () {
			calcBounds();
			return bounds;
		}

		public Rectangle calcBounds () {
			final float[] vertices = getVertices();

			float minx = vertices[X1];
			float miny = vertices[Y1];
			float maxx = vertices[X1];
			float maxy = vertices[Y1];

			minx = minx > vertices[X2] ? vertices[X2] : minx;
			minx = minx > vertices[X3] ? vertices[X3] : minx;
			minx = minx > vertices[X4] ? vertices[X4] : minx;

			maxx = maxx < vertices[X2] ? vertices[X2] : maxx;
			maxx = maxx < vertices[X3] ? vertices[X3] : maxx;
			maxx = maxx < vertices[X4] ? vertices[X4] : maxx;

			miny = miny > vertices[Y2] ? vertices[Y2] : miny;
			miny = miny > vertices[Y3] ? vertices[Y3] : miny;
			miny = miny > vertices[Y4] ? vertices[Y4] : miny;

			maxy = maxy < vertices[Y2] ? vertices[Y2] : maxy;
			maxy = maxy < vertices[Y3] ? vertices[Y3] : maxy;
			maxy = maxy < vertices[Y4] ? vertices[Y4] : maxy;

			bounds.x = minx;
			bounds.y = miny;
			bounds.width = maxx - minx;
			bounds.height = maxy - miny;
			return bounds;
		}

		private float[] getVertices () {
			float[] vertices = this.vertices;
			float localX = -origin.getOriginX();
			float localY = -origin.getOriginY();
			float localX2 = localX + sprite.getWidth();
			float localY2 = localY + sprite.getHeight();
			float worldOriginX = transform.getX() - localX;
			float worldOriginY = transform.getY() - localY;
			if (transform.getScaleX() != 1 || transform.getScaleY() != 1) {
				localX *= transform.getScaleX();
				localY *= transform.getScaleY();
				localX2 *= transform.getScaleX();
				localY2 *= transform.getScaleY();
			}
			if (transform.getRotation() != 0) {
				final float cos = MathUtils.cosDeg(transform.getRotation());
				final float sin = MathUtils.sinDeg(transform.getRotation());
				final float localXCos = localX * cos;
				final float localXSin = localX * sin;
				final float localYCos = localY * cos;
				final float localYSin = localY * sin;
				final float localX2Cos = localX2 * cos;
				final float localX2Sin = localX2 * sin;
				final float localY2Cos = localY2 * cos;
				final float localY2Sin = localY2 * sin;

				final float x1 = localXCos - localYSin + worldOriginX;
				final float y1 = localYCos + localXSin + worldOriginY;
				vertices[X1] = x1;
				vertices[Y1] = y1;

				final float x2 = localXCos - localY2Sin + worldOriginX;
				final float y2 = localY2Cos + localXSin + worldOriginY;
				vertices[X2] = x2;
				vertices[Y2] = y2;

				final float x3 = localX2Cos - localY2Sin + worldOriginX;
				final float y3 = localY2Cos + localX2Sin + worldOriginY;
				vertices[X3] = x3;
				vertices[Y3] = y3;

				vertices[X4] = x1 + (x3 - x2);
				vertices[Y4] = y3 - (y2 - y1);
			} else {
				final float x1 = localX + worldOriginX;
				final float y1 = localY + worldOriginY;
				final float x2 = localX2 + worldOriginX;
				final float y2 = localY2 + worldOriginY;

				vertices[X1] = x1;
				vertices[Y1] = y1;

				vertices[X2] = x1;
				vertices[Y2] = y2;

				vertices[X3] = x2;
				vertices[Y3] = y2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
			}

			return vertices;
		}
	}
}
