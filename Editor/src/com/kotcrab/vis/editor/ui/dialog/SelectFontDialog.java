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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class SelectFontDialog extends VisWindow {

	private final String extension;
	private final FileHandle fontFolder;
	private FontDialogListener listener;

	private ObjectMap<String, FileHandle> fontsMap = new ObjectMap<>();

	private VisList<String> fontList;

	public SelectFontDialog (String extension, FileHandle fontFolder, FontDialogListener listener) {
		super("Select New Font");
		this.extension = extension;
		this.fontFolder = fontFolder;
		this.listener = listener;

		setModal(true);
		addCloseButton();
		closeOnEscape();

		fontList = new VisList<>();

		VisTextButton cancelButton;
		VisTextButton okButton;

		TableUtils.setSpacingDefaults(this);
		defaults().left();

		VisTable buttonsTable = new VisTable(true);
		buttonsTable.add(cancelButton = new VisTextButton("Cancel"));
		buttonsTable.add(okButton = new VisTextButton("OK"));

		add(fontList).expand().fill().row();
		add(buttonsTable).right();

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOut();
			}
		});

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				finishSelection();
			}
		});

		fontList.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if (getTapCount() == 2 && event.getButton() == Buttons.LEFT) finishSelection();
			}
		});

		rebuildFontList();
	}

	private void packAndCenter () {
		pack();
		setSize(getWidth() + 80, getHeight());
		centerWindow();
	}

	public void rebuildFontList () {
		fontList.clearItems();
		fontsMap.clear();

		buildFontList(fontFolder);
		packAndCenter();
	}

	private void finishSelection () {
		FileHandle file = fontsMap.get(fontList.getSelected());

		if (file == null) {
			DialogUtils.showErrorDialog(getStage(), "You must select font!");
			return;
		}

		listener.selected(file);
		fadeOut();
	}

	private void buildFontList (FileHandle fontDirectory) {
		for (FileHandle file : fontDirectory.list()) {
			if (file.isDirectory()) buildFontList(file);

			if (file.extension().equals(extension))
				fontsMap.put(file.path().substring(fontFolder.path().length() + 1), file);
		}

		fontList.setItems(fontsMap.keys().toArray());
	}

	public interface FontDialogListener {
		public void selected (FileHandle file);
	}
}
