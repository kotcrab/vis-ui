/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.module.ProjectIOModule;
import pl.kotcrab.vis.editor.module.project.Project;
import pl.kotcrab.vis.ui.FormValidator;
import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.util.DialogUtils;
import pl.kotcrab.vis.ui.widget.VisCheckBox;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisValidableTextField;
import pl.kotcrab.vis.ui.widget.VisWindow;
import pl.kotcrab.vis.ui.widget.file.FileChooser;
import pl.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import pl.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import pl.kotcrab.vis.ui.widget.file.FileChooserAdapter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class NewProjectDialog extends VisWindow {
	private VisValidableTextField projectRoot;
	private VisValidableTextField sourceLoc;
	private VisValidableTextField assetsLoc;

	private VisCheckBox signFiles;

	private VisTextButton chooseRootButton;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton createButton;

	private FileChooser fileChooser;

	private ProjectIOModule projectIO;

	public NewProjectDialog () {
		super("New Project");
		setModal(true);

		projectIO = Editor.instance.getModule(ProjectIOModule.class);

		craeteUI();
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

	private void craeteUI () {
		projectRoot = new VisValidableTextField("");
		chooseRootButton = new VisTextButton("Choose...");
		sourceLoc = new VisValidableTextField("/core/src");
		assetsLoc = new VisValidableTextField("/android/assets");

		errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		signFiles = new VisCheckBox("Sign files using private key");

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

		add(signFiles).colspan(2);
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
			projectIO.create(project, signFiles.isChecked());
			fadeOut();
		} else
			DialogUtils.showOKDialog(getStage(), "Error", error);

	}
}
