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

package com.kotcrab.vis.editor.util.polygon;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Aurelien Ribon, Kotcrab
 */
public class PolygonUtils {
	private static final int B2_MAX_VERTICES = 8;
	private static final float B2_LINEAR_SLOP = 0.005f;

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

	public static boolean isDegenerate (Vector2[][] faces) {
		for (Vector2[] vs : faces) {
			if (isDegenerate(vs))
				return true;
		}

		return false;
	}

	/** Checks whether polygon faces will make degenerated box2d polygon or not */
	public static boolean isDegenerate (Vector2[] vertices) {
		// https://github.com/libgdx/libgdx/blob/master/extensions/gdx-box2d/gdx-box2d/jni/Box2D/Collision/Shapes/b2PolygonShape.cpp#L120-L226
		// it's not like I understand what is going on here

		int count = vertices.length;
		int n = Math.min(count, B2_MAX_VERTICES);

		// Perform welding and copy vertices into local buffer.
		Vector2[] ps = new Vector2[B2_MAX_VERTICES];
		int tempCount = 0;
		for (int i = 0; i < n; ++i) {
			Vector2 v = vertices[i];

			boolean unique = true;
			for (int j = 0; j < tempCount; ++j) {
				if (distanceSquared(v, ps[j]) < ((0.5f * B2_LINEAR_SLOP) * (0.5f * B2_LINEAR_SLOP))) {
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

		// Create the convex hull using the Gift wrapping algorithm
		// http://en.wikipedia.org/wiki/Gift_wrapping_algorithm

		// Find the right most point on the hull
		int i0 = 0;
		float x0 = ps[0].x;
		for (int i = 1; i < n; ++i) {
			float x = ps[i].x;
			if (x > x0 || (x == x0 && ps[i].y < ps[i0].y)) {
				i0 = i;
				x0 = x;
			}
		}

		int hull[] = new int[B2_MAX_VERTICES];
		int m = 0;
		int ih = i0;

		while (true) {
			hull[m] = ih;

			int ie = 0;
			for (int j = 1; j < n; ++j) {
				if (ie == ih) {
					ie = j;
					continue;
				}

				Vector2 r = ps[ie].cpy().sub(ps[hull[m]]);
				Vector2 v = ps[j].cpy().sub(ps[hull[m]]);

				float c = r.crs(v);
				if (c < 0.0f) {
					ie = j;
				}

				// Collinearity check
				if (c == 0.0f && v.len2() > r.len2()) {
					ie = j;
				}
			}

			++m;
			ih = ie;

			if (ie == i0) {
				break;
			}
		}

		if (m < 3) {
			// Polygon is degenerate.
			return true;
		}

		return false;
	}

	private static float distanceSquared (Vector2 a, Vector2 b) {
		Vector2 c = a.cpy().sub(b);
		return c.dot(c);
	}
}
