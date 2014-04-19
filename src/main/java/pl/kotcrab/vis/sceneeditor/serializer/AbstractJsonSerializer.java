
package pl.kotcrab.vis.sceneeditor.serializer;

import java.util.ArrayList;

import pl.kotcrab.vis.sceneeditor.SceneEditor;
import pl.kotcrab.vis.sceneeditor.support.SceneEditorSupport;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
/**
 * Base class for Json based SceneSerializer, this object does not perfrom serlization and saving data, child class must do it
 * @author Pawel Pastuszak
 */
public abstract class AbstractJsonSerializer implements SceneSerializer {
	private SceneEditor editor;
	private ObjectMap<String, Object> objectMap;

	private Json json;

	public abstract boolean saveJsonData (Array<ObjectInfo> infos);

	public abstract boolean isReadyToLoad ();

	public abstract Array<ObjectInfo> loadJsonData ();

	public AbstractJsonSerializer (SceneEditor editor) {
		this.editor = editor;

		json = new Json();
		json.addClassTag("objectInfo", ObjectInfo.class);
	}

	@Override
	public void setObjectMap (ObjectMap<String, Object> objectMap) {
		this.objectMap = objectMap;
	}

	/** Loads all properties from provied scene file. If file does not exist it will do nothing */
	public void load () {
		if (isReadyToLoad() == false) return;

		Array<ObjectInfo> infos = new Array<ObjectInfo>();
		infos = loadJsonData();

		for (ObjectInfo info : infos) {
			SceneEditorSupport sup = editor.getSupportForIdentifier(info.supportIdentifier);

			Object obj = objectMap.get(info.identifier);

			sup.setX(obj, info.x);
			sup.setY(obj, info.y);
			sup.setOrigin(obj, info.originX, info.originY);
			sup.setSize(obj, info.width, info.height);
			sup.setScale(obj, info.scaleX, info.scaleY);
			sup.setRotation(obj, info.rotation);
		}
	}

	/** Saves all changes to provied scene file */
	public boolean save () {
		Array<ObjectInfo> infos = new Array<ObjectInfo>();

		for (Entry<String, Object> entry : objectMap.entries()) {

			Object obj = entry.value;

			SceneEditorSupport sup = editor.getSupportForClass(obj.getClass());

			ObjectInfo info = new ObjectInfo();
			info.supportIdentifier = sup.getIdentifier();
			info.identifier = entry.key;
			info.x = sup.getX(obj);
			info.y = sup.getY(obj);
			info.scaleX = sup.getScaleX(obj);
			info.scaleY = sup.getScaleY(obj);
			info.originX = sup.getOriginX(obj);
			info.originY = sup.getOriginY(obj);
			info.width = sup.getWidth(obj);
			info.height = sup.getHeight(obj);
			info.rotation = sup.getRotation(obj);

			infos.add(info);
		}

		return saveJsonData(infos);
	}

	protected Json getJson () {
		return json;
	}

}
