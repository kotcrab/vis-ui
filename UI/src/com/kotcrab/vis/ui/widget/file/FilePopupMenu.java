/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.ui.widget.file;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisDialog;

public class FilePopupMenu extends PopupMenu {
	private FileChooser chooser;
	private FileChooserLocale locale;

	private FileHandle file;

	private MenuItem delete;
	private MenuItem showInExplorer;
	private MenuItem addToFavorites;
	private MenuItem removeFromFavorites;

	public FilePopupMenu (FileChooser fileChooser, FileChooserLocale loc) {
		super(true);
		this.chooser = fileChooser;
		this.locale = loc;

		delete = new MenuItem(locale.contextMenuDelete);
		showInExplorer = new MenuItem(locale.contextMenuShowInExplorer);
		addToFavorites = new MenuItem(locale.contextMenuAddToFavorites);
		removeFromFavorites = new MenuItem(locale.contextMenuRemoveFromFavorites);

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
				chooser.addFavorite(file);
			}
		});

		removeFromFavorites.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				remove();
				chooser.removeFavorite(file);
			}
		});

	}

	private void showDeleteDialog () {
		VisDialog dialog = new VisDialog(locale.popupTitle) {
			@Override
			protected void result (Object object) {
				boolean delete = Boolean.parseBoolean(object.toString());
				if (delete) {
					file.delete();
					chooser.refresh();
				}
			}
		};
		dialog.text(locale.contextMenuDeleteWarning);
		dialog.button(locale.popupNo, false);
		dialog.button(locale.popupYes, true);
		dialog.pack();
		dialog.centerWindow();
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
