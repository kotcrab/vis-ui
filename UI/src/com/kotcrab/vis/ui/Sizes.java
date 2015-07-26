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

package com.kotcrab.vis.ui;

/**
 * Default VisUI paddings, spacings and sizes
 * @author Kotcrab
 */
public class Sizes {
	public SizeValue spacingTop;
	public SizeValue spacingBottom;
	public SizeValue spacingRight;
	public SizeValue spacingLeft;

	public SizeValue menuItemIconSize;

	public void scale (float scaleFactor) {
		if (scaleFactor == 1) return; //no need to scale for 1 scale factor

		spacingBottom.scale(scaleFactor);
		spacingTop.scale(scaleFactor);
		spacingLeft.scale(scaleFactor);
		spacingBottom.scale(scaleFactor);

		menuItemIconSize.scale(scaleFactor);
	}

	public static class SizeValue {
		public boolean scale = true;
		public int value;

		public void scale (float factor) {
			if (scale == false) return;
			value *= factor;
		}
	}
}

