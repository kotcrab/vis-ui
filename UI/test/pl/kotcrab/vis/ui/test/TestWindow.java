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
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.components.VisCheckBox;
import pl.kotcrab.vis.ui.components.VisTextButton;
import pl.kotcrab.vis.ui.components.VisTextField;
import pl.kotcrab.vis.ui.components.VisWindow;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class TestWindow extends VisWindow {

	public TestWindow (Stage parent) {
		super(parent, "Test window");
		Skin skin = VisUI.skin;
		setModal(true);

		VisTextField projectRoot = new VisTextField("", skin);
		TextButton chooseButton = new VisTextButton("Choose...", skin);
		VisTextField sourceLoc = new VisTextField("/core/src", skin);
		VisTextField assetsLoc = new VisTextField("/android/assets", skin);

		VisCheckBox signFiles = new VisCheckBox(" Sign files using private key", skin);

		TableUtils.setSpaceDefaults(this);

		columnDefaults(0).left();
		columnDefaults(1).width(300);

		add(new Label("Project root:", skin));
		add(projectRoot);
		add(chooseButton);
		row();

		add(new Label("Source folder:", skin));
		add(sourceLoc).fill();
		row();

		add(new Label("Assets folder:", skin));
		add(assetsLoc).fill();
		row();

		add(signFiles).colspan(2);
		row();

		Table buttonTable = new Table();
		TableUtils.setSpaceDefaults(buttonTable);

		buttonTable.defaults().minWidth(70);

		TextButton cancelButton = new VisTextButton("Cancel", skin);
		TextButton createButton = new VisTextButton("Create", skin);

		buttonTable.add(cancelButton);
		buttonTable.add(createButton);

		add(buttonTable).colspan(3).right();

		pack();
		setPositionToCenter();
	}
}
