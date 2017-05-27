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

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

public class TestTextAreaAndScroll extends VisWindow {

	public TestTextAreaAndScroll () {
		super("textarea / scrollpane");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		if (TestApplication.USE_VIS_WIDGETS)
			addVisWidgets();
		else
			addNormalWidgets();

		setResizable(true);
		setSize(180, 380);
		setPosition(28, 300);
	}

	private void addNormalWidgets () {
		Skin skin = VisUI.getSkin();

		TextArea textArea = new TextArea("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec iaculis odio.", skin);
		textArea.setPrefRows(5);

		// ---

		VisTable table = new VisTable();

		for (int i = 0; i < 20; i++)
			table.add(new Label("Label #" + (i + 1), skin)).expand().fill().row();

		ScrollPane scrollPane = new ScrollPane(table, skin, "list");
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);

		// ---

		add(textArea).row();
		add(scrollPane).spaceTop(8).fillX().expandX().row();
	}

	private void addVisWidgets () {
		ScrollableTextArea textArea = new ScrollableTextArea("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec iaculis odio.\nFun thing: This text area supports scrolling.");

		// ---

		VisTable table = new VisTable();

		for (int i = 0; i < 20; i++)
			table.add(new VisLabel("Label #" + (i + 1))).expand().fill().row();

		VisScrollPane scrollPane = new VisScrollPane(table);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);

		// ---

		add(textArea.createCompatibleScrollPane()).growX().height(100).row();
		add(scrollPane).spaceTop(8).growX().row();
	}
}
