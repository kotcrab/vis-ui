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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/** @author Kotcrab */
public class VisMathUtils {
	public static Vector2 rotatePointAroundCenter (float x, float y, Vector2 target, float centerX, float centerY, float angleDeg) {
		//http://stackoverflow.com/a/12161405/1950897
		float a = angleDeg * MathUtils.degreesToRadians;
		target.x = centerX + (x - centerX) * MathUtils.cos(a) - (y - centerY) * MathUtils.sin(a);
		target.y = centerY + (x - centerX) * MathUtils.sin(a) + (y - centerY) * MathUtils.cos(a);
		return target;
	}

	/**
	 * Maps given value from one range to another using formula Y = (X-A)/(B-A) * (D-C) + C
	 * @param value that will be mapped
	 * @param fromStart beginning of original range
	 * @param frontEnd end of original range
	 * @param toStart beginning of target range
	 * @param toEnd end of target range
	 * @return mapped value
	 */
	public static float map (float value, float fromStart, float frontEnd, float toStart, float toEnd) {
		return (value - fromStart) / (frontEnd - fromStart) * (toEnd - toStart) + toStart;
	}
}
