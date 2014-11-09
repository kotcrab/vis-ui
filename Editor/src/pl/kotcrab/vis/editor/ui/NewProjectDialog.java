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

import pl.kotcrab.vis.editor.Project;
import pl.kotcrab.vis.editor.ui.widgets.EmptyWidget;
import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.widget.VisCheckBox;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisTextField;
import pl.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class NewProjectDialog extends VisWindow {
	private VisTextField projectRoot;
	private VisTextField sourceLoc;
	private VisTextField assetsLoc;
	private Project project = new Project();

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton createButton;

	public NewProjectDialog (Stage parent) {
		super(parent, "New Project");
		setModal(true);

		projectRoot = new VisTextField("");
		VisTextButton chooseButton = new VisTextButton("Choose...");
		sourceLoc = new VisTextField("/core/src");
		assetsLoc = new VisTextField("/android/assets");

		errorLabel = new VisLabel("Project root cannot be empty!");
		errorLabel.setColor(Color.RED);

		VisCheckBox signFiles = new VisCheckBox("Sign files using private key");

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();
		columnDefaults(1).width(300);

		add(new EmptyWidget(10, 3)).space(0).row();
		add(new VisLabel("Project root:"));
		add(projectRoot);
		add(chooseButton);
		row();

		add(new VisLabel("Source folder:"));
		add(sourceLoc).fill();
		row();

		add(new VisLabel("Assets folder:"));
		add(assetsLoc).fill();
		row();

		add(signFiles).colspan(2);
		row();

		VisTable buttonTable = new VisTable(true);
		buttonTable.defaults().minWidth(70);

		cancelButton = new VisTextButton("Cancel");
		createButton = new VisTextButton("Create");

		buttonTable.add(errorLabel).fill().expand();
		buttonTable.add(cancelButton);
		buttonTable.add(createButton);

		add(buttonTable).colspan(3).fill().expand();

		setupListeners();
		pack();
		setPositionToCenter();
	}

	private void setupListeners () {
		createButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				createProject();
			}
		});

		ProjectTextFieldChangeListener listener = new ProjectTextFieldChangeListener();
		projectRoot.setTextFieldListener(listener);
		sourceLoc.setTextFieldListener(listener);
		assetsLoc.setTextFieldListener(listener);
	}

	private class ProjectTextFieldChangeListener implements TextFieldListener {
		@Override
		public void keyTyped (VisTextField textField, char c) {
			errorLabel.setText("");
			createButton.setDisabled(true);

			if (projectRoot.isEmpty()) errorLabel.setText("Project root cannot be empty!");
			if (sourceLoc.isEmpty()) errorLabel.setText("Source location cannot be empty!");
			if (assetsLoc.isEmpty()) errorLabel.setText("Assets location cannot be empty!");

			if (errorLabel.getText().length == 0) createButton.setDisabled(false);
		}
	}

	private void createProject () {
// project.root = projectRoot.getText();
// project.assets = assetsLoc.getText();
// project.source = sourceLoc.getText();
	}
}
