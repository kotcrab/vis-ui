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

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue;
import com.kotcrab.vis.ui.widget.*;

/**
 * Dialog used to select new file
 * @author Kotcrab
 */
public class SelectFileDialog extends VisWindow {
	private String[] extensions;
	private boolean hideExtension;
	private FileHandle folder;
	private AssetsMetadataModule assetsMetadata;
	private AssetDirectoryDescriptor dirDescriptor;
	private FileDialogListener listener;

	private ObjectMap<String, FileHandle> fileMap = new ObjectMap<>();

	private VisLabel noFilesLabel;
	private VisList<String> fileList;
	private VisTextButton okButton;

	/** @param extensions multiple extensions can be passed separated with | for example: mp3|wav|ogg */
	public SelectFileDialog (String extensions, FileHandle folder, FileDialogListener listener) {
		this(extensions, false, folder, listener);
	}

	/** @param extensions multiple extensions can be passed separated with | for example: mp3|wav|ogg */
	public SelectFileDialog (String extensions, boolean hideExtension, FileHandle folder, FileDialogListener listener) {
		this(extensions, hideExtension, folder, null, null, listener);
	}

	/** @param extensions multiple extensions can be passed separated with | for example: mp3|wav|ogg */
	public SelectFileDialog (String extensions, boolean hideExtension, FileHandle folder, AssetsMetadataModule assetsMetadata, AssetDirectoryDescriptor dirDescriptor, FileDialogListener listener) {
		super("Select File");
		this.hideExtension = hideExtension;
		this.folder = folder;
		this.assetsMetadata = assetsMetadata;
		this.dirDescriptor = dirDescriptor;
		this.listener = listener;
		setExtensions(extensions);

		setModal(true);
		addCloseButton();
		closeOnEscape();

		fileList = new VisList<>();

		VisTextButton cancelButton;

		TableUtils.setSpacingDefaults(this);
		defaults().left();

		VisTable buttonsTable = new VisTable(true);
		buttonsTable.add(cancelButton = new VisTextButton("Cancel"));
		buttonsTable.add(okButton = new VisTextButton("OK"));

		noFilesLabel = new VisLabel("There isn't any available file to select.");

		add(noFilesLabel).height(PrefHeightIfVisibleValue.INSTANCE).center().spaceBottom(0).row();
		add(fileList).expand().fill().height(PrefHeightIfVisibleValue.INSTANCE).row();
		add(buttonsTable).padBottom(2).right();

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

		if (fileMap.size == 0) {
			noFilesLabel.setVisible(true);
			okButton.setDisabled(true);
		} else {
			noFilesLabel.setVisible(false);
			okButton.setDisabled(false);
		}

		packAndCenter();
	}

	private void finishSelection () {
		FileHandle file = fileMap.get(fileList.getSelected());

		if (file == null) {
			Dialogs.showErrorDialog(getStage(), "You must select file!");
			return;
		}

		listener.selected(file);
		fadeOut();
	}

	private void buildFileList (FileHandle directory) {
		for (FileHandle file : directory.list()) {
			if (file.isDirectory()) {
				buildFileList(file);
				continue;
			}

			if (assetsMetadata != null && dirDescriptor != null) {
				if (assetsMetadata.isDirectoryMarkedAs(file, dirDescriptor) == false) continue;
			}

			for (String extension : extensions) {
				if (file.extension().equals(extension)) {
					fileMap.put(file.path().substring(folder.path().length() + 1, file.path().length() - (hideExtension ? file.extension().length() + 1 : 0)), file);
				}
			}
		}

		fileList.setItems(fileMap.keys().toArray());
	}

	public void setExtensions (String extensions) {
		this.extensions = extensions.split("\\|");
	}

	public interface FileDialogListener {
		void selected (FileHandle file);
	}
}
