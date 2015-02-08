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
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.util.ColorUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class ColorPicker extends VisWindow implements Disposable {

	private Color color;

	private Pixmap barPixmap;
	private Texture barTexture;
	private Cell<Image> barCell;

	private Texture paletteTexture;
	private Pixmap palettePixmap;
	private Cell<Image> paletteCell;

	private Texture hTexture;
	private Pixmap hPixmap;
	private Cell<Image> hCell;

	private Texture sTexture;
	private Pixmap sPixmap;
	private Cell<Image> sCell;

	private Texture vTexture;
	private Pixmap vPixmap;
	private Cell<Image> vCell;

	private Texture rTexture;
	private Pixmap rPixmap;
	private Cell<Image> rCell;

	private Texture gTexture;
	private Pixmap gPixmap;
	private Cell<Image> gCell;

	private Texture bTexture;
	private Pixmap bPixmap;
	private Cell<Image> bCell;

	private VisValidableTextField hField;
	private VisValidableTextField sField;
	private VisValidableTextField vField;

	private VisValidableTextField rField;
	private VisValidableTextField gField;
	private VisValidableTextField bField;

	private static final int FIELD_WIDTH = 50;

	private static final int PALETTE_SIZE = 160;
	private static final int BAR_WIDTH = 130;
	private static final int BAR_HEIGHT = 11;

	public ColorPicker () {
		super("Color Picker", true);
		init();

		color = ColorUtils.HSVtoRGB(239, 40, 60);

		hPixmap = new Pixmap(360, 1, Format.RGBA8888);
		sPixmap = new Pixmap(100, 1, Format.RGBA8888);
		vPixmap = new Pixmap(100, 1, Format.RGBA8888);

		rPixmap = new Pixmap(255, 1, Format.RGBA8888);
		gPixmap = new Pixmap(255, 1, Format.RGBA8888);
		bPixmap = new Pixmap(255, 1, Format.RGBA8888);

		hTexture = new Texture(hPixmap);
		sTexture = new Texture(sPixmap);
		vTexture = new Texture(vPixmap);

		rTexture = new Texture(rPixmap);
		gTexture = new Texture(gPixmap);
		bTexture = new Texture(bPixmap);

		VisTable rightTable = new VisTable(true);
		rightTable.defaults().left();

		rightTable.add(new VisLabel("H"));
		rightTable.add(hField = new VisValidableTextField()).width(FIELD_WIDTH);
		hCell = rightTable.add(new Image(hTexture)).size(BAR_WIDTH, BAR_HEIGHT);
		rightTable.row();

		rightTable.add(new VisLabel("S"));
		rightTable.add(sField = new VisValidableTextField()).width(FIELD_WIDTH);
		sCell = rightTable.add(new Image(sTexture)).size(BAR_WIDTH, BAR_HEIGHT);
		rightTable.row();

		rightTable.add(new VisLabel("V"));
		rightTable.add(vField = new VisValidableTextField()).width(FIELD_WIDTH);
		vCell = rightTable.add(new Image(vTexture)).size(BAR_WIDTH, BAR_HEIGHT);
		rightTable.row();

		rightTable.add();
		rightTable.row();

		rightTable.add(new VisLabel("R"));
		rightTable.add(rField = new VisValidableTextField()).width(FIELD_WIDTH);
		rCell = rightTable.add(new Image(rTexture)).size(BAR_WIDTH, BAR_HEIGHT);
		rightTable.row();

		rightTable.add(new VisLabel("G"));
		rightTable.add(gField = new VisValidableTextField()).width(FIELD_WIDTH);
		gCell = rightTable.add(new Image(gTexture)).size(BAR_WIDTH, BAR_HEIGHT);
		rightTable.row();

		rightTable.add(new VisLabel("B"));
		rightTable.add(bField = new VisValidableTextField()).width(FIELD_WIDTH);
		bCell = rightTable.add(new Image(bTexture)).size(BAR_WIDTH, BAR_HEIGHT);
		rightTable.row();

		palettePixmap = new Pixmap(100, 100, Format.RGBA8888);
		barPixmap = new Pixmap(1, 360, Format.RGBA8888);

		paletteTexture = new Texture(palettePixmap);
		barTexture = new Texture(barPixmap);

		paletteCell = add(new Image(paletteTexture)).size(PALETTE_SIZE).top().padRight(5);
		barCell = add(new Image(barTexture)).size(15, PALETTE_SIZE).top();
		add(rightTable).expand().left().top().pad(4);

		updatePixmaps();
	}

	private void updatePixmaps () {

		int[] hsv = ColorUtils.RGBtoHSV(color);
		int ch = hsv[0];
		int cs = hsv[1];
		int cv = hsv[2];

		int cr = MathUtils.round(color.r * 255.0f);
		int cg = MathUtils.round(color.g * 255.0f);
		int cb = MathUtils.round(color.b * 255.0f);

		for (int v = 0; v < 100; v++) {
			for (int s = 100; s > 0; s--) {
				Color color = ColorUtils.HSVtoRGB(ch, s, v);
				palettePixmap.drawPixel(v, 100 - s, ColorUtils.toIntRGBA(color));
			}
		}

		for (int h = 0; h < 360; h++) {
			Color color = ColorUtils.HSVtoRGB(h, 100, 100);
			barPixmap.drawPixel(0, h, ColorUtils.toIntRGBA(color));
		}

		for (int h = 0; h < 360; h++) {
			Color color = ColorUtils.HSVtoRGB(h, cs, cv);
			hPixmap.drawPixel(h, 0, ColorUtils.toIntRGBA(color));
		}

		for (int s = 0; s < 100; s++) {
			Color color = ColorUtils.HSVtoRGB(ch, s, cv);
			sPixmap.drawPixel(s, 0, ColorUtils.toIntRGBA(color));
		}

		for (int v = 0; v < 100; v++) {
			Color color = ColorUtils.HSVtoRGB(ch, cs, v);
			vPixmap.drawPixel(v, 0, ColorUtils.toIntRGBA(color));
		}

		for (int r = 0; r < 255; r++) {
			Color color = new Color(r / 255.0f, cg / 255.0f, cb / 255.0f, 1);
			rPixmap.drawPixel(r, 0, ColorUtils.toIntRGBA(color));
		}

		for (int g = 0; g < 255; g++) {
			Color color = new Color(cr / 255.0f, g / 255.0f, cb / 255.0f, 1);
			gPixmap.drawPixel(g, 0, ColorUtils.toIntRGBA(color));
		}

		for (int b = 0; b < 255; b++) {
			Color color = new Color(cr / 255.0f, cg / 255.0f, b / 255.0f, 1);
			bPixmap.drawPixel(b, 0, ColorUtils.toIntRGBA(color));
		}

		paletteTexture = updateImage(palettePixmap, paletteTexture, paletteCell);
		barTexture = updateImage(barPixmap, barTexture, barCell);

		hTexture = updateImage(hPixmap, hTexture, hCell);
		sTexture = updateImage(sPixmap, sTexture, sCell);
		vTexture = updateImage(vPixmap, vTexture, vCell);

		rTexture = updateImage(rPixmap, rTexture, rCell);
		gTexture = updateImage(gPixmap, gTexture, gCell);
		bTexture = updateImage(bPixmap, bTexture, bCell);

		hField.setText(String.valueOf(ch));
		sField.setText(String.valueOf(cs));
		vField.setText(String.valueOf(cv));

		rField.setText(String.valueOf(cr));
		gField.setText(String.valueOf(cg));
		bField.setText(String.valueOf(cb));
	}

	public Texture updateImage (Pixmap pixmap, Texture texture, Cell<Image> cell) {
		texture.dispose();
		texture = new Texture(pixmap);
		cell.setActor(new Image(texture));
		return texture;
	}

	private void init () {
		setModal(true);
		setResizable(true);
		setMovable(true);
		addCloseButton();
		closeOnEscape();

		setSize(410, 500);
		centerWindow();
	}

	@Override
	public void dispose () {
		paletteTexture.dispose();
		barTexture.dispose();

		hTexture.dispose();
		sTexture.dispose();
		vTexture.dispose();
		rTexture.dispose();
		gTexture.dispose();
		bTexture.dispose();

		palettePixmap.dispose();
		barPixmap.dispose();

		hPixmap.dispose();
		sPixmap.dispose();
		vPixmap.dispose();
		rPixmap.dispose();
		gPixmap.dispose();
		bPixmap.dispose();
	}
}
