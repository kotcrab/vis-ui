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

package com.kotcrab.vis.ui.util.highlight;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.HighlightTextArea;

/**
 * Highlighter aggregates multiple {@link HighlightRule} into single collection. Highlighter is used by {@link HighlightTextArea}
 * to get information about which parts of text should be highlighted. If you need GWT compatibility, you need to use {@link BaseHighlighter}.
 * @author Kotcrab
 * @see BaseHighlighter
 * @since 1.1.2
 */
public class Highlighter extends BaseHighlighter {
	/** Adds regex based highlighter rule. */
	public void regex (Color color, String regex) {
		addRule(new RegexHighlightRule(color, regex));
	}
}
