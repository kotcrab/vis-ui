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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.editor.FileChooserModule;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.editor.module.project.ProjectLibGDX;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.form.FormInputValidator;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;

import java.io.File;

/**
 * LibGDX project subdialog for {@link NewProjectDialog}
 * @author Kotcrab
 */
public class NewProjectDialogLibGDX extends VisTable {
	private VisValidatableTextField projectRoot;
	private VisValidatableTextField sourceLoc;
	private VisValidatableTextField assetsLoc;

	private VisTextButton chooseRootButton;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton createButton;

	private NewProjectDialog dialog;
	private FileChooserModule fileChooserModule;
	private ProjectIOModule projectIO;

	public NewProjectDialogLibGDX (NewProjectDialog dialog, FileChooserModule fileChooserModule, ProjectIOModule projectIO) {
		this.dialog = dialog;
		this.fileChooserModule = fileChooserModule;
		this.projectIO = projectIO;

		createUI();
		createListeners();
		createValidators();
	}

	private void createUI () {
		projectRoot = new VisValidatableTextField("");
		chooseRootButton = new VisTextButton("Choose...");
		sourceLoc = new VisValidatableTextField("/core/src");
		assetsLoc = new VisValidatableTextField("/android/assets");

		errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();
		columnDefaults(1).width(300);

		row().padTop(4);
		add(new VisLabel("Project root"));
		add(projectRoot);
		add(chooseRootButton);
		row();

		add(new VisLabel("Source folder"));
		add(sourceLoc).fill();
		row();

		add(new VisLabel("Assets folder"));
		add(assetsLoc).fill();
		row();

		add(new VisLabel("Assets folder is cleared on each export!")).colspan(3).row();

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		createButton = new VisTextButton("Create");
		createButton.setDisabled(true);

		buttonTable.add(errorLabel).fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(createButton);

		add(buttonTable).colspan(3).fillX().expandX();
	}

	private void createListeners () {
		chooseRootButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fileChooserModule.pickFileOrDirectory(new SingleFileChooserListener() {
					@Override
					public void selected (FileHandle file) {
						String path = file.file().getAbsolutePath();
						if (path.endsWith(File.separator) == false) path += File.separator;
						projectRoot.setText(path);
					}
				});
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				dialog.fadeOut();
			}
		});

		createButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				createProject();
			}
		});
	}

	private void createValidators () {
		FormValidator validator = new FormValidator(createButton, errorLabel);

		validator.notEmpty(projectRoot, "Project root path cannot be empty!");
		validator.notEmpty(sourceLoc, "Source location cannot be empty!");
		validator.notEmpty(assetsLoc, "Assets location cannot be empty!");

		validator.fileExists(projectRoot, "Project folder does not exist!");
		validator.fileExists(sourceLoc, projectRoot, "Source folder does not exist!", false);
		validator.fileExists(assetsLoc, projectRoot, "Assets folder does not exist!", false);

		validator.custom(sourceLoc, new FormInputValidator("Source folder can't be the same as project root") {
			@Override
			protected boolean validate (String input) {
				return !new File(projectRoot.getText()).equals(new File(projectRoot.getText() + input));
			}
		});
		validator.custom(assetsLoc, new FormInputValidator("Assets folder can't be the same as project root") {
			@Override
			protected boolean validate (String input) {
				return !new File(projectRoot.getText()).equals(new File(projectRoot.getText() + input));
			}
		});
	}

	private void createProject () {
		ProjectLibGDX project = new ProjectLibGDX(projectRoot.getText(), sourceLoc.getText(), assetsLoc.getText());

		String error = project.verifyIfCorrect();
		if (error == null) {
			projectIO.createLibGDXProject(project);
			dialog.fadeOut();
		} else {
			Dialogs.showErrorDialog(getStage(), error);
		}
	}
}
