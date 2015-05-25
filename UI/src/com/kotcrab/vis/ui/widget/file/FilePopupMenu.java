/*
 * Copyright 2014-2015 See AUTHORS file.
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

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import static com.kotcrab.vis.ui.widget.file.FileChooserText.*;

/**
 * @author Kotcrab
 */
public class FilePopupMenu extends PopupMenu {
	private FileChooser chooser;
	private I18NBundle bundle;

	private FileHandle file;

	private MenuItem delete;
	private MenuItem showInExplorer;
	private MenuItem addToFavorites;
	private MenuItem removeFromFavorites;

	public FilePopupMenu (String styleName, FileChooser fileChooser, I18NBundle bundle) {
		super(styleName);
		this.chooser = fileChooser;
		this.bundle = bundle;

		delete = new MenuItem(getText(CONTEXT_MENU_DELETE));
		showInExplorer = new MenuItem(getText(CONTEXT_MENU_SHOW_IN_EXPLORER));
		addToFavorites = new MenuItem(getText(CONTEXT_MENU_ADD_TO_FAVORITES));
		removeFromFavorites = new MenuItem(getText(CONTEXT_MENU_REMOVE_FROM_FAVORITES));

		delete.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				DialogUtils.showOptionDialog(getStage(), getText(POPUP_TITLE), getText(CONTEXT_MENU_DELETE_WARNING), OptionDialogType.YES_NO, new OptionDialogAdapter() {
					@Override
					public void yes () {
						file.delete();
						chooser.refresh();
					}
				});
			}
		});

		showInExplorer.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
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

	public void build (Array<FileHandle> favorites, FileHandle file) {
		this.file = file;

		clearChildren();

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

	private String getText (FileChooserText text) {
		return bundle.get(text.getName());
	}
}
