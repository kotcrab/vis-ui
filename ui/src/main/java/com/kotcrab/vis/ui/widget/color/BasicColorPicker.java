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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ColorUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.color.internal.AlphaImage;
import com.kotcrab.vis.ui.widget.color.internal.Palette;
import com.kotcrab.vis.ui.widget.color.internal.PickerCommons;
import com.kotcrab.vis.ui.widget.color.internal.VerticalChannelBar;

import static com.kotcrab.vis.ui.widget.color.internal.ColorPickerText.HEX;

/**
 * Color Picker widget, allows user to select color. ColorPicker is relatively heavy widget and should be reused if possible.
 * Unlike other widgets, this one must be disposed when no longer needed!
 * <p>
 * Displays color pallet along with hue spectrum bar. Palette show all possible combination of saturation and value (SV
 * components of HSV color system) for given hue, spectrum bar shows all possible values of hue (H component). Displays
 * preview of current and new color and hex field that can be disabled. See {@link ExtendedColorPicker} if you need
 * more features.
 * <p>
 * Alpha channel can be only set from hex field and it disabled by default, use {@link #setAllowAlphaEdit(boolean)} to enable.
 * @author Kotcrab
 * @see ColorPicker
 * @see BasicColorPicker
 * @see ExtendedColorPicker
 * @since 0.9.3
 */
public class BasicColorPicker extends VisTable implements Disposable {
	public static final int FIELD_WIDTH = 50;

	public static final int PALETTE_SIZE = 160;
	public static final int BAR_WIDTH = 130;
	public static final int BAR_HEIGHT = 12;

	private static final float VERTICAL_BAR_WIDTH = 15;

	private static final int HEX_FIELD_WIDTH = 95;
	private static final int HEX_COLOR_LENGTH = 6;
	private static final int HEX_COLOR_LENGTH_WITH_ALPHA = 8;

	protected ColorPickerWidgetStyle style;
	protected Sizes sizes;

	protected ColorPickerListener listener;

	Color oldColor;
	Color color;

	protected PickerCommons commons;

	protected Palette palette;
	protected VerticalChannelBar verticalBar;

	private VisTable mainTable;
	private VisTable colorPreviewsTable;
	private VisTable hexTable;
	private VisValidatableTextField hexField;

	private Image currentColorImg;
	private Image newColorImg;

	private boolean allowAlphaEdit = false;
	private boolean showHexFields = true;

	private boolean disposed = false;

	public BasicColorPicker () {
		this(null);
	}

	public BasicColorPicker (ColorPickerListener listener) {
		this("default", listener);
	}

	public BasicColorPicker (String styleName, ColorPickerListener listener) {
		this(VisUI.getSkin().get(styleName, ColorPickerWidgetStyle.class), listener, false);
	}

	public BasicColorPicker (ColorPickerWidgetStyle style, ColorPickerListener listener) {
		this(style, listener, false);
	}

	protected BasicColorPicker (ColorPickerWidgetStyle style, ColorPickerListener listener, boolean loadExtendedShaders) {
		this.listener = listener;
		this.style = style;
		this.sizes = VisUI.getSizes();

		oldColor = new Color(Color.BLACK);
		color = new Color(Color.BLACK);

		commons = new PickerCommons(style, sizes, loadExtendedShaders);

		createColorWidgets();
		createUI();

		updateValuesFromCurrentColor();
		updateUI();
	}

	protected void createUI () {
		mainTable = new VisTable(true);

		colorPreviewsTable = createColorsPreviewTable();
		hexTable = createHexTable();

		rebuildMainTable();

		add(mainTable).top();
	}

	private void rebuildMainTable () {
		mainTable.clearChildren();
		mainTable.add(palette).size(PALETTE_SIZE * sizes.scaleFactor);
		mainTable.add(verticalBar).size(VERTICAL_BAR_WIDTH * sizes.scaleFactor, PALETTE_SIZE * sizes.scaleFactor).top();
		mainTable.row();
		mainTable.add(colorPreviewsTable).colspan(2).expandX().fillX();

		if (showHexFields) {
			mainTable.row();
			mainTable.add(hexTable).colspan(2).expandX().left();
		}
	}

