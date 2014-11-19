
package pl.kotcrab.vis.ui.widget.file;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import pl.kotcrab.vis.ui.widget.MenuItem;
import pl.kotcrab.vis.ui.widget.PopupMenu;
import pl.kotcrab.vis.ui.widget.VisDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class FilePopupMenu extends PopupMenu {
	private FileChooser chooser;
	private FileChooserLocale locale;

	private FileHandle file;

	private MenuItem delete;
	private MenuItem showInExplorer;
	private MenuItem addToFavorites;
	private MenuItem removeFromFavorites;

	public FilePopupMenu (FileChooser fileChooser, FileChooserLocale loc) {
		this.chooser = fileChooser;
		this.locale = loc;

		delete = new MenuItem("Delete");
		showInExplorer = new MenuItem("Show in Explorer");
		addToFavorites = new MenuItem("Add To Favorites");
		removeFromFavorites = new MenuItem("Remove From Favorites");

		delete.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				remove();
				showDeleteDialog();
			}
		});

		showInExplorer.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				remove();
				try {
					if (file.isDirectory())
						Desktop.getDesktop().open(file.file());
					else
						Desktop.getDesktop().open(file.parent().file());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		addToFavorites.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				remove();
				chooser.addFavoruite(file);
			}
		});

		removeFromFavorites.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				remove();
				chooser.removeFavoruite(file);
			}
		});

	}

	private void showDeleteDialog () {
		VisDialog dialog = new VisDialog(chooser.getStage(), locale.popupTitle) {
			@Override
			protected void result (Object object) {
				boolean delete = Boolean.parseBoolean(object.toString());
				if (delete) {
					file.delete();
					chooser.refresh();
				}
			}
		};
		dialog.text("This file will be deleted permanently? Are you sure?");
		dialog.button(locale.popupNo, false);
		dialog.button(locale.popupYes, true);
		dialog.pack();
		dialog.setPositionToCenter();
		chooser.getStage().addActor(dialog.fadeIn());
	}

	public void build (Array<FileHandle> favorites, FileHandle file) {
		this.file = file;

		clear();

		if (file.type() == FileType.Absolute || file.type() == FileType.External) addItem(delete);

		if (file.type() == FileType.Absolute) {
			addItem(showInExplorer);

			if (file.isDirectory()) {
				if (favorites.contains(file, false))
					addItem(removeFromFavorites);
				else
					addItem(addToFavorites);
			}
		}
	}

	public void buildForFavorite (Array<FileHandle> favorites, File file) {
		this.file = Gdx.files.absolute(file.getAbsolutePath());

		clear();

		addItem(showInExplorer);

		if (favorites.contains(this.file, false)) addItem(removeFromFavorites);
	}
}
