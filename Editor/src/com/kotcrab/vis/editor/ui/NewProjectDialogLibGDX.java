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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.module.ProjectIOModule;
import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.ui.FormValidator;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.util.DialogUtils;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
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

		TableUtils.setSpaceDefaults(this);
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
		validator.fileExists(sourceLoc, projectRoot, "Source folder does not exist!");
		validator.fileExists(assetsLoc, projectRoot, "Assets folder does not exist!");
	}

	private void createProject () {
		Project project = new Project();
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
