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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter.DigitsOnlyFilter;

/**
 * @author Javier, Kotcrab
 * @since 0.7.0
 */
public class NumberSelector extends VisTable {
	private Array<NumberSelectorListener> listeners = new Array<NumberSelectorListener>();

	private VisValidatableTextField valueText;

	private ButtonRepeatTask buttonRepeatTask = new ButtonRepeatTask();
	private float buttonRepeatInitialTime = 0.4f;
	private float buttonRepeatTime = 0.08f;

	private boolean programmaticChangeEvents = true;

	private float max;
	private float min;
	private float step;
	private float current;
	private int precision = 0;

	/** Creates number selector with step set to 1 */
	public NumberSelector (String name, float initialValue, float min, float max) {
		this(name, initialValue, min, max, 1);
	}

	public NumberSelector (String name, float initialValue, float min, float max, float step) {
		this("default", name, initialValue, min, max, step);
	}

	public NumberSelector (String styleName, String name, float initialValue, float min, float max, float step) {
		this(VisUI.getSkin().get(styleName, NumberSelectorStyle.class), VisUI.getSizes(), name, initialValue, min, max, step);
	}

	public NumberSelector (NumberSelectorStyle style, final Sizes sizes, String name, float initialValue, float min, float max, float step) {
		this.current = initialValue;
		this.max = max;
		this.min = min;
		this.step = step;

		valueText = new VisValidatableTextField(Validators.INTEGERS) {
			@Override
			public float getPrefWidth () {
				return sizes.numberSelectorFieldSize;
			}
		};

		valueText.setProgrammaticChangeEvents(false);
		valueText.setTextFieldFilter(new DigitsOnlyFilter());
		valueText.setText(valueOf(current));
		valueText.addValidator(new InputValidator() {
			@Override
			public boolean validateInput (String input) {
				return checkInput(input);
			}
		});

		VisTable buttonsTable = new VisTable();
		VisImageButton up = new VisImageButton(style.up);
		VisImageButton down = new VisImageButton(style.down);

		buttonsTable.add(up).height(sizes.numberSelectorButtonSize).row();
		buttonsTable.add(down).height(sizes.numberSelectorButtonSize);

		int padding = 0;
		if (name != null && name.length() != 0) {
			add(name);
			padding = 6;
		}

		add(valueText).fillX().expandX().height(sizes.numberSelectorButtonSize * 2).padLeft(padding).padRight(1);
		add(buttonsTable).width(sizes.numberSelectorButtonsWidth);

		up.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().setScrollFocus(valueText);
				increment(true);
			}
		});

		down.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().setScrollFocus(valueText);
				decrement(true);
			}
		});

		up.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (buttonRepeatTask.isScheduled() == false) {
					buttonRepeatTask.increment = true;
					buttonRepeatTask.cancel();
					Timer.schedule(buttonRepeatTask, buttonRepeatInitialTime, buttonRepeatTime);
				}

				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				buttonRepeatTask.cancel();
			}
		});

		down.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (buttonRepeatTask.isScheduled() == false) {
					buttonRepeatTask.increment = false;
					buttonRepeatTask.cancel();
					Timer.schedule(buttonRepeatTask, buttonRepeatInitialTime, buttonRepeatTime);
				}

				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				buttonRepeatTask.cancel();
			}
		});

		valueText.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				textChanged();
			}
		});

		valueText.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (focused == false) {
					valueChanged(true);
					getStage().setScrollFocus(null);
				}
			}
		});

		valueText.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				getStage().setScrollFocus(valueText);
				return true;
			}

			@Override
			public boolean scrolled (InputEvent event, float x, float y, int amount) {
				if (amount == 1)
					decrement(true);
				else
					increment(true);

				return true;
			}

			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					valueChanged(true);
					return true;
				}

				return false;
			}
		});
	}

	/**
	 * Sets precision of this selector. Precision defines how many digits after decimal point can be entered. By default
	 * this is set to 0, meaning that only integers are allowed. Setting precision to 1 would allow 0.0, precision = 2 would
	 * allow 0.00 and etc.
	 */
	public void setPrecision (final int precision) {
		if (precision < 0) throw new IllegalStateException("Precision can't be < 0");
		this.precision = precision;

		valueText.getValidators().clear();
		if (precision == 0) {
			valueText.addValidator(Validators.INTEGERS);
			valueText.setTextFieldFilter(new DigitsOnlyFilter());
		} else {
			valueText.addValidator(Validators.FLOATS);
			valueText.addValidator(new InputValidator() {
				@Override
				public boolean validateInput (String input) {
					int dotIndex = input.indexOf('.');
					if (dotIndex == -1) return true;
					return input.length() - input.indexOf('.') - 1 <= precision;
				}
			});
			valueText.setTextFieldFilter(new FloatDigitsOnlyFilter(true));
		}
	}

	public int getPrecision () {
		return precision;
	}

	private void textChanged () {
		if (valueText.getText().equals(""))
			current = min;
		else if (checkInput(valueText.getText()))
			current = Float.parseFloat(valueText.getText());
	}

	public void increment () {
		increment(programmaticChangeEvents);
	}

	private void increment (boolean fireEvent) {
		if (current + step > max)
			this.current = max;
		else
			this.current += step;

		valueChanged(fireEvent);
	}

	public void decrement () {
		decrement(programmaticChangeEvents);
	}

	private void decrement (boolean fireEvent) {
		if (current - step < min)
			this.current = min;
		else
			this.current -= step;

		valueChanged(fireEvent);
	}

	public void setValue (float newValue) {
		setValue(newValue, programmaticChangeEvents);
	}

	public void setValue (float newValue, boolean fireEvent) {
		if (newValue > max)
			current = max;
		else if (newValue < min)
			current = min;
		else
			current = newValue;

		valueChanged(fireEvent);
	}

	public float getValue () {
		return current;
	}

	public float getMin () {
		return min;
	}

	/** Sets min value, if current is lesser than min, the current value is set to min value */
	public void setMin (float min) {
		this.min = min;

		if (current < min) {
			current = min;
			valueChanged(true);
		}
	}

	public float getMax () {
		return max;
	}

	/** Sets max value, if current is greater than max, the current value is set to max value */
	public void setMax (float max) {
		this.max = max;

		if (current > max) {
			current = max;
			valueChanged(true);
		}
	}

	/**
	 * If false, {@link #setValue(float)}, {@link #decrement()} and {@link #increment()} will not fire change event,
	 * it will be fired only when user changed value
	 */
	public void setProgrammaticChangeEvents (boolean programmaticChangeEvents) {
		this.programmaticChangeEvents = programmaticChangeEvents;
	}

	public float getStep () {
		return step;
	}

	public void setStep (float step) {
		this.step = step;
	}

	private boolean checkInput (String input) {
		try {
			float x = Float.parseFloat(input);
			return x >= min && x <= max;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void valueChanged (boolean fireEvent) {
		valueText.setText(valueOf(current));
		valueText.setCursorPosition(valueText.getText().length());

		if (fireEvent) {
			for (NumberSelectorListener listener : listeners)
				listener.changed(current);
		}
	}

	private String valueOf (float current) {
		if (current == MathUtils.floor(current))
			return String.valueOf((int) current);
		else
			return String.valueOf(current);
	}

	public void addChangeListener (NumberSelectorListener listener) {
		if (listener != null && !listeners.contains(listener, true)) {
			listeners.add(listener);
		}
	}

	public boolean removeChangeListener (NumberSelectorListener listener) {
		return listeners.removeValue(listener, true);
	}

	public static class NumberSelectorStyle {
		public Drawable up;
		public Drawable down;

		public NumberSelectorStyle () {
		}

		public NumberSelectorStyle (Drawable up, Drawable down) {
			this.up = up;
			this.down = down;
		}
	}

	public interface NumberSelectorListener {
		void changed (float number);
	}

	private class ButtonRepeatTask extends Task {
		boolean increment;

		@Override
		public void run () {
			if (increment)
				increment(true);
			else
				decrement(true);
		}
	}
}
