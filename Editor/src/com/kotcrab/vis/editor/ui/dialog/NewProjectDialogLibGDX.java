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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.editor.ProjectIOModule;
import com.kotcrab.vis.editor.module.project.ProjectLibGDX;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class NewProjectDialogLibGDX extends VisWindow {
	private VisValidableTextField projectRoot;
	private VisValidableTextField sourceLoc;
	private VisValidableTextField assetsLoc;

	private VisTextButton chooseRootButton;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton createButton;

	private FileChooser fileChooser;

	private ProjectIOModule projectIO;

	public NewProjectDialogLibGDX (ProjectIOModule projectIO) {
		super("New Project");
		setModal(true);

		this.projectIO = projectIO;

		addCloseButton();
		closeOnEscape();

		createUI();
		createListeners();
		createValidators();

		fileChooser = new FileChooser(Mode.OPEN);
		fileChooser.setSelectionMode(SelectionMode.DIRECTORIES);
		fileChooser.setListener(new FileChooserAdapter() {
			@Override
			public void selected (FileHandle file) {
				projectRoot.setText(file.file().getAbsolutePath());
			}
		});

		pack();
		centerWindow();
	}

	private void createUI () {
		projectRoot = new VisValidableTextField("");
		chooseRootButton = new VisTextButton("Choose...");
		sourceLoc = new VisValidableTextField("/core/src");
		assetsLoc = new VisValidableTextField("/android/assets");

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

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		createButton = new VisTextButton("Create");
		createButton.setDisabled(true);

		buttonTable.add(errorLabel).fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(createButton);

		add(buttonTable).colspan(3).fill().expand();
		padBottom(5);
	}

	private void createListeners () {
		chooseRootButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(fileChooser.fadeIn());
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
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
	}

	private void createProject () {
		ProjectLibGDX project = new ProjectLibGDX();
		project.root = projectRoot.getText();
		project.assets = assetsLoc.getText();
		project.source = sourceLoc.getText();

		String error = projectIO.verify(project);
		if (error == null) {
			projectIO.create(project);
			fadeOut();
		} else
			DialogUtils.showErrorDialog(getStage(), error);

	}
}
