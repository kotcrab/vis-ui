
package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.EditorState;
import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;
import pl.kotcrab.vis.sceneeditor.component.AccessorHandler;
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
