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

package pl.kotcrab.vis.ui.widget;

import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class VisLabel extends Label {
	public VisLabel () {
		super("", VisUI.skin);
	}
	
	public VisLabel (CharSequence text) {
		super(text, VisUI.skin);
	}

	public VisLabel (CharSequence text, String styleName) {
		super(text, VisUI.skin, styleName);
	}

	public VisLabel (CharSequence text, String fontName, Color color) {
		super(text, VisUI.skin, fontName, color);
	}

	public VisLabel (CharSequence text, String fontName, String colorName) {
		super(text, VisUI.skin, fontName, colorName);
	}

}
