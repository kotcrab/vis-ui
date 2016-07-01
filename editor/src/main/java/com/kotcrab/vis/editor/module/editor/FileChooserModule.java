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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.util.vis.JNAFileDeleter;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;

/**
 * Provides shared file chooser instance
 * @author Kotcrab
 */
public class FileChooserModule extends EditorModule {
	private Stage stage;

	private FileChooser chooser;

	private FileChooserListener listener;

	@Override
	public void init () {
		FileChooser.setDefaultPrefsName("com.kotcrab.vis.editor");
		FileChooser.setSaveLastDirectory(true);
		chooser = new FileChooser(Mode.OPEN);
		chooser.setSelectionMode(SelectionMode.FILES_AND_DIRECTORIES);
		chooser.setFileDeleter(new JNAFileDeleter());
		chooser.setListener(new FileChooserListener() {
			@Override
			public void selected (Array<FileHandle> files) {
				if (listener != null) listener.selected(files);
			}

			@Override
			public void canceled () {
				if (listener != null) listener.canceled();
			}
		});
	}

	@Override
	public void resize () {
		chooser.centerWindow();
	}

	public void pickFile (FileChooserListener listener) {
		pick(listener, SelectionMode.FILES);
	}

	public void pickDirectory (FileChooserListener listener) {
		pick(listener, SelectionMode.DIRECTORIES);
	}

	public void pickFileOrDirectory (FileChooserListener listener) {
		pick(listener, SelectionMode.FILES_AND_DIRECTORIES);
	}

	private void pick (FileChooserListener listener, SelectionMode mode) {
		this.listener = listener;

		chooser.setMode(Mode.OPEN);
		chooser.setSelectionMode(mode);
		stage.addActor(chooser.fadeIn());
	}

	public void saveFile (FileChooserListener listener) {
		pick(listener, SelectionMode.FILES);
	}

	public void saveDirectory (FileChooserListener listener) {
		pick(listener, SelectionMode.DIRECTORIES);
	}

	public void saveFileOrDirectory (FileChooserListener listener) {
		pick(listener, SelectionMode.FILES_AND_DIRECTORIES);
	}

	private void save (FileChooserListener listener, SelectionMode mode) {
		this.listener = listener;

		chooser.setMode(Mode.SAVE);
		chooser.setSelectionMode(mode);
		stage.addActor(chooser.fadeIn());
	}
}
