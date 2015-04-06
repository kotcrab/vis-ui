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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;

public class FileChooserModule extends EditorModule {
	private FileChooser chooser;

	private FileChooserListener listener;

	@Override
	public void init () {
		chooser = new FileChooser(Mode.OPEN);
		chooser.setSelectionMode(SelectionMode.FILES_AND_DIRECTORIES);
		chooser.setListener(new FileChooserListener() {
			@Override
			public void selected (Array<FileHandle> files) {
				if(listener != null) listener.selected(files);
			}

			@Override
			public void selected (FileHandle file) {
				if(listener != null) listener.selected(file);
			}

			@Override
			public void canceled () {
				if(listener != null) listener.canceled();
			}
		});
	}

	@Override
	public void resize () {
		chooser.centerWindow();
	}

	public void pickFileOrDirectory (FileChooserAdapter listener) {
		this.listener = listener;

		chooser.setMode(Mode.OPEN);
		chooser.setSelectionMode(SelectionMode.FILES_AND_DIRECTORIES);
		Editor.instance.getStage().addActor(chooser.fadeIn());
	}
}
