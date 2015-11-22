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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ColorUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;
import com.kotcrab.vis.ui.widget.color.internal.*;

import static com.kotcrab.vis.ui.widget.color.ColorPickerText.*;

/**
 * Color Picker widget, allows user to select color. ColorPicker is relatively heavy widget and should be reused if possible.
 * Unlike other widgets, this one must be disposed when no longer needed!
 * @author Kotcrab
 * @since 0.9.3
 * @see ColorPicker
 */
public class ExtendedColorPicker extends VisTable implements Disposable {
	public static final int FIELD_WIDTH = 50;

	public static final int PALETTE_SIZE = 160;
	public static final int BAR_WIDTH = 130;
	public static final int BAR_HEIGHT = 12;

	private static final float VERTICAL_BAR_WIDTH = 15;

	private static final int HEX_FIELD_WIDTH = 95;
	private static final int HEX_COLOR_LENGTH = 6;
	private static final int HEX_COLOR_LENGTH_WITH_ALPHA = 8;

	private ColorPickerStyle style;
	private Sizes sizes;

	private ColorPickerListener listener;

	Color oldColor;
	Color color;

	private PickerCommons commons;

	private Palette palette;
	private VerticalChannelBar verticalBar;

	private ColorChannelWidget hBar;
	private ColorChannelWidget sBar;
	private ColorChannelWidget vBar;

	private ColorChannelWidget rBar;
	private ColorChannelWidget gBar;
	private ColorChannelWidget bBar;

	private ColorChannelWidget aBar;

	private VisValidatableTextField hexField;

	private Image currentColorImg;
	private Image newColorImg;

	private boolean allowAlphaEdit = true;
	private boolean disposed = false;

	public ExtendedColorPicker () {
		this(null);
	}

	public ExtendedColorPicker (ColorPickerListener listener) {
		this("default", listener);
	}

	public ExtendedColorPicker (String styleName, ColorPickerListener listener) {
		this.listener = listener;
		this.style = VisUI.getSkin().get(styleName, ColorPickerStyle.class);
		this.sizes = VisUI.getSizes();

		oldColor = new Color(Color.BLACK);
		color = new Color(Color.BLACK);

		commons = new PickerCommons(style, sizes);

		createColorWidgets();
		createUI();
		updateUI();
	}

	private void createUI () {
		VisTable rightTable = new VisTable(true);

		rightTable.add(hBar).row();
		rightTable.add(sBar).row();
		rightTable.add(vBar).row();

		rightTable.add();
		rightTable.row();

		rightTable.add(rBar).row();
		rightTable.add(gBar).row();
		rightTable.add(bBar).row();

		rightTable.add();
		rightTable.row();

		rightTable.add(aBar).row();

		VisTable leftTable = new VisTable(true);
		leftTable.add(palette).size(PALETTE_SIZE * sizes.scaleFactor);
		leftTable.add(verticalBar).size(VERTICAL_BAR_WIDTH * sizes.scaleFactor, PALETTE_SIZE * sizes.scaleFactor).top();
		leftTable.row();
		leftTable.add(createColorsPreviewTable()).colspan(2).expandX().fillX();
		leftTable.row();
		leftTable.add(createHexTable()).colspan(2).expandX().left();

		add(leftTable).top().padRight(5);
		add(rightTable).expand().left().top().pad(4);
	}

