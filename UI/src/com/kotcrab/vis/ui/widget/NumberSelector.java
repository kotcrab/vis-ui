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

package com.kotcrab.vis.ui.widget;

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
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter.DigitsOnlyFilter;

/**
 * @author Javier, Kotcrab
 * @since 0.7.0
 */
public class NumberSelector extends VisTable {
	private VisValidableTextField text;

	private int max;
	private int min;
	private int step;
	private int current;

	private ButtonRepeatTask buttonRepeatTask = new ButtonRepeatTask();
	private float buttonRepeatInitialTime = 0.4f;
	private float buttonRepeatTime = 0.1f;

	private Array<NumberSelectorListener> listeners = new Array<NumberSelectorListener>();

	public NumberSelector (String name, int initialValue, int min, int max, int step) {
		this("default", name, initialValue, min, max, step);
	}

	public NumberSelector (String styleName, String name, int initialValue, int min, int max, int step) {
		this(VisUI.getSkin().get(styleName, NumberSelectorStyle.class), name, initialValue, min, max, step);
	}

	public NumberSelector (NumberSelectorStyle style, String name, int initialValue, int min, int max, int step) {
		this.current = initialValue;
		this.max = max;
		this.min = min;
		this.step = step;

		VisTable buttonsTable = new VisTable();
		VisImageButton up = new VisImageButton(style.up);
		VisImageButton down = new VisImageButton(style.down);

		text = new VisValidableTextField(Validators.INTEGERS) {
			@Override
			public float getPrefWidth () {
				return 40;
			}
		};

		text.setProgrammaticChangeEvents(false);
		text.setTextFieldFilter(new DigitsOnlyFilter());
		text.setText(String.valueOf(current));
		text.addValidator(new InputValidator() {
			@Override
			public boolean validateInput (String input) {
				return checkInput(input);
			}
		});

		buttonsTable.add(up).height(24 / 2).row();
		buttonsTable.add(down).height(24 / 2);

		add(name);
		add(text).fillX().expandX().height(24).padLeft(6).padRight(1);
		add(buttonsTable).width(12);

		up.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().setScrollFocus(text);
				increment();
			}
		});

		down.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().setScrollFocus(text);
				decrement();
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

		text.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				textChanged();
			}
		});

		text.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				valueChanged();
				getStage().setScrollFocus(null);
			}
		});

		text.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				getStage().setScrollFocus(text);
				return true;
			}

			@Override
			public boolean scrolled (InputEvent event, float x, float y, int amount) {
				if (amount == 1)
					decrement();
				else
					increment();

				return true;
			}
		});
	}

	private void textChanged () {
		if (text.getText().equals("")) return;

		if (checkInput(text.getText()))
			current = Integer.parseInt(text.getText());
		else
			valueChanged(); //will restore old vlaue
	}

	public void increment () {
		if (current + step > max)
			this.current = max;
		else
			this.current += step;

		valueChanged();
	}

	public void decrement () {
		if (current - step < min)
			this.current = min;
		else
			this.current -= step;

		valueChanged();
	}

	public int getValue () {
		return current;
	}

	private boolean checkInput (String input) {
		try {
			int x = Integer.parseInt(input);
			return x >= min && x <= max;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void valueChanged () {
		int pos = text.getCursorPosition();
		text.setText(String.valueOf(current));
		text.setCursorPosition(pos);

		for (NumberSelectorListener listener : listeners)
			listener.changed(current);
	}

	public void addChangeListener (NumberSelectorListener listener) {
		if (listener != null && !listeners.contains(listener, true)) {
			listeners.add(listener);
		}
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
		void changed (int number);
	}

	private class ButtonRepeatTask extends Task {
		boolean increment;

		@Override
		public void run () {
			if (increment)
				increment();
			else
				decrement();
		}
	}
}
