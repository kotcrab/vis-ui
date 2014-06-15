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

package pl.kotcrab.vis.sceneeditor.plugin.interfaces;

import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public interface IObjectManager {
	public Array<ObjectRepresentation> getEditableObjects ();

	public ObjectMap<String, Object> getObjectMap ();

	public Array<ObjectRepresentation> getObjectRepresenationList ();

	public Array<ObjectRepresentation> getSelectedObjs ();

}
