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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ColorUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;
import com.kotcrab.vis.ui.widget.color.internal.*;

import static com.kotcrab.vis.ui.widget.color.ColorPickerText.*;

/**
 * Color Picker widget, allows user to select color. ColorPicker is heavy widget and should be reused whenever possible.
 * This widget must be disposed when no longer needed! ColorPicker will be centered on screen after adding to Stage
 * use {@link #setCenterOnAdd(boolean)} to change this.
 * @author Kotcrab
 * @since 0.6.0
 */
public class ColorPicker extends VisWindow implements Disposable {
	public static final int FIELD_WIDTH = 50;

	public static final int PALETTE_SIZE = 160;
	public static final int BAR_WIDTH = 130;
	public static final int BAR_HEIGHT = 11;

	private static final float VERTICAL_BAR_WIDTH = 15;

	private static final int HEX_FIELD_WIDTH = 95;
	private static final int HEX_COLOR_LENGTH = 6;
	private static final int HEX_COLOR_LENGTH_WITH_ALPHA = 8;

	private ColorPickerStyle style;
	private Sizes sizes;

	private ColorPickerListener listener;

	private Color oldColor;
	private Color color;

	private Texture whiteTexture;
	private Pixmap whitePixmap;

	private ShaderProgram paletteShader;
	private ShaderProgram verticalChannelShader;
	private ShaderProgram hsvShader;
	private ShaderProgram rgbShader;

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

	private VisTextButton restoreButton;
	private VisTextButton cancelButton;
	private VisTextButton okButton;

	private Image currentColorImg;
	private Image newColorImg;

	private boolean allowAlphaEdit = true;
	private boolean closeAfterPickingFinished = true;
	private boolean disposed = false;

	public ColorPicker () {
		this((String) null);
	}

	public ColorPicker (String title) {
		this("default", title, null);
	}

	public ColorPicker (String title, ColorPickerListener listener) {
		this("default", title, listener);
	}

	public ColorPicker (ColorPickerListener listener) {
		this("default", null, listener);
	}

	public ColorPicker (String styleName, String title, ColorPickerListener listener) {
		super(title != null ? title : "", VisUI.getSkin().get(styleName, ColorPickerStyle.class));
		this.listener = listener;
		this.style = (ColorPickerStyle) getStyle();
		this.sizes = VisUI.getSizes();

		if (title == null) getTitleLabel().setText(TITLE.get());

		setModal(true);
		setMovable(true);

		addCloseButton();
		closeOnEscape();

		oldColor = new Color(Color.BLACK);
		color = new Color(Color.BLACK);

		createPixmap();
		loadShaders();

		createColorWidgets();
		createUI();
		createListeners();
		updateUI();

		pack();
		centerWindow();
	}

	private void createPixmap () {
		whitePixmap = new Pixmap(2, 2, Format.RGB888);
		whitePixmap.setColor(Color.WHITE);
		whitePixmap.drawRectangle(0, 0, 2, 2);
		whiteTexture = new Texture(whitePixmap);
		whiteTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}

	private void loadShaders () {
		paletteShader = loadShader("default.vert", "palette.frag");
		verticalChannelShader = loadShader("default.vert", "verticalBar.frag");
		hsvShader = loadShader("default.vert", "hsv.frag");
		rgbShader = loadShader("default.vert", "rgb.frag");
	}

	private ShaderProgram loadShader (String vertFile, String fragFile) {
		ShaderProgram program = new ShaderProgram(
				Gdx.files.classpath("com/kotcrab/vis/ui/widget/color/internal/" + vertFile),
				Gdx.files.classpath("com/kotcrab/vis/ui/widget/color/internal/" + fragFile));

		if (program.isCompiled() == false) {
			throw new IllegalStateException("ColorPicker shader compilation failed: " + program.getLog());
		}

		return program;
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
		leftTable.row();
		leftTable.add(createColorsPreviewTable()).expandX().fillX();
		leftTable.row();
		leftTable.add(createHexTable()).expandX().left();

		add(leftTable).top().padRight(5);
		add(verticalBar).size(VERTICAL_BAR_WIDTH * sizes.scaleFactor, PALETTE_SIZE * sizes.scaleFactor).top();
		add(rightTable).expand().left().top().pad(4);
		row();
		add(createButtons()).pad(3).right().expandX().colspan(3);
	}

