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

package com.kotcrab.vis.ui.widget.spinner;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

/**
 * Spinner can be used to select number or object using up and down buttons or by entering value into text field.
 * Supports custom models that allows selecting either int, floats or even custom objects.
 * <p>
 * Fires {@link ChangeListener.ChangeEvent} when value has changed however unlike some other widgets canceling the event
 * won't undo value change.
 * @author Kotcrab
 * @see SimpleFloatSpinnerModel
 * @see FloatSpinnerModel
 * @see IntSpinnerModel
 * @see ArraySpinnerModel
 * @since 1.0.2
 */
public class Spinner extends VisTable {
	private final Sizes sizes;

	private SpinnerModel model;

	//task is shared between two buttons
	private ButtonRepeatTask buttonRepeatTask = new ButtonRepeatTask();

	private VisImageButton upButton;
	private VisImageButton downButton;
	private Cell<VisValidatableTextField> textFieldCell;
	private Cell<VisLabel> labelCell;

	private TextFieldEventPolicy textFieldEventPolicy = TextFieldEventPolicy.ON_FOCUS_LOST;
	private boolean programmaticChangeEvents = true;
	private boolean disabled;

	public Spinner (String name, SpinnerModel model) {
		this("default", name, model);
	}

	public Spinner (String styleName, String name, SpinnerModel model) {
		this(VisUI.getSkin().get(styleName, SpinnerStyle.class), VisUI.getSizes(), name, model);
	}

	public Spinner (SpinnerStyle style, Sizes sizes, String name, SpinnerModel model) {
		this.sizes = sizes;
		this.model = model;

		VisTable buttonsTable = new VisTable();
		VisValidatableTextField textField = createTextField();
		upButton = new VisImageButton(style.up);
		downButton = new VisImageButton(style.down);
		buttonsTable.add(upButton).height(sizes.spinnerButtonHeight).row();
		buttonsTable.add(downButton).height(sizes.spinnerButtonHeight);

		labelCell = add(new VisLabel(""));
		setSelectorName(name);

		textFieldCell = add(textField).height(sizes.spinnerButtonHeight * 2).growX();
		add(buttonsTable);

		addButtonsListeners(upButton, downButton);

		model.bind(this);
	}

	private VisValidatableTextField createTextField () {
		VisValidatableTextField textField = new VisValidatableTextField() {
			@Override
			public float getPrefWidth () {
				return sizes.spinnerFieldSize;
			}
		};
		textField.setRestoreLastValid(true);
		textField.setProgrammaticChangeEvents(false);
		addTextFieldListeners(textField);
		return textField;
	}

	public void setModel (SpinnerModel model) {
		this.model = model;
		textFieldCell.setActor(createTextField());
		model.bind(this);
	}

