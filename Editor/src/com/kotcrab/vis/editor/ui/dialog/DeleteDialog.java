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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.ui.WindowResultListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

/**
 * Dialog displayed for file delete confirmation
 * @author Kotcrab
 */
public class DeleteDialog extends VisWindow {
	private WindowResultListener<DeleteDialogResult> listener;

	public DeleteDialog (FileHandle file, boolean safeDeleteSupported, final WindowResultListener<DeleteDialogResult> listener) {
		super("Delete");
		this.listener = listener;

		setModal(true);
		addCloseButton();
		closeOnEscape();
		TableUtils.setSpacingDefaults(this);

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
		setSize(getWidth() + 10, getHeight() + 4);
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
