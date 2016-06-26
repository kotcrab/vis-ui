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

package com.kotcrab.vis.ui.widget.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;

/**
 * Implementation of {@link FileChooserListener} that can be used when user picks only one file. Provides convenient
 * {@link #selected(FileHandle)} method. If user picked more than one file (note that chooser must be in multiple select
 * mode for that to happen, see {@link FileChooser#setSelectionMode(SelectionMode)}), that method
 * will be called only for first selected file and remaining files will be ignored.
 * @author Kotcrab
 * @since 1.0.0
 */
public abstract class SingleFileChooserListener implements FileChooserListener {
	@Override
	public final void selected (Array<FileHandle> files) {
		selected(files.first());
	}

	/** Called for first file in selection. See {@link SingleFileChooserListener}. */
	protected abstract void selected (FileHandle file);

	@Override
	public void canceled () {

	}
}
