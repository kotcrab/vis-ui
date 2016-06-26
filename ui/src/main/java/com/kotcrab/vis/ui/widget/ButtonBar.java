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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.Locales;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.OsUtils;

/**
 * Convenient class for creating button panels with buttons such as "Ok", "Cancel", "Yes" etc. Buttons are arranged in
 * platform dependent order. Built-in orders support Windows, Mac, and Linux. When no platform matches ButtonBar
 * defaults to Linux order.
 * User may specify custom order, see {@link ButtonType} for buttons ids.
 * @author Kotcrab
 * @since 1.0.0
 */
public class ButtonBar {
	public static final String WINDOWS_ORDER = "L H BEF YNOCA R";
	public static final String OSX_ORDER = "L H BEF NYCOA R";
	public static final String LINUX_ORDER = "L H NYACBEFO R";

	private Sizes sizes;

	private ObjectMap<Character, Button> buttons = new ObjectMap<Character, Button>();

	private boolean ignoreSpacing;
	private String order;

	public ButtonBar () {
		this(VisUI.getSizes(), getDefaultOrder());
	}

	public ButtonBar (String order) {
		this(VisUI.getSizes(), order);
	}

	public ButtonBar (Sizes sizes) {
		this(sizes, getDefaultOrder());
	}

	public ButtonBar (Sizes sizes, String order) {
		if (sizes == null) throw new IllegalArgumentException("sizes can't be null");
		this.sizes = sizes;
		setOrder(order);
	}

	private static String getDefaultOrder () {
		if (OsUtils.isWindows()) {
			return WINDOWS_ORDER;
		} else if (OsUtils.isMac()) {
			return OSX_ORDER;
		} else //default to linux order
			return LINUX_ORDER;
	}

	public boolean isIgnoreSpacing () {
		return ignoreSpacing;
	}

	/** @param ignoreSpacing if true spacing symbols in order will be ignored */
	public void setIgnoreSpacing (boolean ignoreSpacing) {
		this.ignoreSpacing = ignoreSpacing;
	}

	public String getOrder () {
		return order;
	}

	public void setOrder (String order) {
		if (order == null) throw new IllegalArgumentException("order can't be null");
		this.order = order;
	}

	public void setButton (ButtonType type, ChangeListener listener) {
		setButton(type, type.getText(), listener);
	}

	public void setButton (ButtonType type, String text, ChangeListener listener) {
		setButton(type, new VisTextButton(text), listener);
	}

	public void setButton (ButtonType type, Button button) {
		setButton(type, button, null);
	}

	public void setButton (ButtonType type, Button button, ChangeListener listener) {
		if (type == null) throw new IllegalArgumentException("type can't be null");
		if (button == null) throw new IllegalArgumentException("button can't be null");
		if (buttons.containsKey(type.id)) buttons.remove(type.id);
		buttons.put(type.id, button);
		if (listener != null) button.addListener(listener);
	}

	public Button getButton (ButtonType type) {
		return buttons.get(type.getId());
	}

	/**
	 * @return stored button casted to {@link VisTextButton}. Will throw {@link ClassCastException} in case stored button
	 * type is wrong. This may be safely used when button was created using {@link #setButton(ButtonType, String, ChangeListener)}.
	 */
	public VisTextButton getTextButton (ButtonType type) {
		return (VisTextButton) getButton(type);
	}

	/**
	 * Builds and returns {@link VisTable} containing buttons in platform dependant order. Note that calling this multiple
	 * times will remove buttons from previous tables.
	 */
	public VisTable createTable () {
		VisTable table = new VisTable(true);

		table.left();

		boolean spacingValid = false;
		for (int i = 0; i < order.length(); i++) {
			char ch = order.charAt(i);

			if (ignoreSpacing == false && ch == ' ' && spacingValid) {
				table.add().width(sizes.buttonBarSpacing);
				spacingValid = false;
			}

			Button button = buttons.get(ch);

			if (button != null) {
				table.add(button);
				spacingValid = true;
			}
		}

		return table;
	}

	/** Defines possible button types for {@link ButtonBar} */
	public enum ButtonType {
		LEFT("left", 'L'),
		RIGHT("right", 'R'),
		HELP("help", 'H'),
		NO("no", 'N'),
		YES("yes", 'Y'),
		CANCEL("cancel", 'C'),
		BACK("back", 'B'),
		NEXT("next", 'E'),
		APPLY("apply", 'A'),
		FINISH("finish", 'F'),
		OK("ok", 'O');

		private final String key;
		private final char id;

		ButtonType (String key, char id) {
			this.key = key;
			this.id = id;
		}

		public char getId () {
			return id;
		}

		public final String getText () {
			return Locales.getButtonBarBundle().get(key);
		}

		@Override
		public final String toString () {
			return getText();
		}
	}
}