	private VisTable createColorsPreviewTable () {
		VisTable table = new VisTable(false);
		table.add(new VisLabel(OLD.get())).spaceRight(3);
		table.add(currentColorImg = new AlphaImage(style)).height(25 * sizes.scaleFactor).expandX().fillX();
		table.row();
		table.add(new VisLabel(NEW.get())).spaceRight(3);
		table.add(newColorImg = new AlphaImage(style, true)).height(25 * sizes.scaleFactor).expandX().fillX();

		currentColorImg.setColor(color);
		newColorImg.setColor(color);

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

	private VisTable createButtons () {
		VisTable table = new VisTable(true);
		table.defaults().right();
		table.add(restoreButton = new VisTextButton(RESTORE.get()));
		table.add(okButton = new VisTextButton(OK.get()));
		table.add(cancelButton = new VisTextButton(CANCEL.get()));
		return table;
	}

	private void createColorWidgets () {
		palette = new Palette(style, sizes, paletteShader, whiteTexture, 100, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				sBar.setValue(palette.getV());
				vBar.setValue(palette.getS());

				updateValuesFromHSVFields();
				updateUI();
			}
		});

		verticalBar = new VerticalChannelBar(style, sizes, verticalChannelShader, whiteTexture, 360, new ChangeListener() {
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

		hBar = new ColorChannelWidget(style, sizes, "H", hsvShader, whiteTexture, ChannelBar.MODE_H, 360, new HsvChannelBarListener() {
			@Override
			protected void updateLinkedWidget () {
				verticalBar.setValue(hBar.getValue());
			}
		});

		sBar = new ColorChannelWidget(style, sizes, "S", hsvShader, whiteTexture, ChannelBar.MODE_S, 100, svListener);
		vBar = new ColorChannelWidget(style, sizes, "V", hsvShader, whiteTexture, ChannelBar.MODE_V, 100, svListener);

		RgbChannelBarListener rgbListener = new RgbChannelBarListener();
		rBar = new ColorChannelWidget(style, sizes, "R", rgbShader, whiteTexture, ChannelBar.MODE_R, 255, rgbListener);
		gBar = new ColorChannelWidget(style, sizes, "G", rgbShader, whiteTexture, ChannelBar.MODE_G, 255, rgbListener);
		bBar = new ColorChannelWidget(style, sizes, "B", rgbShader, whiteTexture, ChannelBar.MODE_B, 255, rgbListener);

		aBar = new ColorChannelWidget(style, sizes, "A", rgbShader, whiteTexture, ChannelBar.MODE_ALPHA, 255, new AlphaChannelBarListener());
	}

	private void createListeners () {
		restoreButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				setColor(oldColor);
			}
		});

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (listener != null) listener.finished(new Color(color));
				setColor(color);
				if (closeAfterPickingFinished) fadeOut();
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				setColor(oldColor);
				close();
			}
		});
	}

	@Override
	protected void close () {
		if (listener != null) listener.canceled(oldColor);
		super.close();
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
	 * Controls whether to fade out color picker after users finished color picking and has pressed OK button. If
	 * this is set to false picker won't close after pressing OK button. Default is true.
	 * Note that by default picker is a modal window so might also want to call {@code colorPicker.setModal(false)} to
	 * disable it.
	 */
	public void setCloseAfterPickingFinished (boolean closeAfterPickingFinished) {
		this.closeAfterPickingFinished = closeAfterPickingFinished;
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

	public boolean isAllowAlphaEdit () {
		return allowAlphaEdit;
	}

	public boolean isDisposed () {
		return disposed;
	}

	@Override
	public void dispose () {
		if (disposed) throw new IllegalStateException("ColorPicker can't be disposed twice!");

		whiteTexture.dispose();
		whitePixmap.dispose();

		paletteShader.dispose();
		verticalChannelShader.dispose();
		hsvShader.dispose();
		rgbShader.dispose();

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
