/*
 * Copyright 2014-2017 See AUTHORS file.
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

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.HistoryPolicy;
import com.kotcrab.vis.ui.widget.file.FileChooserStyle;

import static com.kotcrab.vis.ui.widget.file.internal.FileChooserText.*;

/**
 * Manages {@link FileChooser} history of directories that user navigated into. This is internal VisUI API however this class
 * is also reused by VisEditor.
 * @author Kotcrab
 */
public class FileHistoryManager {
	private final FileHistoryCallback callback;

	private Array<FileHandle> history = new Array<FileHandle>();
	private Array<FileHandle> historyForward = new Array<FileHandle>();

	private VisTable buttonsTable;
	private VisImageButton backButton;
	private VisImageButton forwardButton;

	public FileHistoryManager (FileChooserStyle style, FileHistoryCallback callback) {
		this.callback = callback;
		backButton = new VisImageButton(style.iconArrowLeft, BACK.get());
		backButton.setGenerateDisabledImage(true);
		backButton.setDisabled(true);
		forwardButton = new VisImageButton(style.iconArrowRight, FORWARD.get());
		forwardButton.setGenerateDisabledImage(true);
		forwardButton.setDisabled(true);

		buttonsTable = new VisTable(true);
		buttonsTable.add(backButton);
		buttonsTable.add(forwardButton);

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				historyBack();
			}
		});

		forwardButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				historyForward();
			}
		});
	}

	public ClickListener getDefaultClickListener () {
		return new ClickListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (button == Buttons.BACK || button == Buttons.FORWARD) {
					return true;
				}
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (button == Buttons.BACK && hasHistoryBack()) {
					historyBack();
				} else if (button == Buttons.FORWARD && hasHistoryForward()) {
					historyForward();
				} else {
					super.touchUp(event, x, y, pointer, button);
				}
			}
		};
	}

	public VisTable getButtonsTable () {
		return buttonsTable;
	}

	public void historyClear () {
		history.clear();
		historyForward.clear();
		forwardButton.setDisabled(true);
		backButton.setDisabled(true);
	}

	public void historyAdd () {
		history.add(callback.getCurrentDirectory());
		historyForward.clear();
		backButton.setDisabled(false);
		forwardButton.setDisabled(true);
	}

	public void historyBack () {
		FileHandle dir = history.pop();
		historyForward.add(callback.getCurrentDirectory());

		if (setDirectoryFromHistory(dir) == false)
			historyForward.pop();

		if (!hasHistoryBack()) backButton.setDisabled(true);

		forwardButton.setDisabled(false);
	}

	public void historyForward () {
		FileHandle dir = historyForward.pop();
		history.add(callback.getCurrentDirectory());

		if (setDirectoryFromHistory(dir) == false)
			history.pop();

		if (!hasHistoryForward()) forwardButton.setDisabled(true);

		backButton.setDisabled(false);
	}

	private boolean setDirectoryFromHistory (FileHandle dir) {
		if (dir.exists()) {
			callback.setDirectory(dir, HistoryPolicy.IGNORE);
			return true;
		} else {
			Dialogs.showErrorDialog(callback.getStage(), DIRECTORY_NO_LONGER_EXISTS.get());
			return false;
		}
	}

	/** @return returns {@code true} if a forward-history is available */
	private boolean hasHistoryForward () {
		return historyForward.size != 0;
	}

	/** @return returns {@code true} if a back-history is available */
	private boolean hasHistoryBack () {
		return history.size != 0;
	}

	public interface FileHistoryCallback {
		FileHandle getCurrentDirectory ();

		void setDirectory (FileHandle directory, HistoryPolicy policy);

		Stage getStage ();
	}
}
