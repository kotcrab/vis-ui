/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.WindowResultListener;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class DeleteDialog extends VisWindow {
	private WindowResultListener<DeleteDialogResult> listener;

	public DeleteDialog (FileHandle file, boolean safeDeleteSupported, final WindowResultListener<DeleteDialogResult> listener) {
		super("Delete");
		this.listener = listener;

		setModal(true);
		addCloseButton();
		closeOnEscape();
		TableUtils.setSpaceDefaults(this);

		defaults().left();

		VisCheckBox safeDeleteCheckBox = new VisCheckBox("Safe delete (with usage search)");
		safeDeleteCheckBox.setChecked(true);
		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton okButton = new VisTextButton("OK");

		VisTable buttonsTable = new VisTable(true);
		buttonsTable.add(okButton);
		buttonsTable.add(cancelButton);

		if (FileUtils.hasTrash() == false)
			add(new VisLabel("File " + file.name() + " will be deleted, without moving to trash!\n Are you sure?")).row();
		else
			add(new VisLabel("Delete " + file.name() + "?")).row();

		if (safeDeleteSupported) add(safeDeleteCheckBox).row();

		add(buttonsTable).right();

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				listener.canceled();
				fadeOut();
			}
		});

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				listener.finished(new DeleteDialogResult(safeDeleteCheckBox.isChecked()));
				fadeOut();
			}
		});

		pack();
		setSize(getWidth() + 4, getHeight() + 4);
		centerWindow();
	}

	@Override
	protected void close () {
		super.close();
		listener.canceled();
	}

	public static class DeleteDialogResult {
		public boolean safeDelete;

		public DeleteDialogResult (boolean safeDelete) {
			this.safeDelete = safeDelete;
		}
	}
}
