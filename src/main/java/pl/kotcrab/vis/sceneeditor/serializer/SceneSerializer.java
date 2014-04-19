
package pl.kotcrab.vis.sceneeditor.serializer;

import com.badlogic.gdx.utils.ObjectMap;

public interface SceneSerializer {
	public void init (String assetsPath, ObjectMap<String, Object> objectMap);

	public void load ();

	public boolean save ();
}
