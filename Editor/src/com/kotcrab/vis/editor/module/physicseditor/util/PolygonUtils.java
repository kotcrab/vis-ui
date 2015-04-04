/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.physicseditor.util;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class PolygonUtils {
	public static float getPolygonSignedArea (Vector2[] points) {
		if (points.length < 3)
			return 0;

		float sum = 0;
		for (int i = 0; i < points.length; i++) {
			Vector2 p1 = points[i];
			Vector2 p2 = i != points.length - 1 ? points[i + 1] : points[0];
			sum += (p1.x * p2.y) - (p1.y * p2.x);
		}
		return 0.5f * sum;
	}

	public static float getPolygonArea (Vector2[] points) {
		return Math.abs(getPolygonSignedArea(points));
	}

	public static boolean isPolygonCCW (Vector2[] points) {
		return getPolygonSignedArea(points) > 0;
	}

	private static final int MAX_VERTICIES = 8;
	private static final float b2_linearSlop = 0.005f;

	/**
	 * Checks whether polygon vertices will make degenerated box2d polygon or not
	 * @author Kotcrab
	 */
	public static boolean isDegenerate (Vector2[] vertices) {
		//Apparently polgyon can be degenerated, so this code is copied form PolygonShape and b2PolygonShape.cpp to check whether polygon is degenerate or not

		// PolygonShape#set(Vector2[] vertices)
		float[] verts = new float[vertices.length * 2];
		for (int i = 0, j = 0; i < vertices.length * 2; i += 2, j++) {
			verts[i] = vertices[j].x;
			verts[i + 1] = vertices[j].y;
		}

		return isDegenerate(verts, 0, verts.length);
	}

	private static boolean isDegenerate (float[] verts, int offset, int length) {
		// b2PolygonShape::Set(const b2Vec2* vertices, int32 count)
		int numVertices = length / 2;
		Vector2[] verticesOut = new Vector2[numVertices];
		for (int i = 0; i < numVertices; i++) {
			verticesOut[i] = new Vector2(verts[(i << 1) + offset], verts[(i << 1) + offset + 1]);
		}

		return isDegenerate(verticesOut, numVertices);
	}

	private static boolean isDegenerate (Vector2[] verts, int count) {
		// https://github.com/libgdx/libgdx/blob/master/extensions/gdx-box2d/gdx-box2d/jni/Box2D/Collision/Shapes/b2PolygonShape.cpp#L120-L161
		int n = Math.min(count, MAX_VERTICIES);

		Vector2[] ps = new Vector2[MAX_VERTICIES];
		int tempCount = 0;

		for (int i = 0; i < n; ++i) {
			Vector2 v = verts[i];

			boolean unique = true;

			for (int j = 0; j < tempCount; ++j) {
				if (distanceSquared(v, ps[j]) < 0.5f * b2_linearSlop) {
					unique = false;
					break;
				}
			}

			if (unique) {
				ps[tempCount++] = v;
			}
		}

		n = tempCount;
		if (n < 3) {
			// Polygon is degenerate.
			return true;
		}

		return false;
	}

	private static float distanceSquared (Vector2 a, Vector2 b) {
		a.sub(b);
		return a.dot(a);
	}
}
