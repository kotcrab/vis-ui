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

package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.AccessorHandler;
import pl.kotcrab.vis.sceneeditor.EditorState;
import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;
import pl.kotcrab.vis.sceneeditor.plugin.PluginState;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IObjectManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ObjectManagerPlugin extends PluginState implements IObjectManager {

	private AccessorHandler<SceneEditorAccessor<?>> accessorHandler;

	private ObjectMap<String, Object> objectMap;
	private Array<ObjectRepresentation> objectRepresenationList;
	private Array<ObjectRepresentation> selectedObjs;

	public ObjectManagerPlugin (AccessorHandler<SceneEditorAccessor<?>> accessorHandler) {
		this.accessorHandler = accessorHandler;
	}

	@Override
	public void init (EditorState state) {
		super.init(state);

		objectMap = new ObjectMap<String, Object>();

		if (state.devMode) {
			objectRepresenationList = new Array<ObjectRepresentation>();
			selectedObjs = new Array<ObjectRepresentation>();
		}
	}

	public void add (Object obj, String identifier) {
		if (accessorHandler.isAccessorForClassAvaiable(obj.getClass())) {
			objectMap.put(identifier, obj);

			if (state.devMode)
				objectRepresenationList.add(new ObjectRepresentation(accessorHandler.getAccessorForObject(obj), obj, identifier));
		} else {
			Gdx.app.error(SceneEditorConfig.TAG, "Could not add object with identifier: '" + identifier
				+ "'. Accessor not found for class " + obj.getClass() + ". See SceneEditor.registerAccessor()");
		}
	}

	@Override
	public ObjectMap<String, Object> getObjectMap () {
		return objectMap;
	}

	@Override
	public Array<ObjectRepresentation> getObjectRepresenationList () {
		return objectRepresenationList;
	}

	@Override
	public Array<ObjectRepresentation> getSelectedObjs () {
		return selectedObjs;
	}

	@Override
	public Array<ObjectRepresentation> getEditableObjects () {
		return objectRepresenationList;
	}
}
