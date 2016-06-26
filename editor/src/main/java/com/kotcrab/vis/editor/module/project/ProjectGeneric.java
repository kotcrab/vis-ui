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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Generic editor project, that does not really on any game framework.
 * @author Kotcrab
 */
public class ProjectGeneric extends Project {
	/** absolute path */
	private transient String visDirectory;
	/** absolute path */
	private String assetsOutput;

	public ProjectGeneric (String visDirectory, String assetsOutput) {
		this.visDirectory = visDirectory;
		this.assetsOutput = assetsOutput;
	}

	@Override
	public void updateRoot (FileHandle projectDataFile) {
		visDirectory = projectDataFile.parent().path();
	}

	@Override
	public String verifyIfCorrect () {
		return null;
	}

	@Override
	public FileHandle getVisDirectory () {
		return Gdx.files.absolute(visDirectory);
	}

	@Override
	public FileHandle getAssetOutputDirectory () {
		return Gdx.files.absolute(assetsOutput);
	}

	@Override
	public String getRecentProjectDisplayName () {
		return Gdx.files.absolute(visDirectory).name();
	}
}
