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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;

public class AssetsUsages {
	public FileHandle file;
	public boolean limitExceeded;
	public int count;
	public ObjectMap<EditorScene, Array<EditorObject>> list = new ObjectMap<>();

	public String toPrettyString () {
		if (limitExceeded)
			return "More than " + AssetsUsageAnalyzerModule.USAGE_SEARCH_LIMIT + " usages found for " + file.name();
		else
			return "Found " + count + " " + (count == 1 ? "usage" : "usages") + " for " + file.name();
	}
}
