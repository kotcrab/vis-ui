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

package com.kotcrab.vis.editor.util.scene2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Values returning width of provided scroll pane scroll area width. Usedful when embeding {@link GridGroup} in a
 * {@link VisTable} in a {@link VisScrollPane} with scrolling X disabled.
 * @author Kotcrab
 */
public class ScrollPaneScrollWidthValue extends Value {
	private VisScrollPane scrollPane;

	public ScrollPaneScrollWidthValue (VisScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	@Override
	public float get (Actor context) {
		return scrollPane.getScrollWidth();
	}
}
