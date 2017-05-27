/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * Utilities for converting between RGB and HSV color systems.
 * @author Kotcrab
 * @since 0.6.0
 */
public class ColorUtils {
	/**
	 * Converts HSV to RGB
	 * @param h hue 0-360
	 * @param s saturation 0-100
	 * @param v value 0-100
	 * @param alpha 0-1
	 * @return RGB values in LibGDX {@link Color} class
	 */
	public static Color HSVtoRGB (float h, float s, float v, float alpha) {
		Color c = HSVtoRGB(h, s, v);
		c.a = alpha;
		return c;
	}

	/**
	 * Converts HSV color system to RGB
	 * @param h hue 0-360
	 * @param s saturation 0-100
	 * @param v value 0-100
	 * @return RGB values in LibGDX {@link Color} class
	 */
	public static Color HSVtoRGB (float h, float s, float v) {
		Color c = new Color(1, 1, 1, 1);
		HSVtoRGB(h, s, v, c);
		return c;
	}

	/**
	 * Converts HSV color system to RGB
	 * @param h hue 0-360
	 * @param s saturation 0-100
	 * @param v value 0-100
	 * @param targetColor color that result will be stored in
	 * @return targetColor
	 */
	public static Color HSVtoRGB (float h, float s, float v, Color targetColor) {
		if (h == 360) h = 359;
		int r, g, b;
		int i;
		float f, p, q, t;
		h = (float) Math.max(0.0, Math.min(360.0, h));
		s = (float) Math.max(0.0, Math.min(100.0, s));
		v = (float) Math.max(0.0, Math.min(100.0, v));
		s /= 100;
		v /= 100;
		h /= 60;
		i = MathUtils.floor(h);
		f = h - i;
		p = v * (1 - s);
		q = v * (1 - s * f);
		t = v * (1 - s * (1 - f));
		switch (i) {
			case 0:
				r = MathUtils.round(255 * v);
				g = MathUtils.round(255 * t);
				b = MathUtils.round(255 * p);
				break;
			case 1:
				r = MathUtils.round(255 * q);
				g = MathUtils.round(255 * v);
				b = MathUtils.round(255 * p);
				break;
			case 2:
				r = MathUtils.round(255 * p);
				g = MathUtils.round(255 * v);
				b = MathUtils.round(255 * t);
				break;
			case 3:
				r = MathUtils.round(255 * p);
				g = MathUtils.round(255 * q);
				b = MathUtils.round(255 * v);
				break;
			case 4:
				r = MathUtils.round(255 * t);
				g = MathUtils.round(255 * p);
				b = MathUtils.round(255 * v);
				break;
			default:
				r = MathUtils.round(255 * v);
				g = MathUtils.round(255 * p);
				b = MathUtils.round(255 * q);
		}

		targetColor.set(r / 255.0f, g / 255.0f, b / 255.0f, targetColor.a);
		return targetColor;
	}

	/**
	 * Converts {@link Color} to HSV color system
	 * @return 3 element int array with hue (0-360), saturation (0-100) and value (0-100)
	 */
	public static int[] RGBtoHSV (Color c) {
		return RGBtoHSV(c.r, c.g, c.b);
	}

	/**
	 * Converts RGB to HSV color system
	 * @param r red 0-1
	 * @param g green 0-1
	 * @param b blue 0-1
	 * @return 3 element int array with hue (0-360), saturation (0-100) and value (0-100)
	 */
	public static int[] RGBtoHSV (float r, float g, float b) {
		float h, s, v;
		float min, max, delta;

		min = Math.min(Math.min(r, g), b);
		max = Math.max(Math.max(r, g), b);
		v = max;

		delta = max - min;

		if (max != 0)
			s = delta / max;
		else {
			s = 0;
			h = 0;
			return new int[]{MathUtils.round(h), MathUtils.round(s), MathUtils.round(v)};
		}

		if (delta == 0)
			h = 0;
		else {

			if (r == max)
				h = (g - b) / delta;
			else if (g == max)
				h = 2 + (b - r) / delta;
			else
				h = 4 + (r - g) / delta;
		}

		h *= 60;
		if (h < 0)
			h += 360;

		s *= 100;
		v *= 100;

		return new int[]{MathUtils.round(h), MathUtils.round(s), MathUtils.round(v)};
	}
}
