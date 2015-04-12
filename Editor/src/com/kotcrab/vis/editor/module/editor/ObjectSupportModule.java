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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.plugin.ObjectSupport;

public class ObjectSupportModule extends EditorModule {
	private ObjectMap<Class, ObjectSupport> supportMap = new ObjectMap<>();

	public void register (ObjectSupport support) {
		supportMap.put(support.getObjectClass(), support);
	}

	public ObjectSupport get (Class key) {
		return supportMap.get(key);
	}

	public ObjectMap<Class, ObjectSupport> getSupports () {
		return supportMap;
	}
}
