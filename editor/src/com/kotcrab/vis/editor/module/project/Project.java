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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;

/**
 * Abstract class for VisEditor projects.
 * @author Kotcrab
 * @see ProjectLibGDX
 * @see ProjectGeneric
 */
public abstract class Project {
	/** Called after loading when project should update it's root */
	public abstract void updateRoot (FileHandle projectDataFile);

	/**
	 * Called during project creation
	 * @return error message if some error was found or null if project is valid
	 */
	public abstract String verifyIfCorrect ();

	public abstract FileHandle getVisDirectory ();

	public abstract FileHandle getAssetOutputDirectory ();

	public abstract String getRecentProjectDisplayName ();
}
