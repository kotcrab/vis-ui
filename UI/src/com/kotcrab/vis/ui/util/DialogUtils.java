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

package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.InputDialogListener;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.widget.*;

public class DialogUtils {
	public static void showOKDialog (Stage stage, String title, String text) {
		VisDialog dialog = new VisDialog(title);
		dialog.text(text);
		dialog.button("OK").padBottom(3);
		dialog.pack();
		dialog.centerWindow();
		stage.addActor(dialog.fadeIn());
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, true, null, listener);
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, InputValidator validator, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, true, validator, listener);
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, cancelable, null, listener);
	}

	/**
	 * @param fieldTitle may be null
	 */
	public static void showInputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputValidator validator, InputDialogListener listener) {
		new InputDialog(stage, title, fieldTitle, cancelable, validator, listener);
	}


	public static void showErrorDialog (Stage stage, String text) {
		showErrorDialog(stage, text, (String) null);
	}

	public static void showErrorDialog (Stage stage, String text, Exception exception) {
		if (exception == null)
			showErrorDialog(stage, text, (String) null);
		else
			showErrorDialog(stage, text, getStackTrace(exception));
	}

	public static void showErrorDialog (Stage stage, String text, String details) {
		ErrorDialog dialog = new ErrorDialog(text, details);
		stage.addActor(dialog.fadeIn());
	}

	private static VisScrollPane createScrollPane (Actor widget) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}

	private static String getStackTrace (Throwable throwable) {
		StringBuilder builder = new StringBuilder();

		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append(element);
			builder.append("\n");
		}

		return builder.toString();
	}

	private static class InputDialog extends VisWindow {
		private InputDialogListener listener;
		private VisTextField field;
		private VisTextButton okButton;
		private VisTextButton cancelButton;


		public InputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputValidator validator, InputDialogListener listener) {
			super(title);
			this.listener = listener;

			TableUtils.setSpaceDefaults(this);
			setModal(true);

			if (cancelable) {
				addCloseButton();
				closeOnEscape();
			}

			VisTable buttonsTable = new VisTable(true);
			buttonsTable.add(cancelButton = new VisTextButton("Cancel"));
			buttonsTable.add(okButton = new VisTextButton("OK"));

			VisTable fieldTable = new VisTable(true);

			if (validator == null)
				field = new VisTextField();
			else
				field = new VisValidableTextField(validator);

			if (fieldTitle != null) fieldTable.add(new VisLabel(fieldTitle));

			fieldTable.add(field).expand().fill();

			add(fieldTable).padTop(3).spaceBottom(4);
			row();
			add(buttonsTable).padBottom(3);

			addListeners();

			if (validator != null) {
				addValidableFieldListener(field);
				okButton.setDisabled(!field.isInputValid());
			}

			pack();
			centerWindow();

			stage.addActor(fadeIn());
			field.focusField();
		}

		@Override
		protected void close () {
			super.close();
			listener.canceled();
		}

		private void addValidableFieldListener (final VisTextField field) {
			field.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if (field.isInputValid())
						okButton.setDisabled(false);
					else
						okButton.setDisabled(true);
				}
			});
		}

		private void addListeners () {
			okButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					listener.finished(field.getText());
					fadeOut();
				}
			});

			cancelButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					close();
				}
			});

			field.addListener(new InputListener() {
				@Override
				public boolean keyDown (InputEvent event, int keycode) {
					if (keycode == Keys.ENTER && okButton.isDisabled() == false) {
						listener.finished(field.getText());
						fadeOut();
					}

					return super.keyDown(event, keycode);
				}
			});
		}
	}

	private static class ErrorDialog extends VisDialog {
		final int OK = 0;
		final int DETAILS = 1;

		private VisTable detailsTable = new VisTable(true);
		private Cell<?> detailsCell;

		public ErrorDialog (String text, String stackTrace) {
			super("Error");

			text(text);

			if (stackTrace != null) {
				final VisTextButton copyButton = new VisTextButton("Copy");
				final VisLabel errorLabel = new VisLabel(stackTrace);

				copyButton.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						Gdx.app.getClipboard().setContents((errorLabel.getText().toString()));
						copyButton.setText("Copied");
					}
				});

				detailsTable.add(new VisLabel("Details:")).left().expand().padTop(6);
				detailsTable.add(copyButton);
				detailsTable.row();

				VisTable errorTable = new VisTable();
				errorTable.add(errorLabel).top().expand().fillX();
				detailsTable.add(createScrollPane(errorTable)).colspan(2).width(600).height(300);

				getContentTable().row();
				detailsCell = getContentTable().add(detailsTable);
				detailsCell.setActor(null);
				button("Details", DETAILS);
			}

			button("OK", OK).padBottom(3);
			pack();
			centerWindow();
		}

		@Override
		protected void result (Object object) {
			int result = (Integer) object;

			if (result == DETAILS) {
				detailsCell.setActor(detailsCell.hasActor() ? null : detailsTable);
				pack();
				centerWindow();
				cancel();
			}
		}
	}

}
