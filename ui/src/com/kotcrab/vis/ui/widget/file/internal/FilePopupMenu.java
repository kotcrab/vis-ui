/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserStyle;
import com.kotcrab.vis.ui.widget.file.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.kotcrab.vis.ui.widget.file.internal.FileChooserText.*;

/** @author Kotcrab */
public class FilePopupMenu extends PopupMenu {
	private final FileChooserStyle style;

	private boolean trashAvailable;

	private FileHandle file;

	private MenuItem delete;
	private MenuItem newDirectory;
	private MenuItem showInExplorer;
	private MenuItem addToFavorites;
	private MenuItem removeFromFavorites;

	public FilePopupMenu (final FileChooser chooser, FileChooserStyle chooserStyle, final FilePopupMenuCallback callback) {
		super(chooserStyle.popupMenuStyleName);
		this.style = chooserStyle;

		delete = new MenuItem(CONTEXT_MENU_DELETE.get(), style.iconTrash);
		newDirectory = new MenuItem(CONTEXT_MENU_NEW_DIRECTORY.get(), style.iconFolderNew);
		showInExplorer = new MenuItem(CONTEXT_MENU_SHOW_IN_EXPLORER.get());
		addToFavorites = new MenuItem(CONTEXT_MENU_ADD_TO_FAVORITES.get(), style.iconFolderStar);
		removeFromFavorites = new MenuItem(CONTEXT_MENU_REMOVE_FROM_FAVORITES.get(), style.iconFolderStar);

		delete.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Dialogs.showOptionDialog(chooser.getStage(), POPUP_TITLE.get(),
						trashAvailable ? CONTEXT_MENU_MOVE_TO_TRASH_WARNING.get() : CONTEXT_MENU_DELETE_WARNING.get(),
						OptionDialogType.YES_NO, new OptionDialogAdapter() {
							@Override
							public void yes () {
								try {
									boolean success = callback.delete(file);
									if (success == false) {
										Dialogs.showErrorDialog(chooser.getStage(), POPUP_DELETE_FILE_FAILED.get());
									}
								} catch (IOException e) {
									Dialogs.showErrorDialog(chooser.getStage(), POPUP_DELETE_FILE_FAILED.get(), e);
									e.printStackTrace();
								}
								chooser.refresh();
							}
						});
			}
		});

		newDirectory.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				callback.showNewDirDialog();
			}
		});

		showInExplorer.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				try {
					FileUtils.showDirInExplorer(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		addToFavorites.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				chooser.addFavorite(file);
			}
		});

		removeFromFavorites.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				chooser.removeFavorite(file);
			}
		});
	}

	public void build () {
		clearChildren();
		addItem(newDirectory);
	}

	public void build (Array<FileHandle> favorites, FileHandle file) {
		this.file = file;

		clearChildren();

		addItem(newDirectory);
		addSeparator();

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

		clearChildren();

		addItem(showInExplorer);

		if (favorites.contains(this.file, false)) addItem(removeFromFavorites);
	}

	public void fileDeleterChanged (boolean trashAvailable) {
		this.trashAvailable = trashAvailable;
		delete.setText(trashAvailable ? CONTEXT_MENU_MOVE_TO_TRASH.get() : CONTEXT_MENU_DELETE.get());
	}

	public interface FilePopupMenuCallback {
		void showNewDirDialog ();

		boolean delete (FileHandle fileHandle) throws IOException;
	}
}
