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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialog;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisDialog;

import java.util.Optional;

//TODO settings dialogs allowing to turn dialog back again on, now it would required modifying setting file
//TODO use gson when 0.3.0 branch is merged into master

/** @author Kotcrab */
public class DisableableDialogsModule extends EditorModule {
	private static final String VIS_PREFIX = "com.kotcrab.vis.editor.";
	public static final String DIALOG_PROJECT_EXPORT = VIS_PREFIX + "EXPORT_PROJECT_DIALOG";

	private AppFileAccessModule fileAccess;

	private Array<String> disabledDialogs;

	private FileHandle configFile;
	private Json json;

	@Override
	public void init () {
		configFile = fileAccess.getConfigFolder().child("disabledDialogs.json");
		json = new Json();
		json.addClassTag("String", String.class);
		json.setOutputType(OutputType.json);

		if (configFile.file().exists()) {
			disabledDialogs = json.fromJson(Array.class, configFile);
		} else {
			disabledDialogs = new Array<>();
		}
	}

	private void saveConfig () {
		json.toJson(disabledDialogs, configFile);
	}

	public Optional<OptionDialog> showOptionDialog (String dialogId, DefaultDialogOption defaultOption, Stage stage, String title, String text, OptionDialogType type, OptionDialogListener listener) {
		if (disabledDialogs.contains(dialogId, false)) {
			switch (defaultOption) {
				case YES:
					listener.yes();
					break;
				case NO:
					listener.no();
					break;
				case CANCEL:
					listener.cancel();
					break;
			}
			return Optional.empty();
		}

		VisCheckBox dontShowAgain = new VisCheckBox("Don't show again");

		OptionDialogListener wrapperListener = new OptionDialogListener() {
			@Override
			public void yes () {
				checkIfDontShowAgainChecked();
				listener.yes();
			}

			@Override
			public void no () {
				checkIfDontShowAgainChecked();
				listener.no();
			}

			@Override
			public void cancel () {
				checkIfDontShowAgainChecked();
				listener.cancel();
			}

			private void checkIfDontShowAgainChecked () {
				if (dontShowAgain.isChecked()) {
					disabledDialogs.add(dialogId);
					saveConfig();
				}
			}
		};

		OptionDialog dialog = DialogUtils.showOptionDialog(stage, title, text, type, wrapperListener);
		insertActorInDialogButtonTable(dialog, dontShowAgain).padRight(10);
		dialog.pack();

		return Optional.of(dialog);
	}

	private <T extends Actor> Cell<T> insertActorInDialogButtonTable (VisDialog target, T newActor) {
		Array<Actor> oldActors = new Array<>(target.getButtonsTable().getChildren());
		Table table = target.getButtonsTable();
		table.clearChildren();

		Cell<T> cell = table.add(newActor);
		oldActors.forEach(table::add);

		return cell;
	}

	public enum DefaultDialogOption {
		YES, NO, CANCEL
	}
}
