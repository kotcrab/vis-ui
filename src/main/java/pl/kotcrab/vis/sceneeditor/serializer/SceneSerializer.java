/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/


package pl.kotcrab.vis.sceneeditor.serializer;

import pl.kotcrab.vis.sceneeditor.SceneEditor;

import com.badlogic.gdx.utils.ObjectMap;

public interface SceneSerializer {
	public void init (SceneEditor editor, ObjectMap<String, Object> objectMap);

	/** Loads all properties from provied scene file. If file does not exist it will do nothing */
	public void load ();

	/** Saves all changes to provied scene file
	 * @return true if save succeed, false otherwise */
	public boolean save ();
}
