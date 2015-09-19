/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.util.gdx.FloatDigitsOnlyFilter;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.*;

/**
 * Dialog used to change scene settings
 * @author Kotcrab
 */
public class SceneSettingsDialog extends VisWindow {
	private SceneTab sceneTab;
	private EditorScene scene;

	private VisValidatableTextField widthField;
	private VisValidatableTextField heightField;

	private EnumSelectBox<SceneViewport> viewportModeSelectBox;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton saveButton;

	public SceneSettingsDialog (SceneTab tab) {
		super("Scene Settings");

		this.sceneTab = tab;
		scene = tab.getScene();

		addCloseButton();
		closeOnEscape();
		setModal(true);

		createUI();
		createListeners();
		createValidators();

		pack();
		centerWindow();
	}

	private void createUI () {
		viewportModeSelectBox = new EnumSelectBox<>(SceneViewport.class);
		viewportModeSelectBox.setSelectedEnum(scene.viewport);

		//TODO error msg can't fit on window, for now we don't display it at all
		errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		row().padTop(4);

		add(new VisLabel("Viewport"));
		add(viewportModeSelectBox).expand().fill();
		row();

		widthField = new VisValidatableTextField(String.valueOf(scene.width));
		heightField = new VisValidatableTextField(String.valueOf(scene.height));
		widthField.setTextFieldFilter(new FloatDigitsOnlyFilter(false));
		heightField.setTextFieldFilter(new FloatDigitsOnlyFilter(false));

		VisTable sizeTable = new VisTable(true);
		add(new VisLabel("Width"));
		sizeTable.add(widthField).width(60);
		sizeTable.add(new VisLabel("Height"));
		sizeTable.add(heightField).width(60);

		add(sizeTable).expand().fill();
		row();

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
					DialogUtils.showOptionDialog(getStage(), "Save settings", "This will save any previous changes in scene, continue?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
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
		scene.viewport = viewportModeSelectBox.getSelectedEnum();
		scene.width = Float.valueOf(widthField.getText());
		scene.height = Float.valueOf(heightField.getText());
		sceneTab.save();
	}

	private void createValidators () {
		FormValidator validator = new FormValidator(saveButton, errorLabel);

		validator.floatNumber(widthField, "Width must be a number");
		validator.floatNumber(heightField, "Height must be a number");
		validator.valueGreaterThan(widthField, "Width must be greater than zero", 0);
		validator.valueGreaterThan(heightField, "Height must be greater than zero", 0);
	}
}
