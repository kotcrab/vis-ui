/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.OrderedMap;
import com.kotcrab.vis.editor.module.scene.EditorScene;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.ui.FormValidator;
import com.kotcrab.vis.ui.OptionDialogAdapter;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.util.DialogUtils;
import com.kotcrab.vis.ui.util.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter.DigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class SceneSettingsDialog extends VisWindow {
	private SceneTab sceneTab;
	private EditorScene scene;

	private VisValidableTextField widthField;
	private VisValidableTextField heightField;

	private VisSelectBox<String> viewportModeSelectBox;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton saveButton;


	private OrderedMap<String, SceneViewport> viewportMap;

	public SceneSettingsDialog (SceneTab tab) {
		super("Scene Settings");

		this.sceneTab = tab;
		scene = tab.getScene();

		addCloseButton();
		closeOnEscape();
		setModal(true);

		viewportMap = new OrderedMap<>();
		SceneViewport[] values = SceneViewport.values();
		for (int i = 0; i < values.length; i++)
			viewportMap.put(values[i].toListString(), values[i]);

		createUI();
		createListeners();
		createValidators();

		pack();
		centerWindow();
	}

	private void createUI () {
		viewportModeSelectBox = new VisSelectBox<>();
		viewportModeSelectBox.setItems(viewportMap.keys().toArray());
		viewportModeSelectBox.setSelected(viewportMap.findKey(scene.viewport, true));

		//TODO error msg can't fit on window, for now we don't display it at all
		errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();

		row().padTop(4);

		add(new VisLabel("Viewport"));
		add(viewportModeSelectBox).expand().fill();
		row();

		widthField = new VisValidableTextField(String.valueOf(scene.width));
		heightField = new VisValidableTextField(String.valueOf(scene.height));
		widthField.setTextFieldFilter(new DigitsOnlyFilter());
		heightField.setTextFieldFilter(new DigitsOnlyFilter());

		VisTable sizeTable = new VisTable(true);
		add(new VisLabel("Width"));
		sizeTable.add(widthField).width(60);
		sizeTable.add(new VisLabel("Height"));
		sizeTable.add(heightField).width(60);

		add(sizeTable).expand().fill();
		row();

		//This wil save any change previous change in scene

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		saveButton = new VisTextButton("Save");
		saveButton.setDisabled(true);

		buttonTable.add().fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(saveButton);

		add(buttonTable).colspan(2).fill().expand();
		padBottom(5);
	}

	private void createListeners () {
		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
			}
		});

		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (sceneTab.isDirty()) {
					DialogUtils.showOptionDialog(getStage(), "Save settings", "This will save any previous change in scene, continue?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
						@Override
						public void yes () {
							setValuesToSceneAndSave();
						}
					});
				} else
					setValuesToSceneAndSave();

				fadeOut();
			}
		});
	}

	private void setValuesToSceneAndSave () {
		scene.viewport = viewportMap.get(viewportModeSelectBox.getSelected());
		scene.width = Integer.valueOf(widthField.getText());
		scene.height = Integer.valueOf(heightField.getText());
		sceneTab.save();
	}

	private void createValidators () {
		FormValidator validator = new FormValidator(saveButton, errorLabel);

		validator.integerNumber(widthField, "Width must be a number");
		validator.integerNumber(heightField, "Height must be a number");
		validator.valueGreaterThan(widthField, "Width must be greater than zero", 0);
		validator.valueGreaterThan(heightField, "Height must be greater than zero", 0);
	}
}
