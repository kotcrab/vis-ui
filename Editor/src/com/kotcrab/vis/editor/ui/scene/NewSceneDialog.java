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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.SceneCacheModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
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
	@InjectModule private StatusBarModule statusBar;

	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private SceneIOModule sceneIO;
	@InjectModule private SceneCacheModule sceneCache;
	@InjectModule private SceneTabsModule sceneTabsModule;

	private VisValidableTextField nameTextField;
	private VisValidableTextField pathTextField;

	private VisValidableTextField widthField;
	private VisValidableTextField heightField;

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
		nameTextField = new VisValidableTextField();
		pathTextField = new VisValidableTextField("/scene/");
		viewportModeSelectBox = new EnumSelectBox<>(SceneViewport.class);

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

		widthField = new VisValidableTextField("1280");
		heightField = new VisValidableTextField("720");
		widthField.setTextFieldFilter(new DigitsOnlyFilter());
		heightField.setTextFieldFilter(new DigitsOnlyFilter());

		VisTable sizeTable = new VisTable(true);
		add(new VisLabel("Width"));
		sizeTable.add(widthField).width(60);
		sizeTable.add(new VisLabel("Height"));
		sizeTable.add(heightField).width(60);
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
				sceneIO.create(targetFile, viewportModeSelectBox.getSelectedEnum(), Integer.valueOf(widthField.getText()), Integer.valueOf(heightField.getText()));
				statusBar.setText("Scene created: " + targetFile.path().substring(1));

				DialogUtils.showOptionDialog(getStage(), "Message", "Open this new scene in editor?", OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						EditorScene scene = sceneCache.get(assetsFolder.child(targetFile.path()));
						sceneTabsModule.open(scene);
					}
				});

				fadeOut();
			}
		});
	}

	private void createValidators () {
		FormValidator validator = new FormValidator(createButton, errorLabel);
		validator.notEmpty(nameTextField, "Name cannot be empty!");
		validator.notEmpty(pathTextField, "Path cannot be empty!");

		validator.integerNumber(widthField, "Width must be a number");
		validator.integerNumber(heightField, "Height must be a number");
		validator.valueGreaterThan(widthField, "Width must be greater than zero", 0);
		validator.valueGreaterThan(heightField, "Height must be greater than zero", 0);

		validator.fileExists(pathTextField, assetsFolder, "Path does not exist!");

		validator.custom(nameTextField, new FormInputValidator("That scene already exists!") {
			@Override
			public boolean validate (String input) {
				FileHandle sceneFile = assetsFolder.child(pathTextField.getText()).child(input + ".scene");
				return !sceneFile.exists();
			}
		});
	}
}
