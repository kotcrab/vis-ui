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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestHighlightTextArea extends VisWindow {

	public TestHighlightTextArea () {
		super("highlight textarea");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		setResizable(true);
		addCloseButton();
		closeOnEscape();
		addVisWidgets();

		setSize(280, 380);
		centerWindow();
	}

	private void addVisWidgets () {
		HighlightTextArea textArea = new HighlightTextArea("private class Foo {\n" +
				"  int foo;\n" +
				"  float bar;\n" +
				"  String foobar;\n" +
				"}");
		Highlighter highlighter = new Highlighter();
		//it is much more reliable to use regex for keyword detection
		highlighter.regex(Color.valueOf("66CCB3"), "\\b(class|private|protected|public|if|else|void|for|while|continue|break)\\b");
		highlighter.regex(Color.valueOf("BED6FF"), "\\b(int|float|boolean|short|long|char)\\b");
		highlighter.regex(Color.valueOf("EFC090"), "\\b(foo|bar)\\b");
		highlighter.regex(Color.valueOf("75715E"), "/\\*(?:.|[\\n\\r])*?\\*/"); //block comments (/* comment */)
		textArea.setHighlighter(highlighter);
		add(textArea.createCompatibleScrollPane()).grow();
	}
}
