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

import java.io.File;

import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.SceneViewport;
import pl.kotcrab.vis.editor.module.FileAccessModule;
import pl.kotcrab.vis.ui.FormValidator;
import pl.kotcrab.vis.ui.TableUtils;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.widget.VisLabel;
import pl.kotcrab.vis.ui.widget.VisSelectBox;
import pl.kotcrab.vis.ui.widget.VisTextButton;
import pl.kotcrab.vis.ui.widget.VisValidableTextField;
import pl.kotcrab.vis.ui.widget.VisWindow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class NewSceneDialog extends VisWindow {

	private VisValidableTextField nameTextField;
	private VisValidableTextField pathTextField;
	private VisSelectBox<SceneViewport> viewportModeSelectBox;

	private VisLabel errorLabel;

	private VisTextButton cancelButton;
	private VisTextButton createButton;

	private File visFolder;

	public NewSceneDialog () {
		super("New Scene");
		setModal(true);

		FileAccessModule fileAccess = Editor.instance.getProjectModule(FileAccessModule.class);
		visFolder = fileAccess.getVisFolder();

		createUI();
		createListeners();
		createValidators();

		pack();
		centerWindow();
	}

	private void createUI () {
		nameTextField = new VisValidableTextField();
		pathTextField = new VisValidableTextField("/assets/scene/");
		viewportModeSelectBox = new VisSelectBox<SceneViewport>();
		viewportModeSelectBox.setItems(SceneViewport.values());

		errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		TableUtils.setSpaceDefaults(this);
		columnDefaults(0).left();
		columnDefaults(1).width(300);

		row().padTop(4);
		add(new VisLabel("File name"));
		add(nameTextField);
		row();

		add(new VisLabel("Path"));
		add(pathTextField);
		row();

		add(new VisLabel("Viewport"));
		add(viewportModeSelectBox);
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
			}
		});
	}

	private void createValidators () {
		FormValidator validator = new FormValidator(createButton, errorLabel);

		validator.notEmpty(nameTextField, "Name cannot be empty!");
		validator.notEmpty(pathTextField, "Path cannot be empty!");

		validator.fileExist(pathTextField, visFolder, "Path does not exist!");
	}

}
