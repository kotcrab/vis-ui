/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.ui.components;

import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

public class VisProgressBar extends ProgressBar {

	public VisProgressBar (float min, float max, float stepSize, boolean vertical, String styleName) {
		super(min, max, stepSize, vertical, VisUI.skin, styleName);
	}

	public VisProgressBar (float min, float max, float stepSize, boolean vertical) {
		super(min, max, stepSize, vertical, VisUI.skin);
	}

	public VisProgressBar (float min, float max, float stepSize, boolean vertical, ProgressBarStyle style) {
		super(min, max, stepSize, vertical, style);
	}

}
