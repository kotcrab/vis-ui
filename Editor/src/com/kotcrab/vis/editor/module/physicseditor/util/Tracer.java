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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.physicseditor.util.trace.TextureConverter;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Tracer {
	public static Vector2[][] trace (String path, float hullTolerance, int alphaTolerance, boolean multiPartDetection, boolean holeDetection) {
		Blending blending = Pixmap.getBlending();
		Pixmap.setBlending(Blending.None);
		Pixmap pixmap = new Pixmap(Gdx.files.absolute(path));

		int w = pixmap.getWidth();
		int h = pixmap.getHeight();

		int size = w * h;
		int[] array = new int[size];

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int color = pixmap.getPixel(x, y);
				array[x + y * w] = color;
			}
		}

		pixmap.dispose();
		Pixmap.setBlending(blending);

		Array<Array<Vector2>> outlines;
		try {
			outlines = TextureConverter.createPolygon(array, w, h, hullTolerance, alphaTolerance, multiPartDetection, holeDetection);
		} catch (Exception e) {
			return null;
		}

		TextureRegion region = new TextureRegion(new Texture(path));
		float tw = region.getRegionWidth();
		float th = region.getRegionHeight();

		Vector2[][] polygons = new Vector2[outlines.size][];

		for (int i = 0; i < outlines.size; i++) {
			Array<Vector2> outline = outlines.get(i);
			polygons[i] = new Vector2[outline.size];
			for (int ii = 0; ii < outline.size; ii++) {
				polygons[i][ii] = outline.get(ii);
				polygons[i][ii].x /= tw;
				polygons[i][ii].y /= tw;
				polygons[i][ii].y = 1 * th / tw - polygons[i][ii].y;
			}
		}

		return polygons;
	}
}
