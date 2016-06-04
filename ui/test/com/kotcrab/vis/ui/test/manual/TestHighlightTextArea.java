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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestHighlightTextArea extends VisWindow {

	public TestHighlightTextArea () {
		super("highlight textarea");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		setResizable(true);
		addVisWidgets();

		setSize(280, 380);
		setPosition(28, 300);
	}

	private void addVisWidgets () {
		HighlightTextArea textArea = new HighlightTextArea("private class Foo {\n" +
				"  int foo;\n" +
				"  float bar;\n" +
				"  String foobar;\n" +
				"}");

		Highlighter highlighter = new Highlighter();
		highlighter.word(Color.valueOf("66CCB3"), "class", "private", "public", "protected", "if", "else", "void");
		highlighter.word(Color.valueOf("BED6FF"), "int", "float", "boolean");
		highlighter.word(Color.valueOf("EFC090"), "foo", "bar");
		highlighter.regex(Color.valueOf("75715E"), "/\\*(?:.|[\\n\\r])*?\\*/"); //block comments
		textArea.setHighlighter(highlighter);

		VisScrollPane scrollPane = new VisScrollPane(textArea);
		scrollPane.setOverscroll(false, false);
		scrollPane.setFlickScroll(false);
		scrollPane.setScrollbarsOnTop(true);
//		scrollPane.setScrollingDisabled(false, false);

		add(scrollPane).grow();
	}
}
