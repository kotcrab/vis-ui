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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.SceneCacheModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.util.vis.PrettyEnumNameProvider;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormInputValidator;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter.DigitsOnlyFilter;

/**
 * Dialog used when user is creating new scene
 * @author Kotcrab
 */
public class NewSceneDialog extends VisWindow {
	private StatusBarModule statusBar;

	private FileAccessModule fileAccess;
	private SceneIOModule sceneIO;
	private SceneCacheModule sceneCache;
	private SceneTabsModule sceneTabsModule;

	private VisValidatableTextField nameTextField;
	private VisValidatableTextField pathTextField;

	private VisValidatableTextField widthField;
	private VisValidatableTextField heightField;

	private VisValidatableTextField pixelsPerUnitField;

	private EnumSelectBox<SceneViewport> viewportModeSelectBox;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton createButton;

	private FileHandle assetsFolder;

	public NewSceneDialog (ModuleInjector injector) {
		super("New Scene");
		injector.injectModules(this);
		addCloseButton();
		closeOnEscape();
		setModal(true);

		assetsFolder = fileAccess.getAssetsFolder();

		createUI();
		createListeners();
		createValidators();

		pack();
		centerWindow();
	}

	private void createUI () {
		nameTextField = new VisValidatableTextField();
		pathTextField = new VisValidatableTextField("/scene/");
		viewportModeSelectBox = new EnumSelectBox<>(SceneViewport.class, new PrettyEnumNameProvider<>());

		errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();
		columnDefaults(1).width(300);

		row().padTop(4);

		VisTable fileFieldTable = new VisTable(true);
		fileFieldTable.add(nameTextField).expand().fill();
		fileFieldTable.add(new VisLabel(".scene"));

		add(new VisLabel("File name"));
		add(fileFieldTable);
		row();

		add(new VisLabel("Path"));
		add(pathTextField);
		row();

		add(new VisLabel("Viewport"));
		add(viewportModeSelectBox);
		row();

		pixelsPerUnitField = new VisValidatableTextField("100");
		pixelsPerUnitField.setTextFieldFilter(new DigitsOnlyFilter());

		add(new VisLabel("Pixels per unit"));
		add(pixelsPerUnitField).left().width(60);
		row();

		widthField = new VisValidatableTextField("12.8");
		heightField = new VisValidatableTextField("7.2");
		widthField.setTextFieldFilter(new FloatDigitsOnlyFilter(false));
		heightField.setTextFieldFilter(new FloatDigitsOnlyFilter(false));

		VisTable sizeTable = new VisTable(true);
		add(new VisLabel("Width"));
		sizeTable.add(widthField).width(60);
		sizeTable.add(new VisLabel("Height"));
		sizeTable.add(heightField).width(60);
		sizeTable.add(new VisLabel("units"));
		sizeTable.add().expand().fill();

		add(sizeTable).expand().fill();
		row();

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		createButton = new VisTextButton("Create");
		createButton.setDisabled(true);

		buttonTable.add(errorLabel).fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(createButton);

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

		createButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				final FileHandle targetFile = Gdx.files.absolute(pathTextField.getText()).child(nameTextField.getText() + ".scene");
				assetsFolder.child(pathTextField.getText()).mkdirs(); //create non existing path dirs
				sceneIO.create(targetFile, viewportModeSelectBox.getSelectedEnum(),
						Float.valueOf(widthField.getText()), Float.valueOf(heightField.getText()), Integer.valueOf(pixelsPerUnitField.getText()));
				statusBar.setText("Scene created: " + targetFile.path().substring(1));

				Dialogs.showOptionDialog(getStage(), "Message", "Open this new scene in editor?", OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						sceneTabsModule.open(assetsFolder.child(targetFile.path()));
					}
				});

				fadeOut();
			}
		});
	}

	private void createValidators () {
		FormValidator validator = new FormValidator(createButton, errorLabel);
		validator.notEmpty(nameTextField, "Name cannot be empty");
		validator.notEmpty(pathTextField, "Path cannot be empty");

		validator.floatNumber(widthField, "Width must be a number");
		validator.floatNumber(heightField, "Height must be a number");
		validator.integerNumber(pixelsPerUnitField, "Pixel per units must be a number");

		validator.valueGreaterThan(widthField, "Width must be greater than zero", 0);
		validator.valueGreaterThan(heightField, "Height must be greater than zero", 0);
		validator.valueGreaterThan(pixelsPerUnitField, "Pixel per units must be greater than zero", 0);

		validator.custom(nameTextField, new FormInputValidator("That scene already exists") {
			@Override
			public boolean validate (String input) {
				FileHandle sceneFile = assetsFolder.child(pathTextField.getText()).child(input + ".scene");
				return !sceneFile.exists();
			}
		});
	}
}
