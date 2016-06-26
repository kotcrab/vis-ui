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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.kotcrab.vis.ui.VisUI;

/**
 * Compatible with {@link ScrollPane}. Does not provide additional features.
 * @author Kotcrab
 * @see ScrollPane
 */
public class VisScrollPane extends ScrollPane {
	public VisScrollPane (Actor widget, ScrollPaneStyle style) {
		super(widget, style);
	}

	public VisScrollPane (Actor widget, String styleName) {
		super(widget, VisUI.getSkin(), styleName);
	}

	public VisScrollPane (Actor widget) {
		super(widget, VisUI.getSkin(), "list");
	}
}
