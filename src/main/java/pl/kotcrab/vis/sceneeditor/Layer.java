
package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.utils.ObjectMap;

public class Layer extends ObjectMap<String, Object> {
	public Layer add (Object obj, String identifier) {
		put(identifier, obj);
		return this;
	}
}
