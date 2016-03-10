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

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ColorUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.color.internal.ChannelBar;
import com.kotcrab.vis.ui.widget.color.internal.ColorChannelWidget;
import com.kotcrab.vis.ui.widget.color.internal.Palette;
import com.kotcrab.vis.ui.widget.color.internal.VerticalChannelBar;

/**
 * Color Picker widget, allows user to select color. ColorPicker is relatively heavy widget and should be reused if possible.
 * Unlike other widgets, this one must be disposed when no longer needed!
 * <p>
 * Extends {@link BasicColorPicker} functionality and additionally provides separate bars for H, S, V color components.
 * Additional 3 bars (R, G, B) for selecting colors using RGB systems and dedicated bar to alpha channel. Alpha edition
 * is enabled by default.
 * <p>
 * Used directly by {@link ColorPicker} dialog.
 * @author Kotcrab
 * @see BasicColorPicker
 * @see ColorPicker
 * @since 0.9.3
 */
public class ExtendedColorPicker extends BasicColorPicker implements Disposable {
	private ColorChannelWidget hBar;
	private ColorChannelWidget sBar;
	private ColorChannelWidget vBar;

	private ColorChannelWidget rBar;
	private ColorChannelWidget gBar;
	private ColorChannelWidget bBar;

	private ColorChannelWidget aBar;

	public ExtendedColorPicker () {
		this(null);
	}

	public ExtendedColorPicker (ColorPickerListener listener) {
		this("default", listener);
	}

	public ExtendedColorPicker (String styleName, ColorPickerListener listener) {
		this(VisUI.getSkin().get(styleName, ColorPickerWidgetStyle.class), listener);
	}

	public ExtendedColorPicker (ColorPickerWidgetStyle style, ColorPickerListener listener) {
		super(style, listener, true);
		setAllowAlphaEdit(true);
	}

	@Override
	protected void createUI () {
		super.createUI();

		VisTable extendedTable = new VisTable(true); //displayed next to mainTable

		extendedTable.add(hBar).row();
		extendedTable.add(sBar).row();
		extendedTable.add(vBar).row();

		extendedTable.add();
		extendedTable.row();

		extendedTable.add(rBar).row();
		extendedTable.add(gBar).row();
		extendedTable.add(bBar).row();

		extendedTable.add();
		extendedTable.row();

		extendedTable.add(aBar).row();

		add(extendedTable).expand().left().top().pad(0, 9, 4, 4);
	}

	@Override
	protected void createColorWidgets () {
		palette = new Palette(commons, 100, new PickerChangeListener() {
			@Override
			public void updateLinkedWidget () {
				sBar.setValue(palette.getS());
				vBar.setValue(palette.getV());
			}
		});

		verticalBar = new VerticalChannelBar(commons, 360, new PickerChangeListener() {
			@Override
			public void updateLinkedWidget () {
				hBar.setValue(verticalBar.getValue());
			}
		});

		HsvChannelBarListener svListener = new HsvChannelBarListener() {
			@Override
			protected void updateLinkedWidget () {
				palette.setValue(sBar.getValue(), vBar.getValue());
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

	@Override
	public void setAllowAlphaEdit (boolean allowAlphaEdit) {
		aBar.setVisible(allowAlphaEdit);
		super.setAllowAlphaEdit(allowAlphaEdit);
	}

	@Override
	protected void updateValuesFromCurrentColor () {
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
		palette.setValue(sBar.getValue(), vBar.getValue());
	}

	/** Updates picker from H, S and V bars */
	@Override
	protected void updateValuesFromHSVFields () {
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
		palette.setValue(sBar.getValue(), vBar.getValue());
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
