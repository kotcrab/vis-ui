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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.spinner.*;

public class TestWindow extends VisWindow {

	public TestWindow () {
		super("test window");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		if (TestApplication.USE_VIS_WIDGETS)
			addVisWidgets();
		else
			addNormalWidgets();

		pack();
		setPosition(234, 280);
	}

	private void addVisWidgets () {
		VisLabel label = new VisLabel("label");
		final VisLabel labelWithTooltip = new VisLabel("label with tooltip");
		labelWithTooltip.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				labelWithTooltip.remove();
			}
		});
		new Tooltip.Builder("this label has a tooltip").target(labelWithTooltip).build();

		TableUtils.setSpacingDefaults(this);

		VisTable labelTable = new VisTable(true);
		labelTable.add(label);
		labelTable.add(labelWithTooltip);
		// ---

		VisTextButton normalButton = new VisTextButton("button");
		VisTextButton normalBlueButton = new VisTextButton("button blue", "blue");
		VisTextButton disabledButton = new VisTextButton("disabled");
		VisTextButton toggleButton = new VisTextButton("toggle", "toggle");
		disabledButton.setDisabled(true);

		VisTable buttonTable = new VisTable(true);
		buttonTable.add(normalButton);
		buttonTable.add(normalBlueButton);
		buttonTable.add(disabledButton);
		buttonTable.add(toggleButton);

		// ---

		VisCheckBox normalCheckbox = new VisCheckBox("checkbox");
		VisCheckBox disabledCheckbox = new VisCheckBox("disabled");
		VisCheckBox disabledCheckedCheckbox = new VisCheckBox("disabled checked");
		disabledCheckbox.setDisabled(true);
		disabledCheckedCheckbox.setDisabled(true);
		disabledCheckedCheckbox.setChecked(true);

		VisTable checkboxTable = new VisTable(true);
		checkboxTable.add(normalCheckbox);
		checkboxTable.add(disabledCheckbox);
		checkboxTable.add(disabledCheckedCheckbox);

		// ---
		VisRadioButton normalRadio = new VisRadioButton("radio");
		VisRadioButton disabledRadio = new VisRadioButton("disabled");
		VisRadioButton disabledCheckedRadio = new VisRadioButton("disabled checked");
		disabledRadio.setDisabled(true);
		disabledCheckedRadio.setDisabled(true);
		disabledCheckedRadio.setChecked(true);

		VisTable radioTable = new VisTable(true);
		radioTable.add(normalRadio);
		radioTable.add(disabledRadio);
		radioTable.add(disabledCheckedRadio);

		// ---

		VisTextField normalTextField = new VisTextField("textbox");
		VisTextField disabledTextField = new VisTextField("disabled");
		VisTextField passwordTextField = new VisTextField("password");
		VisTextField invalidTextField = new VisTextField("invalid");
		disabledTextField.setDisabled(true);
		passwordTextField.setPasswordMode(true);
		invalidTextField.setInputValid(false);

		VisTable textFieldTable = new VisTable(true);
		textFieldTable.defaults().width(120);
		textFieldTable.add(normalTextField);
		textFieldTable.add(disabledTextField);
		textFieldTable.add(passwordTextField);
		textFieldTable.add(invalidTextField);

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

		VisTable listSpinnerTable = new VisTable();
		VisList<String> list = new VisList<String>();
		list.setItems("item 1", "item 2", "item 3", "item 4");

		listSpinnerTable.add(new VisLabel("list: "));
		listSpinnerTable.add(list);

		Array<String> stringArray = new Array<String>();
		stringArray.add("a");
		stringArray.add("b");
		stringArray.add("c");
		stringArray.add("d");
		stringArray.add("e");
		final ArraySpinnerModel<String> arrayModel = new ArraySpinnerModel<String>(stringArray);
		Spinner arraySpinner = new Spinner("array", arrayModel);

		final IntSpinnerModel intModel = new IntSpinnerModel(10, -5, 20, 2);
		Spinner intSpinner = new Spinner("int", intModel);

		VisTable spinnerTable = new VisTable(true);
		spinnerTable.add(new VisLabel("spinners")).colspan(2).row();
		spinnerTable.add(intSpinner);
		spinnerTable.add(arraySpinner).row();
		spinnerTable.add(new Spinner("simple float", new SimpleFloatSpinnerModel(10f, -5f, 20f, 1.5f, 3)));
		spinnerTable.add(new Spinner("float", new FloatSpinnerModel("1", "-5", "10", "0.5", 2)));

		arraySpinner.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				System.out.println("changed array spinner to: " + arrayModel.getCurrent());
			}
		});

		intSpinner.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				System.out.println("changed int spinner to: " + intModel.getValue());
			}
		});

		listSpinnerTable.add(spinnerTable).padLeft(10f);

		// ---

		VisTable selectorsTable = new VisTable(true);
		VisSelectBox<String> selectBox = new VisSelectBox<String>();
		selectBox.setItems("item 1", "item 2", "item 3", "item 4");

		selectorsTable.add(new VisLabel("select box: "));
		selectorsTable.add(selectBox);

		// ---

		VisTable linkTable = new VisTable(true);
		linkTable.add(new VisLabel("link label:"));
		linkTable.add(new LinkLabel("https://github.com/kotcrab/vis-ui")).row();

		VisTable linkTable2 = new VisTable(true);
		linkTable2.add(new VisLabel("link label with custom text:"));
		linkTable2.add(new LinkLabel("kotcrab's blog", "http://kotcrab.com")).row();

		// ---

		add(labelTable).row();
		add(buttonTable).row();
		add(checkboxTable).row();
		add(radioTable).row();
		add(textFieldTable).row();
		add(progressbarTable).row();
		add(listSpinnerTable).row();
		add(selectorsTable).row();
		add(linkTable).row();
		add(linkTable2).padBottom(3).row();
	}

	private void addNormalWidgets () {
		Skin skin = VisUI.getSkin();

		Label label = new VisLabel("label");

		VisTable labelTable = new VisTable(true);
		labelTable.add(label);
		// ---

		TextButton normalButton = new TextButton("button", skin);
		TextButton disabledButton = new TextButton("disabled", skin);
		TextButton toggleButton = new TextButton("toggle", skin, "toggle");
		disabledButton.setDisabled(true);

		VisTable buttonTable = new VisTable(true);
		buttonTable.add(normalButton);
		buttonTable.add(disabledButton);
		buttonTable.add(toggleButton);

		// ---

		CheckBox normalCheckbox = new CheckBox(" checkbox", skin);
		CheckBox disabledCheckbox = new CheckBox(" disabled", skin);
		CheckBox disabledCheckedCheckbox = new CheckBox(" disabled checked", skin);
		disabledCheckbox.setDisabled(true);
		disabledCheckedCheckbox.setDisabled(true);
		disabledCheckedCheckbox.setChecked(true);

		VisTable checkboxTable = new VisTable(true);
		checkboxTable.add(normalCheckbox);
		checkboxTable.add(disabledCheckbox);
		checkboxTable.add(disabledCheckedCheckbox);

		// ---

		CheckBox normalRadio = new CheckBox(" radio", skin, "radio");
		CheckBox disabledRadio = new CheckBox(" disabled", skin, "radio");
		CheckBox disabledCheckedRadio = new CheckBox(" disabled checked", skin, "radio");
		disabledRadio.setDisabled(true);
		disabledCheckedRadio.setDisabled(true);
		disabledCheckedRadio.setChecked(true);

		VisTable radioTable = new VisTable(true);
		radioTable.add(normalRadio);
		radioTable.add(disabledRadio);
		radioTable.add(disabledCheckedRadio);

		// ---

		TextField normalTextField = new TextField("textbox", skin);
		TextField disabledTextField = new TextField("disabled", skin);
		TextField passwordTextField = new TextField("password", skin);
		disabledTextField.setDisabled(true);
		passwordTextField.setPasswordMode(true);

		VisTable textFieldTable = new VisTable(true);
		textFieldTable.add(normalTextField);
		textFieldTable.add(disabledTextField);
		textFieldTable.add(passwordTextField);

		// ---

		ProgressBar progressbar = new ProgressBar(0, 100, 1, false, skin);
		Slider slider = new Slider(0, 100, 1, false, skin);
		Slider sliderDisabled = new Slider(0, 100, 1, false, skin);

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
		List<String> list = new List<String>(skin);
		list.setItems("item 1", "item 2", "item 3", "item 4");

		listTable.add(new VisLabel("list: ")).top().spaceRight(10);
		listTable.add(list);

		// ---

		VisTable selectBoxTable = new VisTable();
		SelectBox<String> selectBox = new SelectBox<String>(skin);
		selectBox.setItems("item 1", "item 2", "item 3", "item 4");

		selectBoxTable.add(new VisLabel("select box: ")).top().spaceRight(6);
		selectBoxTable.add(selectBox);

		// ---

		add(labelTable).row();
		add(buttonTable).row();
		add(checkboxTable).row();
		add(radioTable).row();
		add(textFieldTable).row();
		add(progressbarTable).row();
		add(listTable).row();
		add(selectBoxTable).row();
	}

}
