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
import com.badlogic.gdx.utils.CharArray;
import com.badlogic.gdx.utils.I18NBundle;
import com.kotcrab.vis.ui.Locales;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.i18n.BundleText;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.ButtonBar.ButtonType;

/**
 * Utilities for displaying various type of dialogs. Equivalent of JOptionPane from Swing.
 * @author Kotcrab
 * @since 0.2.0
 */
public class Dialogs {
	private static final int BUTTON_OK = 1;
	private static final int BUTTON_DETAILS = 2;

	/**
	 * Dialog with given text and single OK button.
	 * @param title dialog title
	 */
	public static VisDialog showOKDialog (Stage stage, String title, String text) {
		final VisDialog dialog = new VisDialog(title);
		dialog.closeOnEscape();
		dialog.text(text);
		dialog.button(ButtonType.OK.getText()).padBottom(3);
		dialog.pack();
		dialog.centerWindow();
		dialog.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					dialog.fadeOut();
					return true;
				}
				return false;
			}
		});
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
	public static DetailsDialog showErrorDialog (Stage stage, String text) {
		return showErrorDialog(stage, text, (String) null);
	}

	/** Dialog with title "Error", provided text and exception stacktrace available after pressing 'Details' button. */
	public static DetailsDialog showErrorDialog (Stage stage, String text, Throwable exception) {
		if (exception == null)
			return showErrorDialog(stage, text, (String) null);
		else
			return showErrorDialog(stage, text, getStackTrace(exception));
	}

	/** Dialog with title "Error", provided text, and provided details available after pressing 'Details' button. */
	public static DetailsDialog showErrorDialog (Stage stage, String text, String details) {
		DetailsDialog dialog = new DetailsDialog(text, Text.ERROR.get(), details);
		stage.addActor(dialog.fadeIn());
		return dialog;
	}

	/** Dialog with given title, provided text, and more details available after pressing 'Details' button. */
	public static DetailsDialog showDetailsDialog (Stage stage, String text, String title, String details) {
		return showDetailsDialog(stage, text, title, details, false);
	}

	/**
	 * Dialog with given title, provided text, and more details available after pressing 'Details' button.
	 * @param expandDetails if true details will be visible without need to press 'Details' button
	 */
	public static DetailsDialog showDetailsDialog (Stage stage, String text, String title, String details, boolean expandDetails) {
		DetailsDialog dialog = new DetailsDialog(text, title, details);
		dialog.setDetailsVisible(expandDetails);
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
		CharArray builder = new CharArray();
		getStackTrace(throwable, builder);
		return builder.toString();
	}

	private static void getStackTrace (Throwable throwable, CharArray builder) {
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
	 * Dialog with input field and optional {@link InputValidator}. Can be used directly although you should use {@link Dialogs}
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

			ButtonBar buttonBar = new ButtonBar();
			buttonBar.setIgnoreSpacing(true);
			buttonBar.setButton(ButtonType.CANCEL, cancelButton = new VisTextButton(ButtonType.CANCEL.getText()));
			buttonBar.setButton(ButtonType.OK, okButton = new VisTextButton(ButtonType.OK.getText()));

			VisTable fieldTable = new VisTable(true);

			if (validator == null)
				field = new VisTextField();
			else
				field = new VisValidatableTextField(validator);

			if (fieldTitle != null) fieldTable.add(new VisLabel(fieldTitle));

			fieldTable.add(field).expand().fill();

			add(fieldTable).padTop(3).spaceBottom(4);
			row();
			add(buttonBar.createTable()).padBottom(3);

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
	 * Dialog with text and buttons like Yes, No, Cancel. Can be used directly although you should use {@link Dialogs}
	 * showOptionDialog methods.
	 */
	public static class OptionDialog extends VisWindow {
		//NOTE: when updating this class, don't forget about Editor's DisableableOptionDialog
		private final ButtonBar buttonBar;

		public OptionDialog (String title, String text, OptionDialogType type, final OptionDialogListener listener) {
			super(title);

			setModal(true);

			add(new VisLabel(text, Align.center));
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

			add(buttonBar.createTable());

			pack();
			centerWindow();
		}

		public OptionDialog setNoButtonText (String text) {
			buttonBar.getTextButton(ButtonType.NO).setText(text);
			pack();
			return this;
		}

		public OptionDialog setYesButtonText (String text) {
			buttonBar.getTextButton(ButtonType.YES).setText(text);
			pack();
			return this;
		}

		public OptionDialog setCancelButtonText (String text) {
			buttonBar.getTextButton(ButtonType.CANCEL).setText(text);
			pack();
			return this;
		}
	}

	/**
	 * Dialog with text and exception stacktrace available after pressing Details button.
	 * Can be used directly although you should use {@link Dialogs} showErrorDialog methods.
	 */
	public static class DetailsDialog extends VisDialog {
		private VisTable detailsTable = new VisTable(true);
		private Cell<?> detailsCell;
		private boolean detailsVisible;

		private VisTextButton copyButton;
		private VisLabel detailsLabel;

		public DetailsDialog (String text, String title, String details) {
			super(title);

			text(text);

			if (details != null) {
				copyButton = new VisTextButton(Text.COPY.get());
				detailsLabel = new VisLabel(details);

				Sizes sizes = VisUI.getSizes();

				copyButton.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						Gdx.app.getClipboard().setContents((detailsLabel.getText().toString()));
						copyButton.setText(Text.COPIED.get());
					}
				});

				detailsTable.add(new VisLabel(Text.DETAILS_COLON.get())).left().expand().padTop(6);
				detailsTable.add(copyButton);
				detailsTable.row();

				VisTable detailsTable = new VisTable();
				detailsTable.add(detailsLabel).top().expand().fillX();
				this.detailsTable.add(createScrollPane(detailsTable)).colspan(2).minWidth(600 * sizes.scaleFactor).height(300 * sizes.scaleFactor);

				getContentTable().row();
				detailsCell = getContentTable().add(this.detailsTable);
				detailsCell.setActor(null);
				button(Text.DETAILS.get(), BUTTON_DETAILS);
			}

			button(ButtonType.OK.getText(), BUTTON_OK).padBottom(3);
			pack();
			centerWindow();
		}

		@Override
		protected void result (Object object) {
			int result = (Integer) object;

			if (result == BUTTON_DETAILS) {
				setDetailsVisible(!detailsVisible);
				cancel();
			}
		}

		public void setWrapDetails (boolean wrap) {
			detailsLabel.setWrap(wrap);
		}

		public void setCopyDetailsButtonVisible (boolean visible) {
			copyButton.setVisible(visible);
		}

		public boolean isCopyDetailsButtonVisible () {
			return copyButton.isVisible();
		}

		/**
		 * Changes visibility of details pane. Note that Window must be added to Stage or Window won't be packed properly and
		 * it's size will be wrong. If Window is not added to Stage packing will be performed next frame, if it is still
		 * not added at that point, Window size will be incorrect.
		 */
		public void setDetailsVisible (boolean visible) {
			if (detailsVisible == visible) return;
			detailsVisible = visible;
			detailsCell.setActor(detailsCell.hasActor() ? null : detailsTable);

			//looks like Stage is required to properly pack window
			//if it's null do packing next frame and hope that window have been already added to Stage at that point
			if (getStage() == null) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run () {
						pack();
						centerWindow();
					}
				});
			} else {
				pack();
				centerWindow();
			}
		}

		public boolean isDetailsVisible () {
			return detailsVisible;
		}
	}

	/**
	 * Dialog with title, text and n amount of buttons. Can be used directly although you should use {@link Dialogs}
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

	/** {@link Dialogs} I18N properties. */
	private enum Text implements BundleText {
		DETAILS("details"),
		DETAILS_COLON("detailsColon"),
		COPY("copy"),
		COPIED("copied"),
		ERROR("error");

		private final String name;

		Text (final String name) {
			this.name = name;
		}

		private static I18NBundle getBundle () {
			return Locales.getDialogsBundle();
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
