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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.kotcrab.vis.ui.VisUI;

/**
 * Compatible with {@link VisProgressBar}. Does not provide additional features.
 * @author Kotcrab
 * @see ProgressBar
 */
public class VisProgressBar extends ProgressBar {
	public VisProgressBar (float min, float max, float stepSize, boolean vertical) {
		this(min, max, stepSize, vertical, VisUI.getSkin().get("default-" + (vertical ? "vertical" : "horizontal"),
				ProgressBarStyle.class));
	}

	public VisProgressBar (float min, float max, float stepSize, boolean vertical, String styleName) {
		this(min, max, stepSize, vertical, VisUI.getSkin().get(styleName, ProgressBarStyle.class));
	}

	public VisProgressBar (float min, float max, float stepSize, boolean vertical, ProgressBarStyle style) {
		super(min, max, stepSize, vertical, style);
	}
}
