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

package com.kotcrab.vis.editor.module.project.converter;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;
import com.kotcrab.vis.ui.util.async.AsyncTask;

/**
 * Base class for project converters.
 * Project converters can use injected modules from {@link EditorModuleContainer}
 * @author Kotcrab
 */
public abstract class ProjectConverter {
	protected int fromVersion;
	protected int toVersion;

	public ProjectConverter (int fromVersion, int toVersion) {
		this.fromVersion = fromVersion;
		this.toVersion = toVersion;
		if (fromVersion >= toVersion) throw new IllegalStateException("fromVersion must be smaller than toVersion");
	}

	public int getFromVersion () {
		return fromVersion;
	}

	public int getToVersion () {
		return toVersion;
	}

	public abstract AsyncTask getConversionTask (FileHandle dataFile);
}