	private void addButtonsListeners (VisImageButton upButton, VisImageButton downButton) {
		upButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				event.stop();
				getStage().setScrollFocus(getTextField());
				increment(true);
			}
		});

		downButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				event.stop();
				getStage().setScrollFocus(getTextField());
				decrement(true);
			}
		});

		upButton.addListener(new ButtonInputListener(true));
		downButton.addListener(new ButtonInputListener(false));
	}

	private void addTextFieldListeners (final VisTextField textField) {
		textField.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				event.stop();
				model.textChanged();
				if (textField.isInputValid() && textFieldEventPolicy == TextFieldEventPolicy.ON_KEY_TYPED) {
					notifyValueChanged(true);
				}
			}
		});

		textField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (focused == false) {
					getStage().setScrollFocus(null);
					if (textFieldEventPolicy == TextFieldEventPolicy.ON_FOCUS_LOST) {
						notifyValueChanged(true);
					}
				}
			}
		});

		textField.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				getStage().setScrollFocus(getTextField());
				return true;
			}

			@Override
			public boolean scrolled (InputEvent event, float x, float y, int amount) {
				if (disabled) return false;
				if (amount == 1) {
					decrement(true);
				} else {
					increment(true);
				}

				return true;
			}

			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					notifyValueChanged(true);
					return true;
				}

				return false;
			}
		});
	}

	public void setDisabled (boolean disabled) {
		this.disabled = disabled;
		upButton.setDisabled(disabled);
		downButton.setDisabled(disabled);
		getTextField().setDisabled(disabled);
	}

	public boolean isDisabled () {
		return disabled;
	}

	public void setSelectorName (String name) {
		labelCell.getActor().setText(name);
		if (name == null || name.length() == 0) {
			labelCell.padRight(0);
		} else {
			labelCell.padRight(6);
		}
	}

	public String getSelectorName () {
		return super.getName();
	}

	public void increment () {
		model.increment(programmaticChangeEvents);
	}

	private void increment (boolean fireEvent) {
		model.increment(fireEvent);
	}

	public void decrement () {
		model.decrement(programmaticChangeEvents);
	}

	private void decrement (boolean fireEvent) {
		model.decrement(fireEvent);
	}

	/** If false, methods changing spinner value form code won't trigger change event, it will be fired only when user has changed value. */
	public void setProgrammaticChangeEvents (boolean programmaticChangeEvents) {
		this.programmaticChangeEvents = programmaticChangeEvents;
	}

	public boolean isProgrammaticChangeEvents () {
		return programmaticChangeEvents;
	}

	public void setTextFieldEventPolicy (TextFieldEventPolicy textFieldEventPolicy) {
		this.textFieldEventPolicy = textFieldEventPolicy;
	}

	public TextFieldEventPolicy getTextFieldEventPolicy () {
		return textFieldEventPolicy;
	}

	public int getMaxLength () {
		return getTextField().getMaxLength();
	}

	public void setMaxLength (int maxLength) {
		getTextField().setMaxLength(maxLength);
	}

	public SpinnerModel getModel () {
		return model;
	}

	/**
	 * Called by {@link SpinnerModel}. Notifies when underlying model value has changed and spinner text field must updated.
	 * Typically there is no need to call this method manually.
	 * @param fireEvent if true then {@link ChangeListener.ChangeEvent} will be fired
	 */
	public void notifyValueChanged (boolean fireEvent) {
		VisValidatableTextField textField = getTextField();
		int cursor = textField.getCursorPosition();
		textField.setCursorPosition(0);
		textField.setText(model.getText());
		textField.setCursorPosition(cursor);

		if (fireEvent) {
			ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
			fire(changeEvent);
			Pools.free(changeEvent);
		}
	}

	public VisValidatableTextField getTextField () {
		return textFieldCell.getActor();
	}

	public static class SpinnerStyle {
		public Drawable up;
		public Drawable down;

		public SpinnerStyle () {
		}

		public SpinnerStyle (SpinnerStyle style) {
			this.up = style.up;
			this.down = style.down;
		}

		public SpinnerStyle (Drawable up, Drawable down) {
			this.up = up;
			this.down = down;
		}
	}

	private class ButtonRepeatTask extends Task {
		boolean advance;

		@Override
		public void run () {
			if (advance) {
				increment(true);
			} else {
				decrement(true);
			}
		}
	}

	/**
	 * Allows to configure how {@link Spinner} will fire {@link ChangeListener.ChangeEvent} after user interaction with
	 * Spinner text field.
	 * @since 1.1.6
	 */
	public enum TextFieldEventPolicy {
		/**
		 * Spinner change event will be only fired after user has pressed enter in text field. This mode is the default
		 * one prior to VisUI 1.1.6
		 */
		ON_ENTER_ONLY,
		/**
		 * Spinner change event will be always fired after text field has lost focus and entered value is valid. Note
		 * that event will be fired even if user has not changed actual value of spinner. Event won't be fired
		 * if current model determined that entered value is invalid. This mode is the default one.
		 */
		ON_FOCUS_LOST,
		/**
		 * Spinner change event will be fired right after user has typed something in the text field and model has
		 * determined that entered value is valid. Event won't be fired if entered value is invalid.
		 */
		ON_KEY_TYPED
	}

	private class ButtonInputListener extends InputListener {
		private float buttonRepeatInitialTime = 0.4f;
		private float buttonRepeatTime = 0.08f;

		private boolean advance;

		public ButtonInputListener (boolean advance) {
			this.advance = advance;
		}

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if (buttonRepeatTask.isScheduled() == false) {
				buttonRepeatTask.advance = advance;
				buttonRepeatTask.cancel();
				Timer.schedule(buttonRepeatTask, buttonRepeatInitialTime, buttonRepeatTime);
			}
			return true;
		}

		@Override
		public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			buttonRepeatTask.cancel();
		}
	}
}
