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

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Dialog used to select new file
 * @author Kotcrab
 */
public class SelectFileDialog extends VisWindow {
	private String extension;
	private boolean hideExtension;
	private FileHandle folder;
	private FileDialogListener listener;

	private ObjectMap<String, FileHandle> fileMap = new ObjectMap<>();

	private VisList<String> fileList;

	public SelectFileDialog (String extension, FileHandle folder, FileDialogListener listener) {
		this(extension, false, folder, listener);
	}

	public SelectFileDialog (String extension, boolean hideExtension, FileHandle folder, FileDialogListener listener) {
		super("Select File");
		this.extension = extension;
		this.hideExtension = hideExtension;
		this.folder = folder;
		this.listener = listener;

		setModal(true);
		addCloseButton();
		closeOnEscape();

		fileList = new VisList<>();

		VisTextButton cancelButton;
		VisTextButton okButton;

		TableUtils.setSpacingDefaults(this);
		defaults().left();

		VisTable buttonsTable = new VisTable(true);
		buttonsTable.add(cancelButton = new VisTextButton("Cancel"));
		buttonsTable.add(okButton = new VisTextButton("OK"));

		add(fileList).expand().fill().row();
		add(buttonsTable).right();

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
			}
		});

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				finishSelection();
			}
		});

		fileList.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if (getTapCount() == 2 && event.getButton() == Buttons.LEFT) finishSelection();
			}
		});

		rebuildFileList();
	}

	private void packAndCenter () {
		pack();
		setSize(getWidth() + 80, getHeight());
		centerWindow();
	}

	public void rebuildFileList () {
		fileList.clearItems();
		fileMap.clear();

		buildFileList(folder);
		packAndCenter();
	}

	private void finishSelection () {
		FileHandle file = fileMap.get(fileList.getSelected());

		if (file == null) {
			DialogUtils.showErrorDialog(getStage(), "You must select file!");
			return;
		}

		listener.selected(file);
		fadeOut();
	}

	private void buildFileList (FileHandle directory) {
		for (FileHandle file : directory.list()) {
			if (file.isDirectory()) buildFileList(file);

			if (file.extension().equals(extension))
				fileMap.put(file.path().substring(folder.path().length() + 1, file.path().length() - (hideExtension ? extension.length() + 1 : 0)), file);
		}

		fileList.setItems(fileMap.keys().toArray());
	}

	public interface FileDialogListener {
		void selected (FileHandle file);
	}
}
