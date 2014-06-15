
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
