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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * EditorScene layer class
 * @author Kotcrab
 */
@Deprecated
public class Layer implements Disposable, SceneSelectionRoot {
	public String name;
	public boolean locked = false;
	public boolean visible = true;
	public Array<EditorObject> entities = new Array<EditorObject>();

	public Layer (String name) {
		this.name = name;
	}

	@Override
	public void dispose () {
		for (EditorObject entity : entities) {
			entity.dispose();
		}
	}

	@Override
	public String toString () {
		return name;
	}

	@Override
	public Array<EditorObject> getSelectionEntities () {
		return entities;
	}
}