	private VisTable createColorsPreviewTable () {
		VisTable table = new VisTable(false);
		table.add(currentColorImg = new AlphaImage(commons, 5 * sizes.scaleFactor)).height(25 * sizes.scaleFactor).width(80 * sizes.scaleFactor).expandX().fillX();
		table.add(new Image(VisUI.getSkin().getDrawable("icon-arrow-right")));
		table.add(newColorImg = new AlphaImage(commons, 5 * sizes.scaleFactor)).height(25 * sizes.scaleFactor).width(80 * sizes.scaleFactor).expandX().fillX();

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

		hexField.setMaxLength(8);
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
				if (hexField.getText().length() == (allowAlphaEdit ? HEX_COLOR_LENGTH_WITH_ALPHA : HEX_COLOR_LENGTH))
					setColor(Color.valueOf(hexField.getText()), false);
			}
		});

		return table;
	}

	private void createColorWidgets () {
		palette = new Palette(commons, 100, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				sBar.setValue(palette.getV());
				vBar.setValue(palette.getS());

				updateValuesFromHSVFields();
				updateUI();
			}
		});

		verticalBar = new VerticalChannelBar(commons, 360, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				hBar.setValue(verticalBar.getValue());
				updateValuesFromHSVFields();
				updateUI();
			}
		});

		HsvChannelBarListener svListener = new HsvChannelBarListener() {
			@Override
			protected void updateLinkedWidget () {
				palette.setValue(vBar.getValue(), sBar.getValue());
			}
		};

		hBar = new ColorChannelWidget(commons, "H", ChannelBar.MODE_H, 360, new HsvChannelBarListener() {
			@Override
			protected void updateLinkedWidget () {
				verticalBar.setValue(hBar.getValue());
			}
		});

		sBar = new ColorChannelWidget(commons, "S", ChannelBar.MODE_S, 100, svListener);
		vBar = new ColorChannelWidget(commons, "V", ChannelBar.MODE_V, 100, svListener);

		RgbChannelBarListener rgbListener = new RgbChannelBarListener();
		rBar = new ColorChannelWidget(commons, "R", ChannelBar.MODE_R, 255, rgbListener);
		gBar = new ColorChannelWidget(commons, "G", ChannelBar.MODE_G, 255, rgbListener);
		bBar = new ColorChannelWidget(commons, "B", ChannelBar.MODE_B, 255, rgbListener);

		aBar = new ColorChannelWidget(commons, "A", ChannelBar.MODE_ALPHA, 255, new AlphaChannelBarListener());
	}

	public ColorPickerListener getListener () {
		return listener;
	}

	public void setListener (ColorPickerListener listener) {
		this.listener = listener;
	}

	private void updateUI () {
		palette.setPickerHue(hBar.getValue());

		newColorImg.setColor(color);

		hexField.setText(color.toString().toUpperCase());
		hexField.setCursorPosition(hexField.getMaxLength());

		if (listener != null) listener.changed(color);
	}

	@Override
	/** Sets current selected color in picker. If alpha editing is disabled then alpha channel of this new color will be set to 1 */
	public void setColor (Color newColor) {
		if (allowAlphaEdit == false) newColor.a = 1;
		//this method overrides setColor in Actor, not big deal we definitely don't need it
		setColor(newColor, true);
	}

	private void setColor (Color newColor, boolean updateCurrentColor) {
		if (updateCurrentColor) {
			currentColorImg.setColor(new Color(newColor));
			oldColor = new Color(newColor);
		}
		color = new Color(newColor);
		updateValuesFromCurrentColor();
		updateUI();
	}

	/**
	 * @param allowAlphaEdit if false this picker will have disabled editing color alpha channel. If current picker color
	 * has alpha it will be reset to 1. If true alpha editing will be re-enabled. For better UX this should not be called
	 * while ColorPicker is visible.
	 */
	public void setAllowAlphaEdit (boolean allowAlphaEdit) {
		this.allowAlphaEdit = allowAlphaEdit;

		aBar.setVisible(allowAlphaEdit);
		hexField.setMaxLength(allowAlphaEdit ? HEX_COLOR_LENGTH_WITH_ALPHA : HEX_COLOR_LENGTH);
		if (allowAlphaEdit == false) {
			Color newColor = new Color(color);
			newColor.a = 1;
			setColor(newColor);
		}
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		boolean wasPedantic = ShaderProgram.pedantic;
		ShaderProgram.pedantic = false;
		super.draw(batch, parentAlpha);
		ShaderProgram.pedantic = wasPedantic;
	}

	public boolean isAllowAlphaEdit () {
		return allowAlphaEdit;
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

	/** Updates picker ui from current color */
	private void updateValuesFromCurrentColor () {
		int[] hsv = ColorUtils.RGBtoHSV(color);
		int ch = hsv[0];
		int cs = hsv[1];
		int cv = hsv[2];

		int cr = MathUtils.round(color.r * 255.0f);
		int cg = MathUtils.round(color.g * 255.0f);
		int cb = MathUtils.round(color.b * 255.0f);
		int ca = MathUtils.round(color.a * 255.0f);

		hBar.setValue(ch);
		sBar.setValue(cs);
		vBar.setValue(cv);

		rBar.setValue(cr);
		gBar.setValue(cg);
		bBar.setValue(cb);

		aBar.setValue(ca);

		verticalBar.setValue(hBar.getValue());
		palette.setValue(vBar.getValue(), sBar.getValue());
	}

	/** Updates picker from H, S and V bars */
	private void updateValuesFromHSVFields () {
		int[] hsv = ColorUtils.RGBtoHSV(color);
		int h = hsv[0];
		int s = hsv[1];
		int v = hsv[2];

		if (hBar.isInputValid()) h = hBar.getValue();
		if (sBar.isInputValid()) s = sBar.getValue();
		if (vBar.isInputValid()) v = vBar.getValue();

		color = ColorUtils.HSVtoRGB(h, s, v, color.a);

		int cr = MathUtils.round(color.r * 255.0f);
		int cg = MathUtils.round(color.g * 255.0f);
		int cb = MathUtils.round(color.b * 255.0f);

		rBar.setValue(cr);
		gBar.setValue(cg);
		bBar.setValue(cb);
	}

	/** Updates picker from R, G and B bars */
	private void updateValuesFromRGBFields () {
		int r = MathUtils.round(color.r * 255.0f);
		int g = MathUtils.round(color.g * 255.0f);
		int b = MathUtils.round(color.b * 255.0f);

		if (rBar.isInputValid()) r = rBar.getValue();
		if (gBar.isInputValid()) g = gBar.getValue();
		if (bBar.isInputValid()) b = bBar.getValue();

		color.set(r / 255.0f, g / 255.0f, b / 255.0f, color.a);

		int[] hsv = ColorUtils.RGBtoHSV(color);
		int ch = hsv[0];
		int cs = hsv[1];
		int cv = hsv[2];

		hBar.setValue(ch);
		sBar.setValue(cs);
		vBar.setValue(cv);

		verticalBar.setValue(hBar.getValue());
		palette.setValue(vBar.getValue(), sBar.getValue());
	}

	public void restoreLastColor () {
		Color colorBeforeReset = new Color(color);
		setColor(oldColor);
		if(listener != null) listener.reset(colorBeforeReset, color);
	}

	private class RgbChannelBarListener implements ChannelBar.ChannelBarListener {
		@Override
		public void updateFields () {
			updateValuesFromRGBFields();
			updateUI();
		}

		@Override
		public void setShaderUniforms (ShaderProgram shader) {
			shader.setUniformf("u_r", color.r);
			shader.setUniformf("u_g", color.g);
			shader.setUniformf("u_b", color.b);
		}
	}

	private class AlphaChannelBarListener extends RgbChannelBarListener {
		@Override
		public void updateFields () {
			if (aBar.isInputValid()) color.a = aBar.getValue() / 255.0f;
			updateUI();
		}
	}

	private abstract class HsvChannelBarListener implements ChannelBar.ChannelBarListener {
		@Override
		public void updateFields () {
			updateLinkedWidget();
			updateValuesFromHSVFields();
			updateUI();
		}

		@Override
		public void setShaderUniforms (ShaderProgram shader) {
			shader.setUniformf("u_h", hBar.getValue() / 360.0f);
			shader.setUniformf("u_s", sBar.getValue() / 100.0f);
			shader.setUniformf("u_v", vBar.getValue() / 100.0f);
		}

		protected abstract void updateLinkedWidget ();
	}
}