	private VisTable createColorsPreviewTable () {
		VisTable table = new VisTable(false);
		table.add(currentColorImg = new AlphaImage(commons, 5 * sizes.scaleFactor))
				.height(25 * sizes.scaleFactor).width(80 * sizes.scaleFactor).expandX().fillX();
		table.add(new Image(style.iconArrowRight)).pad(0, 2, 0, 2);
		table.add(newColorImg = new AlphaImage(commons, 5 * sizes.scaleFactor))
				.height(25 * sizes.scaleFactor).width(80 * sizes.scaleFactor).expandX().fillX();

		currentColorImg.setColor(color);
		newColorImg.setColor(color);

		currentColorImg.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				restoreLastColor();
			}
		});

		return table;
	}

	private VisTable createHexTable () {
		VisTable table = new VisTable(true);
		table.add(new VisLabel(HEX.get()));
		table.add(hexField = new VisValidatableTextField("00000000")).width(HEX_FIELD_WIDTH * sizes.scaleFactor);
		table.row();

		hexField.setMaxLength(HEX_COLOR_LENGTH);
		hexField.setProgrammaticChangeEvents(false);
		hexField.setTextFieldFilter(new TextFieldFilter() {
			@Override
			public boolean acceptChar (VisTextField textField, char c) {
				return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
			}
		});

		hexField.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (hexField.getText().length() == (allowAlphaEdit ? HEX_COLOR_LENGTH_WITH_ALPHA : HEX_COLOR_LENGTH)) {
					setColor(Color.valueOf(hexField.getText()), false);
				}
			}
		});

		return table;
	}

	protected void createColorWidgets () {
		PickerChangeListener pickerListener = new PickerChangeListener();
		palette = new Palette(commons, 100, pickerListener);
		verticalBar = new VerticalChannelBar(commons, 360, pickerListener);
	}

	protected void updateUI () {
		palette.setPickerHue(verticalBar.getValue());

		newColorImg.setColor(color);

		hexField.setText(color.toString().toUpperCase());
		hexField.setCursorPosition(hexField.getMaxLength());

		if (listener != null) listener.changed(color);
	}

	/** Updates picker ui from current color */
	protected void updateValuesFromCurrentColor () {
		int[] hsv = ColorUtils.RGBtoHSV(color);
		int ch = hsv[0];
		int cs = hsv[1];
		int cv = hsv[2];

		verticalBar.setValue(ch);
		palette.setValue(cs, cv);
	}

	protected void updateValuesFromHSVFields () {
		color = ColorUtils.HSVtoRGB(verticalBar.getValue(), palette.getS(), palette.getV(), color.a);
	}

	public void restoreLastColor () {
		Color colorBeforeReset = new Color(color);
		setColor(oldColor);
		if (listener != null) listener.reset(colorBeforeReset, color);
	}

	/** Sets current selected color in picker. */
	@Override
	public void setColor (Color newColor) {
		if (allowAlphaEdit == false) newColor.a = 1;
		//this method overrides setColor in Actor, not big deal we definitely don't need it
		setColor(newColor, true);
	}

	protected void setColor (Color newColor, boolean updateCurrentColor) {
		if (updateCurrentColor) {
			currentColorImg.setColor(new Color(newColor));
			oldColor = new Color(newColor);
		}
		color = new Color(newColor);
		updateValuesFromCurrentColor();
		updateUI();
	}

	public ColorPickerListener getListener () {
		return listener;
	}

	public void setListener (ColorPickerListener listener) {
		this.listener = listener;
	}

	/**
	 * @param allowAlphaEdit if false this picker will have disabled editing color alpha channel. If current picker color
	 * has alpha it will be reset to 1. If true alpha editing will be re-enabled. For better UX this should not be called
	 * while ColorPicker is visible.
	 */
	public void setAllowAlphaEdit (boolean allowAlphaEdit) {
		this.allowAlphaEdit = allowAlphaEdit;

		hexField.setMaxLength(allowAlphaEdit ? HEX_COLOR_LENGTH_WITH_ALPHA : HEX_COLOR_LENGTH);
		if (allowAlphaEdit == false) {
			setColor(new Color(color));
		}
	}

	public boolean isAllowAlphaEdit () {
		return allowAlphaEdit;
	}

	public void setShowHexFields (boolean showHexFields) {
		this.showHexFields = showHexFields;
		hexTable.setVisible(showHexFields);
		rebuildMainTable();
	}

	public boolean isShowHexFields () {
		return showHexFields;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		boolean wasPedantic = ShaderProgram.pedantic;
		ShaderProgram.pedantic = false;
		super.draw(batch, parentAlpha);
		ShaderProgram.pedantic = wasPedantic;
	}

	public boolean isDisposed () {
		return disposed;
	}

	@Override
	public void dispose () {
		if (disposed) throw new IllegalStateException("ColorPicker can't be disposed twice!");
		commons.dispose();
		disposed = true;
	}

	/** Internal default picker listener used to get events from color widgets */
	class PickerChangeListener extends ChangeListener {
		protected void updateLinkedWidget () {

		}

		@Override
		public void changed (ChangeEvent event, Actor actor) {
			updateLinkedWidget();

			updateValuesFromHSVFields();
			updateUI();
		}
	}
}
