
package pl.kotcrab.vis.ui.widget.file;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class FavoritesIO {
	private static String favoritesPrefsName = "pl.kotcrab.vis.ui.widget.file.filechooser_favorites";
	private static String keyName = "favorites";

	private Preferences prefs;
	private Json json = new Json();

	public static String getFavoritesPrefsName () {
		return favoritesPrefsName;
	}

	public static void setFavoritesPrefsName (String favoritesPrefsName) {
		FavoritesIO.favoritesPrefsName = favoritesPrefsName;
	}

	public FavoritesIO () {
		prefs = Gdx.app.getPreferences(favoritesPrefsName);
	}

	public Array<FileHandle> loadFavorites () {
		String data = prefs.getString(keyName, "");
		if (data.equals(""))
			return new Array<FileHandle>();
		else
			return json.fromJson(FavouriteData.class, data).data;
	}

	public void saveFavorites (Array<FileHandle> favorites) {
		prefs.putString(keyName, json.toJson(new FavouriteData(favorites)));
		prefs.flush();
	}

	private class FavouriteData {
		public Array<FileHandle> data;

		public FavouriteData (Array<FileHandle> data) {
			this.data = data;
		}

	}

}
