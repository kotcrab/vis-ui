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

import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.MultiSplitPane;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestMultiSplitPane extends VisWindow {
	private boolean vertical = false;

	public TestMultiSplitPane () {
		super("multisplitpane");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		addVisWidgets();

		setSize(300, 150);
		centerWindow();
	}

	private void addVisWidgets () {
		VisLabel label = new VisLabel("Label #1");
		VisLabel label2 = new VisLabel("Label #2");
		VisLabel label3 = new VisLabel("Label #3");

		MultiSplitPane splitPane = new MultiSplitPane(vertical);
		splitPane.setWidgets(label, label2, label3);
		add(splitPane).fill().expand();
	}

}
