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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialog;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener;
import com.kotcrab.vis.ui.widget.ButtonBar;
import com.kotcrab.vis.ui.widget.ButtonBar.ButtonType;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.Optional;

//TODO settings dialogs allowing to turn dialog back again on, now it would required modifying setting file
//TODO use gson when 0.3.0 branch is merged into master

/** @author Kotcrab */
public class DisableableDialogsModule extends EditorModule {
	private static final String VIS_PREFIX = "com.kotcrab.vis.editor.";
	public static final String DIALOG_PROJECT_EXPORT = VIS_PREFIX + "EXPORT_PROJECT_DIALOG";
	public static final String POLYGON_TOOL_ROTATED_UNSUPPORTED = VIS_PREFIX + "POLYGON_TOOL_ROTATED_UNSUPPORTED_DIALOG";

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

	public Optional<DisableableOptionDialog> showOptionDialog (String dialogId, DefaultDialogOption defaultOption, Stage stage, String title, String text, OptionDialogType type, OptionDialogListener listener) {
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

		DisableableOptionDialog dialog = new DisableableOptionDialog(title, text, type, wrapperListener, dontShowAgain);
		stage.addActor(dialog.fadeIn());
		return Optional.of(dialog);
	}

	public enum DefaultDialogOption {
		YES, NO, CANCEL
	}

	/** @see OptionDialog */
	public static class DisableableOptionDialog extends VisWindow {
		private final ButtonBar buttonBar;

		public DisableableOptionDialog (String title, String text, OptionDialogType type, final OptionDialogListener listener, VisCheckBox dontShowAgain) {
			super(title);

			setModal(true);

			add(new VisLabel(text, Align.center)).colspan(2);
			row();
			defaults().space(6);
			defaults().padBottom(3);

			buttonBar = new ButtonBar();
			buttonBar.setIgnoreSpacing(true);

			ChangeListener yesBtnListener = new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					listener.yes();
					fadeOut();
				}
			};

			ChangeListener noBtnListener = new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					listener.no();
					fadeOut();
				}
			};

			ChangeListener cancelBtnListener = new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					listener.cancel();
					fadeOut();
				}
			};

			switch (type) {
				case YES_NO:
					buttonBar.setButton(ButtonType.YES, yesBtnListener);
					buttonBar.setButton(ButtonType.NO, noBtnListener);
					break;
				case YES_CANCEL:
					buttonBar.setButton(ButtonType.YES, yesBtnListener);
					buttonBar.setButton(ButtonType.CANCEL, cancelBtnListener);
					break;
				case YES_NO_CANCEL:
					buttonBar.setButton(ButtonType.YES, yesBtnListener);
					buttonBar.setButton(ButtonType.NO, noBtnListener);
					buttonBar.setButton(ButtonType.CANCEL, cancelBtnListener);
					break;
			}

			add(dontShowAgain).left();
			add(buttonBar.createTable()).expandX().center();

			pack();
			centerWindow();
		}

		public DisableableOptionDialog setNoButtonText (String text) {
			buttonBar.getTextButton(ButtonType.NO).setText(text);
			pack();
			return this;
		}

		public DisableableOptionDialog setYesButtonText (String text) {
			buttonBar.getTextButton(ButtonType.YES).setText(text);
			pack();
			return this;
		}

		public DisableableOptionDialog setCancelButtonText (String text) {
			buttonBar.getTextButton(ButtonType.CANCEL).setText(text);
			pack();
			return this;
		}
	}
}
