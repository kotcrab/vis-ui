
package pl.kotcrab.vis.ui.widget.file;

import java.awt.Desktop;
import java.io.IOException;

import pl.kotcrab.vis.ui.widget.MenuItem;
import pl.kotcrab.vis.ui.widget.PopupMenu;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class FilePopupMenu extends PopupMenu {
	private MenuItem delete;
	private MenuItem showInExplorer;
	private MenuItem addToFavorites;
	private MenuItem removeFromFavorites;

	private FileHandle file;

	public FilePopupMenu () {
		delete = new MenuItem("Delete");
		showInExplorer = new MenuItem("Show in Explorer");
		addToFavorites = new MenuItem("Add To Favorites");
		removeFromFavorites = new MenuItem("Remove From Favorites");

		showInExplorer.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				try {
					Desktop.getDesktop().open(file.file());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				remove();
			}
		});
	}

	public void build (Array<FileHandle> favorites, FileHandle file) {
		this.file = file;

		clear();
		addItem(delete);

		if (file.type() == FileType.Absolute) addItem(showInExplorer);

		if (file.isDirectory()) {
			if (favorites.contains(file, true))
				addItem(removeFromFavorites);
			else
				addItem(addToFavorites);
		}
	}
}
