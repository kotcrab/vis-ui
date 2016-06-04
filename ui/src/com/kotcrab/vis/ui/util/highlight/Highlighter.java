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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.HighlightTextArea;

/**
 * @author Kotcrab
 * @since 1.1.2
 */
public class Highlighter {
	private Array<HighlightRule> rules = new Array<HighlightRule>();

	public void addRule (HighlightRule rule) {
		rules.add(rule);
	}

	public void word (Color color, String word) {
		addRule(new WordHighlightRule(color, word));
	}

	public void word (Color color, String... words) {
		for (String word : words) {
			addRule(new WordHighlightRule(color, word));
		}
	}

	public void regex (Color color, String regex) {
		addRule(new RegexHighlightRule(color, regex));
	}

	/**
	 * @param textArea text rea
	 * @param highlights current highlights, new highlights can be added to this list however it should not be modified in any other ways
	 */
	public void process (HighlightTextArea textArea, Array<Highlight> highlights) {
		for (HighlightRule rule : rules) {
			rule.process(textArea, highlights);
		}
	}
}
