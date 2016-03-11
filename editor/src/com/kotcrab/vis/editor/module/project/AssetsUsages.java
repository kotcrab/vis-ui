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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.editor.scene.EditorScene;

/**
 * @author Kotcrab
 */
public class AssetsUsages {
	public FileHandle file;
	public Array<SceneUsages> list = new Array<>();

	public AssetsUsages (FileHandle file) {
		this.file = file;
	}

	public String toPrettyString () {
		int count = count();
		return "Found " + count + " " + (count == 1 ? "usage" : "usages") + " for " + file.name();
	}

	public int count () {
		int count = 0;

		for (SceneUsages usages : list)
			count = count + usages.ids.size;

		return count;
	}

	public static class SceneUsages {
		public EditorScene scene;
		public IntArray ids = new IntArray();

		public SceneUsages (EditorScene scene) {
			this.scene = scene;
		}
	}
}
