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

package com.kotcrab.vis.ui.util.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.StringBuilder;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.i18n.BundleText;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;

/**
 * Utilities for displaying various type of dialogs, equivalent of JOptionPane from Swing.
 * @author Kotcrab
 * @since 0.2.0
 */
public class DialogUtils {
	private static final int BUTTON_CANCEL = 0;
	private static final int BUTTON_YES = 1;
	private static final int BUTTON_NO = 2;
	private static final int BUTTON_OK = 3;
	private static final int BUTTON_DETAILS = 4;

	/**
	 * Dialog with given text and single OK button.
	 * @param title dialog title
	 */
	public static VisDialog showOKDialog (Stage stage, String title, String text) {
		VisDialog dialog = new VisDialog(title);
		dialog.text(text);
		dialog.button(Text.OK.get()).padBottom(3);
		dialog.pack();
		dialog.centerWindow();
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/**
	 * Dialog with text and buttons like Yes, No, Cancel.
	 * @param title dialog title
	 * @param type specifies what types of buttons will this dialog have
	 * @param listener dialog buttons listener.
	 * @return dialog for the purpose of changing buttons text.
	 * @see OptionDialog
	 * @since 0.6.0
	 */
	public static OptionDialog showOptionDialog (Stage stage, String title, String text, OptionDialogType type, OptionDialogListener listener) {
		OptionDialog dialog = new OptionDialog(title, text, type, listener);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/**
	 * Dialog with title, text and n amount of buttons. If you need dialog with only buttons like Yes, No, Cancel then
	 * see {@link #showOptionDialog(Stage, String, String, OptionDialogType, OptionDialogListener)}.
	 * <p>
	 * @param title dialog title.
	 * @param listener button listener for this dialog. This dialog is generic, listener type will depend on
	 * 'returns' param type.
	 * @since 0.7.0
	 */
	public static <T> ConfirmDialog<T> showConfirmDialog (Stage stage, String title, String text, String[] buttons, T[] returns, ConfirmDialogListener<T> listener) {
		ConfirmDialog<T> dialog = new ConfirmDialog<T>(title, text, buttons, returns, listener);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/**
	 * Dialog with text and text field for user input. Cannot be canceled.
	 * @param title dialog title.
	 * @param fieldTitle displayed before input field, may be null.
	 * @param listener dialog buttons listener.
	 */
	public static InputDialog showInputDialog (Stage stage, String title, String fieldTitle, InputDialogListener listener) {
		InputDialog dialog = new InputDialog(title, fieldTitle, true, null, listener);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/**
	 * Dialog with text and text field for user input. Cannot be canceled.
	 * @param title dialog title.
	 * @param fieldTitle displayed before input field, may be null.
	 * @param validator used to validate user input. Eg. limit input to integers only. See {@link Validators} for built-in validators.
	 * @param listener dialog buttons listener.
	 */
	public static InputDialog showInputDialog (Stage stage, String title, String fieldTitle, InputValidator validator, InputDialogListener listener) {
		InputDialog dialog = new InputDialog(title, fieldTitle, true, validator, listener);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/**
	 * Dialog with text and text field for user input.
	 * @param title dialog title.
	 * @param cancelable if true dialog may be canceled by user.
	 * @param fieldTitle displayed before input field, may be null.
	 * @param listener dialog buttons listener.
	 */
	public static InputDialog showInputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputDialogListener listener) {
		InputDialog dialog = new InputDialog(title, fieldTitle, cancelable, null, listener);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/**
	 * Dialog with text and text field for user input.
	 * @param title dialog title
	 * @param validator used to validate user input, can be used to easily limit input to int etc. See {@link Validators} for premade validators.
	 * @param cancelable if true dialog may be canceled.
	 * @param fieldTitle displayed before input field, may be null.
	 */
	public static InputDialog showInputDialog (Stage stage, String title, String fieldTitle, boolean cancelable, InputValidator validator, InputDialogListener listener) {
		InputDialog dialog = new InputDialog(title, fieldTitle, cancelable, validator, listener);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/** Dialog with title "Error" and provided text. */
	public static ErrorDialog showErrorDialog (Stage stage, String text) {
		return showErrorDialog(stage, text, (String) null);
	}

	/** Dialog with title "Error", provided text and exception stacktrace available after pressing 'Details' button. */
	public static ErrorDialog showErrorDialog (Stage stage, String text, Exception exception) {
		if (exception == null)
			return showErrorDialog(stage, text, (String) null);
		else
			return showErrorDialog(stage, text, getStackTrace(exception));
	}

	/** Dialog with title "Error", provided text, and provided details available after pressing 'Details' button. */
	public static ErrorDialog showErrorDialog (Stage stage, String text, String details) {
		ErrorDialog dialog = new ErrorDialog(text, details);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	private static VisScrollPane createScrollPane (Actor widget) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setOverscroll(false, true);
		scrollPane.setFadeScrollBars(false);
		return scrollPane;
	}

	private static String getStackTrace (Throwable throwable) {
		StringBuilder builder = new StringBuilder();
		getStackTrace(throwable, builder);
		return builder.toString();
	}

	private static void getStackTrace (Throwable throwable, StringBuilder builder) {
		String msg = throwable.getMessage();
		if (msg != null) {
			builder.append(msg);
			builder.append("\n\n");
		}

		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append(element);
			builder.append("\n");
		}

		if (throwable.getCause() != null) {
			builder.append("\nCaused by: ");
			getStackTrace(throwable.getCause(), builder);
		}
	}

	public enum OptionDialogType {
		YES_NO, YES_NO_CANCEL, YES_CANCEL
	}

	/**
	 * Dialog with input field and optional {@link InputValidator}. Can be used directly although you should use {@link DialogUtils}
	 * showInputDialog methods.
	 */
	public static class InputDialog extends VisWindow {
		private InputDialogListener listener;
		private VisTextField field;
		private VisTextButton okButton;
		private VisTextButton cancelButton;

		public InputDialog (String title, String fieldTitle, boolean cancelable, InputValidator validator, InputDialogListener listener) {
			super(title);
			this.listener = listener;

			TableUtils.setSpacingDefaults(this);
			setModal(true);

			if (cancelable) {
				addCloseButton();
				closeOnEscape();
			}

			VisTable buttonsTable = new VisTable(true);
			buttonsTable.add(cancelButton = new VisTextButton(Text.CANCEL.get()));
			buttonsTable.add(okButton = new VisTextButton(Text.OK.get()));

			VisTable fieldTable = new VisTable(true);

			if (validator == null)
				field = new VisTextField();
			else
				field = new VisValidatableTextField(validator);

			if (fieldTitle != null) fieldTable.add(new VisLabel(fieldTitle));

			fieldTable.add(field).expand().fill();

			add(fieldTable).padTop(3).spaceBottom(4);
			row();
			add(buttonsTable).padBottom(3);

			addListeners();

			if (validator != null) {
				addValidatableFieldListener(field);
				okButton.setDisabled(!field.isInputValid());
			}

			pack();
			centerWindow();
		}

		@Override
		protected void close () {
			super.close();
			listener.canceled();
		}

		@Override
		protected void setStage (Stage stage) {
			super.setStage(stage);
			if (stage != null)
				field.focusField();
		}

		public InputDialog setText (String text) {
			return setText(text, false);
		}

		/** @param selectText if true text will be selected (this can be useful if you want to allow user quickly erase all text). */
		public InputDialog setText (String text, boolean selectText) {
			field.setText(text);
			field.setCursorPosition(text.length());
			if (selectText) {
				field.selectAll();
			}

			return this;
		}

		private InputDialog addValidatableFieldListener (final VisTextField field) {
			field.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if (field.isInputValid()) {
						okButton.setDisabled(false);
					} else {
						okButton.setDisabled(true);
					}
				}
			});
			return this;
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

	/**
	 * Dialog with text and buttons like Yes, No, Cancel. Can be used directly although you should use {@link DialogUtils}
	 * showOptionDialog methods.
	 */
	public static class OptionDialog extends VisDialog {
		private OptionDialogListener listener;

		private VisTextButton yesButton = new VisTextButton(Text.YES.get());
		private VisTextButton noButton = new VisTextButton(Text.NO.get());
		private VisTextButton cancelButton = new VisTextButton(Text.CANCEL.get());

		public OptionDialog (String title, String text, OptionDialogType type, OptionDialogListener listener) {
			super(title);

			this.listener = listener;

			text(new VisLabel(text, Align.center));
			defaults().padBottom(3);

			switch (type) {
				case YES_NO:
					button(yesButton, BUTTON_YES);
					button(noButton, BUTTON_NO);
					break;
				case YES_CANCEL:
					button(yesButton, BUTTON_YES);
					button(cancelButton, BUTTON_CANCEL);
					break;
				case YES_NO_CANCEL:
					button(yesButton, BUTTON_YES);
					button(noButton, BUTTON_NO);
					button(cancelButton, BUTTON_CANCEL);
					break;
			}

			pack();
			centerWindow();
		}

		@Override
		protected void result (Object object) {
			int result = (Integer) object;

			if (result == BUTTON_YES) listener.yes();
			if (result == BUTTON_NO) listener.no();
			if (result == BUTTON_CANCEL) listener.cancel();
		}

		public OptionDialog setNoButtonText (String text) {
			noButton.setText(text);
			return this;
		}

		public OptionDialog setYesButtonText (String text) {
			yesButton.setText(text);
			return this;
		}

		public OptionDialog setCancelButtonText (String text) {
			cancelButton.setText(text);
			return this;
		}
	}

	/**
	 * Dialog with text and exception stacktrace available after pressing Details button.
	 * Can be used directly although you should use {@link DialogUtils} showErrorDialog methods.
	 */
	public static class ErrorDialog extends VisDialog {
		private VisTable detailsTable = new VisTable(true);
		private Cell<?> detailsCell;

		public ErrorDialog (String text, String stackTrace) {
			super(Text.ERROR.get());

			text(text);

			if (stackTrace != null) {
				final VisTextButton copyButton = new VisTextButton(Text.COPY.get());
				final VisLabel errorLabel = new VisLabel(stackTrace);

				Sizes sizes = VisUI.getSizes();

				copyButton.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						Gdx.app.getClipboard().setContents((errorLabel.getText().toString()));
						copyButton.setText(Text.COPIED.get());
					}
				});

				detailsTable.add(new VisLabel(Text.DETAILS_COLON.get())).left().expand().padTop(6);
				detailsTable.add(copyButton);
				detailsTable.row();

				VisTable errorTable = new VisTable();
				errorTable.add(errorLabel).top().expand().fillX();
				detailsTable.add(createScrollPane(errorTable)).colspan(2).minWidth(600 * sizes.scaleFactor).height(300 * sizes.scaleFactor);

				getContentTable().row();
				detailsCell = getContentTable().add(detailsTable);
				detailsCell.setActor(null);
				button(Text.DETAILS.get(), BUTTON_DETAILS);
			}

			button(Text.OK.get(), BUTTON_OK).padBottom(3);
			pack();
			centerWindow();
		}

		@Override
		protected void result (Object object) {
			int result = (Integer) object;

			if (result == BUTTON_DETAILS) {
				detailsCell.setActor(detailsCell.hasActor() ? null : detailsTable);
				pack();
				centerWindow();
				cancel();
			}
		}
	}

	/**
	 * Dialog with title, text and n amount of buttons. Can be used directly although you should use {@link DialogUtils}
	 * showConfirmDialog methods.
	 * @author Javier
	 * @author Kotcrab
	 */
	public static class ConfirmDialog<T> extends VisDialog {
		private ConfirmDialogListener<T> listener;

		public ConfirmDialog (String title, String text, String[] buttons, T[] returns, ConfirmDialogListener<T> listener) {
			super(title);

			if (buttons.length != returns.length) {
				throw new IllegalStateException("buttons.length must be equal to returns.length");
			}

			this.listener = listener;

			text(new VisLabel(text, Align.center));
			defaults().padBottom(3);

			for (int i = 0; i < buttons.length; i++) {
				button(buttons[i], returns[i]);
			}

			padBottom(3);
			pack();
			centerWindow();
		}

		@Override
		protected void result (Object object) {
			listener.result((T) object);
		}
	}

	/** {@link DialogUtils} I18N properties. */
	private enum Text implements BundleText {
		YES("yes"),
		NO("no"),
		CANCEL("cancel"),
		OK("ok"),
		ERROR("error"),

		DETAILS("details"),
		DETAILS_COLON("detailsColon"),
		COPY("copy"),
		COPIED("copied");

		private final String name;

		Text (final String name) {
			this.name = name;
		}

		private static I18NBundle getBundle () {
			return VisUI.getDialogUtilsBundle();
		}

		@Override
		public final String getName () {
			return name;
		}

		@Override
		public final String get () {
			return getBundle().get(name);
		}

		@Override
		public final String format () {
			return getBundle().format(name);
		}

		@Override
		public final String format (final Object... arguments) {
			return getBundle().format(name, arguments);
		}

		@Override
		public final String toString () {
			return get();
		}
	}

}
