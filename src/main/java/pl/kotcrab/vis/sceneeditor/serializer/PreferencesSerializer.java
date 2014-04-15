
package pl.kotcrab.vis.sceneeditor.serializer;

import java.util.ArrayList;

import pl.kotcrab.vis.sceneeditor.SceneEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.SerializationException;

public class PreferencesSerializer extends AbstractJsonSerializer {
	private static final String TAG = "VisSceneEditor:PreferencesSerializer";

	private Preferences prefs;
	private String keyName;

	public PreferencesSerializer (SceneEditor editor, Preferences prefs, String keyName) {
		super(editor);
		this.prefs = prefs;
		this.keyName = keyName;
	}

	@Override
	public boolean saveJsonData (ArrayList<ObjectInfo> infos) {
		try {
			prefs.putString(keyName, getJson().toJson(infos));
			Gdx.app.log(TAG, "Saved changes to preferences.");
			return true;
		} catch (SerializationException e) {
			Gdx.app.error(TAG, "Error while saving to preferences.");
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<ObjectInfo> loadJsonData () {
		return getJson().fromJson(new ArrayList<ObjectInfo>().getClass(), prefs.getString(keyName));
	}

	@Override
	public boolean isReadyToLoad () {
		return prefs.contains(keyName);
	}
}
