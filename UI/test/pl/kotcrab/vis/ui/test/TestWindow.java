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
import pl.kotcrab.vis.ui.components.VisCheckBox;
import pl.kotcrab.vis.ui.components.VisLabel;
import pl.kotcrab.vis.ui.components.VisList;
import pl.kotcrab.vis.ui.components.VisProgressBar;
import pl.kotcrab.vis.ui.components.VisRadioButton;
import pl.kotcrab.vis.ui.components.VisSlider;
import pl.kotcrab.vis.ui.components.VisTextButton;
import pl.kotcrab.vis.ui.components.VisTextField;
import pl.kotcrab.vis.ui.components.VisWindow;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class TestWindow extends VisWindow {

	public TestWindow (Stage parent) {
		super(parent, "test window");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		// TODO textarea selectbox scrollpane splitpane

		VisLabel label = new VisLabel("label");

		VisTable labelTable = new VisTable(true);
		labelTable.add(label);
		// ---

		VisTextButton normalButton = new VisTextButton("button");
		VisTextButton disabledButton = new VisTextButton("disabled");
		disabledButton.setDisabled(true);

		VisTable buttonTable = new VisTable(true);
		buttonTable.add(normalButton);
		buttonTable.add(disabledButton);

		// ---

		VisCheckBox normalCheckbox = new VisCheckBox(" checkbox");
		VisCheckBox disabledCheckbox = new VisCheckBox(" disabled");
		VisCheckBox disabledCheckedCheckbox = new VisCheckBox(" disabled checked");
		disabledCheckbox.setDisabled(true);
		disabledCheckedCheckbox.setDisabled(true);
		disabledCheckedCheckbox.setChecked(true);

		VisTable checkboxTable = new VisTable(true);
		checkboxTable.add(normalCheckbox);
		checkboxTable.add(disabledCheckbox);
		checkboxTable.add(disabledCheckedCheckbox);

		// ---
		VisRadioButton normalRadio = new VisRadioButton(" radio");
		VisRadioButton disabledRadio = new VisRadioButton(" disabled");
		VisRadioButton disabledCheckedRadio = new VisRadioButton(" disabled checked");
		disabledRadio.setDisabled(true);
		disabledCheckedRadio.setDisabled(true);
		disabledCheckedRadio.setChecked(true);

		VisTable radioTable = new VisTable(true);
		radioTable.add(normalRadio);
		radioTable.add(disabledRadio);
		radioTable.add(disabledCheckedRadio);

		// ---

		VisTextField normalTextfield = new VisTextField("textbox");
		VisTextField disabledTextfield = new VisTextField("disabled");
		VisTextField passwordTextfield = new VisTextField("password");
		disabledTextfield.setDisabled(true);
		passwordTextfield.setPasswordMode(true);

		VisTable textfieldTable = new VisTable(true);
		textfieldTable.add(normalTextfield);
		textfieldTable.add(disabledTextfield);
		textfieldTable.add(passwordTextfield);

		// ---

		VisProgressBar progressbar = new VisProgressBar(0, 100, 1, false);
		VisSlider slider = new VisSlider(0, 100, 1, false);
		VisSlider sliderDisabled = new VisSlider(0, 100, 1, false);

		progressbar.setValue(50);
		slider.setValue(50);
		sliderDisabled.setValue(50);
		sliderDisabled.setDisabled(true);

		VisTable progressbarTable = new VisTable(true);
		progressbarTable.add(progressbar);
		progressbarTable.add(slider);
		progressbarTable.add(sliderDisabled);

		// ---

		VisTable listTable = new VisTable();
		VisList<String> list = new VisList<String>();
		list.setItems("item 1", "item 2", "item 3", "item 4");

		listTable.add(new VisLabel("list: ")).top().spaceRight(10);
		listTable.add(list);
		
		// ---

		add(labelTable).row();
		add(buttonTable).row();
		add(checkboxTable).row();
		add(radioTable).row();
		add(textfieldTable).row();
		add(progressbarTable).row();
		add(listTable).row();

		pack();
		setPositionToCenter();
	}
}
