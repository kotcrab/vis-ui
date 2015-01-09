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

package com.kotcrab.vis.ui.test;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.kotcrab.vis.ui.TableUtils;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestSplitPane extends VisWindow {

	public TestSplitPane (boolean useVisComponets) {
		super("splitpane");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		if (useVisComponets)
			addVisComponents();
		else
			addNormalComponents();

		setSize(300, 150);
		setPosition(350, 110);
	}

	private void addVisComponents () {
		VisLabel label = new VisLabel("Lorem \nipsum \ndolor \nsit \namet");
		VisLabel label2 = new VisLabel("Consectetur \nadipiscing \nelit");
		VisTable table = new VisTable(true);
		VisTable table2 = new VisTable(true);

		table.add(label);
		table2.add(label2);

		VisSplitPane splitPane = new VisSplitPane(table, table2, false);
		add(splitPane).fill().expand();
	}

	private void addNormalComponents () {
		Skin skin = VisUI.skin;
		Label label = new Label("Lorem \nipsum \ndolor \nsit \namet", skin);
		Label label2 = new Label("Consectetur \nadipiscing \nelit", skin);

		VisTable table = new VisTable(true);
		VisTable table2 = new VisTable(true);

		table.add(label);
		table2.add(label2);

		SplitPane splitPane = new SplitPane(table, table2, false, skin);
		add(splitPane).fill().expand();
	}

}
