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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ColorUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorChannelWidget.ColorChannelWidgetListener;

/**
 * Color Picker widget, allows user to select color.
 * @since 0.6.0
 */
public class ColorPicker extends VisWindow implements Disposable {
	private static final Drawable white = VisUI.getSkin().getDrawable("white");

	static final int FIELD_WIDTH = 50;
	static final int HEX_FIELD_WIDTH = 95;

	static final int PALETTE_SIZE = 160;
	static final int BAR_WIDTH = 130;
	static final int BAR_HEIGHT = 11;
	static final float VERTICAL_BAR_WIDTH = 15;

	private ColorPickerListener listener;

	private Color oldColor;
	private Color color;

	private Pixmap barPixmap;
	private Texture barTexture;
	private VerticalChannelBar verticalBar;

	private Texture paletteTexture;
	private Pixmap palettePixmap;
	private Palette palette;

	private ColorChannelWidget hBar;
	private ColorChannelWidget sBar;
	private ColorChannelWidget vBar;

	private ColorChannelWidget rBar;
	private ColorChannelWidget gBar;
	private ColorChannelWidget bBar;

	private ColorChannelWidget aBar;

	private VisValidableTextField hexField;

	private VisTextButton restoreButton;
	private VisTextButton cancelButton;
	private VisTextButton okButton;

	private Image currentColor;
	private Image newColor;

	public ColorPicker () {
		this(null);
	}

	public ColorPicker (ColorPickerListener listener) {
		super("Color Picker");
		this.listener = listener;

		setModal(true);
		setMovable(true);

		addCloseButton();
		closeOnEscape();

		oldColor = new Color(Color.BLACK);
		color = new Color(Color.BLACK);

		createColorWidgets();
		createUI();
		createListeners();
		updatePixmaps();

		pack();
		centerWindow();
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
		leftTable.add(palette).size(PALETTE_SIZE);
		leftTable.row();
		leftTable.add(createColorsPreviewTable()).expandX().fillX();
		leftTable.row();
		leftTable.add(createHexTable()).expandX().left();

		add(leftTable).top().padRight(5);
		add(verticalBar).size(VERTICAL_BAR_WIDTH, PALETTE_SIZE).top();
		add(rightTable).expand().left().top().pad(4);
		row();
		add(createButtons()).pad(3).right().expandX().colspan(3);
	}

	private VisTable createColorsPreviewTable () {
		VisTable table = new VisTable(false);
		table.add(new VisLabel("Old")).spaceRight(3);
		table.add(currentColor = new AlphaImage(white)).height(25).expandX().fillX();
		table.row();
		table.add(new VisLabel("New")).spaceRight(3);
		table.add(newColor = new AlphaImage(white)).height(25).expandX().fillX();

		currentColor.setColor(color);
		newColor.setColor(color);

		return table;
	}

