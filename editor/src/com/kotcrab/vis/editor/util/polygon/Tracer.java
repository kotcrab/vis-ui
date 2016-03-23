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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.polygon.trace.TextureConverter;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 * @author Kotcrab
 */
public class Tracer {
	public static Vector2[][] trace (int[] pixelArray, int width, int height, float hullTolerance, int alphaTolerance) {
		Array<Array<Vector2>> outlines;
		try {
			//multipart and hole detection are not supported by editor (one polygon by entity)
			outlines = TextureConverter.createPolygon(pixelArray, width, height, hullTolerance, alphaTolerance, false, false);
		} catch (Exception e) {
			Log.error("Polygonizer tracer error occurred");
			Log.exception(e);
			return null;
		}

		Vector2[][] polygons = new Vector2[outlines.size][];

		for (int i = 0; i < outlines.size; i++) {
			Array<Vector2> outline = outlines.get(i);
			polygons[i] = new Vector2[outline.size];
			for (int ii = 0; ii < outline.size; ii++) {
				polygons[i][ii] = outline.get(ii);
				polygons[i][ii].x /= width;
				polygons[i][ii].y /= width;
				polygons[i][ii].y = 1 * height / width - polygons[i][ii].y;
			}
		}

		return polygons;
	}
}
