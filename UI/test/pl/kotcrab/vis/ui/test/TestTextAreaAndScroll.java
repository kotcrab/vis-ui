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

package pl.kotcrab.vis.ui.test;

import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisScrollPane;
import pl.kotcrab.vis.ui.widget.VisTextArea;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class TestTextAreaAndScroll extends VisWindow {

	public TestTextAreaAndScroll (Stage parent, boolean useVisComponets) {
		super(parent, "textarea / scrollpane");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		if (useVisComponets)
			addVisComponents();
		else
			addNormalComponents();

		setSize(180, 380);
		setPositionToCenter();
		setPosition(getX() - 380, getY());
	}

	private void addNormalComponents () {
		Skin skin = VisUI.skin;

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

	private void addVisComponents () {
		VisTextArea textArea = new VisTextArea("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec iaculis odio.");
		textArea.setPrefRows(5);

		// ---

		VisTable table = new VisTable();

		for (int i = 0; i < 20; i++)
			table.add(new VisLabel("Label #" + (i + 1))).expand().fill().row();

		VisScrollPane scrollPane = new VisScrollPane(table);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);

		// ---

		add(textArea).row();
		add(scrollPane).spaceTop(8).fillX().expandX().row();
	}
}