	private VisTable createHexTable () {
		VisTable table = new VisTable(true);
		table.add(new VisLabel("Hex"));
		table.add(hexField = new VisValidableTextField("00000000")).width(HEX_FIELD_WIDTH);
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
				if (hexField.getText().length() == 8) setColor(Color.valueOf(hexField.getText()));
			}
		});

		return table;
	}

	private VisTable createButtons () {
		VisTable table = new VisTable(true);
		table.defaults().right();
		table.add(restoreButton = new VisTextButton("Restore"));
		table.add(okButton = new VisTextButton("OK"));
		table.add(cancelButton = new VisTextButton("Cancel"));
		return table;
	}

	private void createColorWidgets () {
		palettePixmap = new Pixmap(100, 100, Format.RGBA8888);
		barPixmap = new Pixmap(1, 360, Format.RGBA8888);

		paletteTexture = new Texture(palettePixmap);
		barTexture = new Texture(barPixmap);

		palette = new Palette(paletteTexture, 0, 0, 100, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				//S ans V are flipped because the plate is flipped as well!
				sBar.setValue(palette.getV());
				vBar.setValue(palette.getS());

				updateHSVValuesFromFields();
				updatePixmaps();
			}
		});

		verticalBar = new VerticalChannelBar(barTexture, 0, 360, new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				hBar.setValue(verticalBar.getValue());
				updatePixmaps();
			}
		});

		hBar = new ColorChannelWidget("H", 360, new ColorChannelWidgetListener() {
			@Override
			public void updateFields () {
				verticalBar.setValue(hBar.getValue());
				updateHSVValuesFromFields();
				updatePixmaps();
			}

			@Override
			public void draw (Pixmap pixmap) {
				for (int h = 0; h < 360; h++) {
					Color color = ColorUtils.HSVtoRGB(h, sBar.getValue(), vBar.getValue());
					pixmap.drawPixel(h, 0, ColorUtils.toIntRGBA(color));
				}
			}
		});

		sBar = new ColorChannelWidget("S", 100, new ColorChannelWidgetListener() {
			@Override
			public void updateFields () {
				palette.setValue(vBar.getValue(), sBar.getValue());
				updateHSVValuesFromFields();
				updatePixmaps();
			}

			@Override
			public void draw (Pixmap pixmap) {
				for (int s = 0; s < 100; s++) {
					Color color = ColorUtils.HSVtoRGB(hBar.getValue(), s, vBar.getValue());
					pixmap.drawPixel(s, 0, ColorUtils.toIntRGBA(color));
				}
			}
		});

		vBar = new ColorChannelWidget("V", 100, new ColorChannelWidgetListener() {
			@Override
			public void updateFields () {
				palette.setValue(vBar.getValue(), sBar.getValue());
				updateHSVValuesFromFields();
				updatePixmaps();
			}

			@Override
			public void draw (Pixmap pixmap) {
				for (int v = 0; v < 100; v++) {
					Color color = ColorUtils.HSVtoRGB(hBar.getValue(), sBar.getValue(), v);
					pixmap.drawPixel(v, 0, ColorUtils.toIntRGBA(color));
				}

			}
		});

		rBar = new ColorChannelWidget("R", 255, new ColorChannelWidgetListener() {
			@Override
			public void updateFields () {
				updateRGBValuesFromFields();
				updatePixmaps();
			}

			@Override
			public void draw (Pixmap pixmap) {
				for (int r = 0; r < 255; r++) {
					Color pixelColor = new Color(r / 255.0f, color.g, color.b, 1);
					pixmap.drawPixel(r, 0, ColorUtils.toIntRGBA(pixelColor));
				}
			}
		});

		gBar = new ColorChannelWidget("G", 255, new ColorChannelWidgetListener() {
			@Override
			public void updateFields () {
				updateRGBValuesFromFields();
				updatePixmaps();
			}

			@Override
			public void draw (Pixmap pixmap) {
				for (int g = 0; g < 255; g++) {
					Color pixelColor = new Color(color.r, g / 255.0f, color.b, 1);
					pixmap.drawPixel(g, 0, ColorUtils.toIntRGBA(pixelColor));
				}
			}
		});

		bBar = new ColorChannelWidget("B", 255, new ColorChannelWidgetListener() {
			@Override
			public void updateFields () {
				updateRGBValuesFromFields();
				updatePixmaps();
			}

			@Override
			public void draw (Pixmap pixmap) {
				for (int b = 0; b < 255; b++) {
					Color pixelColor = new Color(color.r, color.g, b / 255.0f, 1);
					pixmap.drawPixel(b, 0, ColorUtils.toIntRGBA(pixelColor));
				}

			}
		});

		aBar = new ColorChannelWidget("A", 255, true, new ColorChannelWidgetListener() {
			@Override
			public void updateFields () {
				if (aBar.isInputValid()) color.a = aBar.getValue() / 255.0f;
				updatePixmaps();
			}

			@Override
			public void draw (Pixmap pixmap) {
				pixmap.setColor(0, 0, 0, 0);
				pixmap.fill();
				for (int i = 0; i < 255; i++) {
					Color pixelColor = new Color(color.r, color.g, color.b, i / 255.0f);
					pixmap.drawPixel(i, 0, ColorUtils.toIntRGBA(pixelColor));
				}
			}
		});
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
				fadeOut();
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
		if (listener != null) listener.canceled();
		super.close();
	}

	public ColorPickerListener getListener () {
		return listener;
	}

	public void setListener (ColorPickerListener listener) {
		this.listener = listener;
	}

	private void updatePixmaps () {
		for (int v = 0; v <= 100; v++) {
			for (int s = 0; s <= 100; s++) {
				Color color = ColorUtils.HSVtoRGB(hBar.getValue(), s, v);
				palettePixmap.drawPixel(v, 100 - s, ColorUtils.toIntRGBA(color));
			}
		}

		for (int h = 0; h < 360; h++) {
			Color color = ColorUtils.HSVtoRGB(360 - h, 100, 100);
			barPixmap.drawPixel(0, h, ColorUtils.toIntRGBA(color));
		}

		paletteTexture = updateImage(palettePixmap, paletteTexture, palette);
		barTexture = updateImage(barPixmap, barTexture, verticalBar);

		newColor.setColor(color);

		hBar.redraw();
		sBar.redraw();
		vBar.redraw();

		rBar.redraw();
		gBar.redraw();
		bBar.redraw();

		aBar.redraw();

		hexField.setText(color.toString().toUpperCase());
		hexField.setCursorPosition(hexField.getMaxLength());
	}


	@Override
	/** Sets current selected color in picker.*/
	public void setColor (Color c) {
		//this method overrides setColor in Actor, not big deal we definitely don't need it
		oldColor = new Color(c);
		color = new Color(c);
		updateFieldsFromColor();
		updatePixmaps();
	}

	private Texture updateImage (Pixmap pixmap, Texture texture, VisImage image) {
		texture.dispose();
		texture = new Texture(pixmap);
		image.setDrawable(texture);
		return texture;
	}

	@Override
	public void dispose () {
		paletteTexture.dispose();
		barTexture.dispose();

		palettePixmap.dispose();
		barPixmap.dispose();

		hBar.dispose();
		sBar.dispose();
		vBar.dispose();

		rBar.dispose();
		gBar.dispose();
		bBar.dispose();

		aBar.dispose();
	}

	private void updateFieldsFromColor () {
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

	private void updateHSVValuesFromFields () {
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

	private void updateRGBValuesFromFields () {
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
}
