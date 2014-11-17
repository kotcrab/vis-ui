package pl.kotcrab.vis.ui.widget.file;

import java.io.File;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

public class FavouritesIO
{
	private static String favouritePrefsName = "pl.kotcrab.vis.ui.widget.file.filechooserfavs";

	private Preferences prefs;
	private Array<File> favourites = new Array<File>();
	
	public static String getFavouritePrefsName () {
		return favouritePrefsName;
	}

	public static void setFavouritePrefsName (String favouritePrefsName) {
		FavouritesIO.favouritePrefsName = favouritePrefsName;
	}
	
	
	
}