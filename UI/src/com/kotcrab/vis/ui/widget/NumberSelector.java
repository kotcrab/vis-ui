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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
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
import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.Validators;

import java.math.BigDecimal;

/**
 * NumberSelector can be used to select number using buttons or by entering it into text fields. Supports
 * mimimum and maximum value, step size. Both integer and floats are supported. When using float you can specify
 * selector precision, see {@link #setPrecision(int)}. Similar to JSpinner from Swing.
 * @author Javier, Kotcrab
 * @since 0.7.0
 */
public class NumberSelector extends VisTable {
	private Array<NumberSelectorListener> listeners = new Array<NumberSelectorListener>();

	private InputValidator boundsValidator = new BoundsValidator();
	private VisValidatableTextField valueText;
	private Cell<VisLabel> labelCell;

	private ButtonRepeatTask buttonRepeatTask = new ButtonRepeatTask();
	private float buttonRepeatInitialTime = 0.4f;
	private float buttonRepeatTime = 0.08f;

	private boolean programmaticChangeEvents = true;

	private float max;
	private float min;
	private float step;
	private float current;
	private int precision = 0;

	/**
	 * Creates integer number selector with step set to 1
	 * @param name may be null
	 */
	public NumberSelector (String name, float initialValue, float min, float max) {
		this(name, initialValue, min, max, 1);
	}

	/**
	 * Creates integer number selector
	 * @param name may be null
	 */
	public NumberSelector (String name, float initialValue, float min, float max, float step) {
		this("default", name, initialValue, min, max, step);
	}

	/**
	 * Creates integer or float number selector depending on precision, see {@link #setPrecision(int)}
	 * @param name may be null
	 */
	public NumberSelector (String name, float initialValue, float min, float max, float step, int precision) {
		this(VisUI.getSkin().get("default", NumberSelectorStyle.class), VisUI.getSizes(), name, initialValue, min, max, step, precision);
	}

	/**
	 * Creates integer number selector
	 * @param name may be null
	 */
	public NumberSelector (String styleName, String name, float initialValue, float min, float max, float step) {
		this(VisUI.getSkin().get(styleName, NumberSelectorStyle.class), VisUI.getSizes(), name, initialValue, min, max, step, 0);
	}

	/**
	 * Creates integer or float number selector depending on precision, see {@link #setPrecision(int)}
	 * @param name may be null
	 */
	public NumberSelector (String styleName, String name, float initialValue, float min, float max, float step, int precision) {
		this(VisUI.getSkin().get(styleName, NumberSelectorStyle.class), VisUI.getSizes(), name, initialValue, min, max, step, precision);
	}

	/**
	 * Creates integer number selector
	 * @param name may be null
	 */
	public NumberSelector (NumberSelectorStyle style, Sizes sizes, String name, float initialValue, float min, float max, float step) {
		this(style, sizes, name, initialValue, min, max, step, 0);
	}

	/**
	 * Creates integer or float number selector depending on precision, see {@link #setPrecision(int)}
	 * @param name may be null
	 */
	public NumberSelector (NumberSelectorStyle style, final Sizes sizes, String name, float initialValue, float min, float max, float step, int precision) {
		this.current = initialValue;
		this.max = max;
		this.min = min;
		this.step = step;

		valueText = new VisValidatableTextField() {
			@Override
			public float getPrefWidth () {
				return sizes.numberSelectorFieldSize;
			}
		};

		valueText.setProgrammaticChangeEvents(false);
		setPrecision(precision);
		valueText.setText(valueOf(current));

		VisTable buttonsTable = new VisTable();
		VisImageButton upButton = new VisImageButton(style.up);
		VisImageButton downButton = new VisImageButton(style.down);

		buttonsTable.add(upButton).height(sizes.numberSelectorButtonSize).row();
		buttonsTable.add(downButton).height(sizes.numberSelectorButtonSize);

		labelCell = add(new VisLabel(""));
		setSelectorName(name);

		add(valueText).fillX().expandX().height(sizes.numberSelectorButtonSize * 2).padRight(sizes.numberSelectorFieldRightPadding);
		add(buttonsTable).width(sizes.numberSelectorButtonsWidth);

		addButtonsListeners(upButton, downButton);
		addTextFieldListeners();
	}

	private void addButtonsListeners (VisImageButton upButton, VisImageButton downButton) {
		upButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().setScrollFocus(valueText);
				increment(true);
			}
		});

		downButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().setScrollFocus(valueText);
				decrement(true);
			}
		});

		upButton.addListener(new InputListener() {
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

		downButton.addListener(new InputListener() {
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
	}

	private void addTextFieldListeners () {
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

					try {
						float newValue = Float.valueOf(valueText.getText());
						setValue(newValue, true);
					} catch (NumberFormatException e) {
						//if entered value is invalid then restore last valid value
						valueChanged(true);
					}

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
		valueText.addValidator(boundsValidator); //Both need the bounds check
		if (precision == 0) {
			valueText.addValidator(Validators.INTEGERS);
			valueText.setTextFieldFilter(new IntDigitsOnlyFilter(true));
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

	private void textChanged () {
		if (valueText.getText().equals("")) {
			current = min;
		} else if (checkInputBounds(valueText.getText())) {
			current = Float.parseFloat(valueText.getText());
		}
	}

	public void increment () {
		increment(programmaticChangeEvents);
	}

	private void increment (boolean fireEvent) {
		if (current + step > max) {
			this.current = max;
		} else {
			this.current += step;
		}

		valueChanged(fireEvent);
	}

	public void decrement () {
		decrement(programmaticChangeEvents);
	}

	private void decrement (boolean fireEvent) {
		if (current - step < min) {
			this.current = min;
		} else {
			this.current -= step;
		}

		valueChanged(fireEvent);
	}

	public void setValue (float newValue) {
		setValue(newValue, programmaticChangeEvents);
	}

	public void setValue (float newValue, boolean fireEvent) {
		if (newValue > max) {
			current = max;
		} else if (newValue < min) {
			current = min;
		} else {
			current = newValue;
		}

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

	private boolean checkInputBounds (String input) {
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
			for (NumberSelectorListener listener : listeners) {
				listener.changed(current);
			}
		}
	}

	private String valueOf (float current) {
		if (precision >= 1) {
			//dealing with float rounding errors
			BigDecimal bd = new BigDecimal(String.valueOf(current));
			bd = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
			return String.valueOf(bd.floatValue());
		} else {
			return String.valueOf((int) current);
		}
	}

	public void addChangeListener (NumberSelectorListener listener) {
		if (listener != null && !listeners.contains(listener, true)) {
			listeners.add(listener);
		}
	}

	public boolean removeChangeListener (NumberSelectorListener listener) {
		return listeners.removeValue(listener, true);
	}

	public interface NumberSelectorListener {
		void changed (float number);
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

	private class BoundsValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			return checkInputBounds(input);
		}
	}

	private class ButtonRepeatTask extends Task {
		boolean increment;

		@Override
		public void run () {
			if (increment) {
				increment(true);
			} else {
				decrement(true);
			}
		}
	}
}
