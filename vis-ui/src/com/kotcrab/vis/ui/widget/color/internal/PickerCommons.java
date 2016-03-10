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

package com.kotcrab.vis.ui.widget.color.internal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle;

/** @author Kotcrab */
public class PickerCommons implements Disposable {
	final ColorPickerWidgetStyle style;
	final Sizes sizes;

	private boolean loadExtendedShaders;
	ShaderProgram paletteShader;
	ShaderProgram verticalChannelShader;
	ShaderProgram hsvShader;
	ShaderProgram rgbShader;
	ShaderProgram gridShader;

	Texture whiteTexture;

	public PickerCommons (ColorPickerWidgetStyle style, Sizes sizes, boolean loadExtendedShaders) {
		this.style = style;
		this.sizes = sizes;
		this.loadExtendedShaders = loadExtendedShaders;

		createPixmap();
		loadShaders();
	}

	private void createPixmap () {
		Pixmap whitePixmap = new Pixmap(2, 2, Format.RGB888);
		whitePixmap.setColor(Color.WHITE);
		whitePixmap.drawRectangle(0, 0, 2, 2);
		whiteTexture = new Texture(whitePixmap);
		whiteTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		whitePixmap.dispose();
	}

	private void loadShaders () {
		paletteShader = loadShader("default.vert", "palette.frag");
		verticalChannelShader = loadShader("default.vert", "verticalBar.frag");
		gridShader = loadShader("default.vert", "checkerboard.frag");

		if (loadExtendedShaders) {
			hsvShader = loadShader("default.vert", "hsv.frag");
			rgbShader = loadShader("default.vert", "rgb.frag");
		}
	}

	private ShaderProgram loadShader (String vertFile, String fragFile) {
		ShaderProgram program = new ShaderProgram(
				Gdx.files.classpath("com/kotcrab/vis/ui/widget/color/internal/" + vertFile),
				Gdx.files.classpath("com/kotcrab/vis/ui/widget/color/internal/" + fragFile));

		if (program.isCompiled() == false) {
			throw new IllegalStateException("ColorPicker shader compilation failed. Shader: " + vertFile + ", " + fragFile + ": " + program.getLog());
		}

		return program;
	}

	ShaderProgram getBarShader (int mode) {
		switch (mode) {
			case ChannelBar.MODE_ALPHA:
			case ChannelBar.MODE_R:
			case ChannelBar.MODE_G:
			case ChannelBar.MODE_B:
				return rgbShader;
			case ChannelBar.MODE_H:
			case ChannelBar.MODE_S:
			case ChannelBar.MODE_V:
				return hsvShader;
			default:
				throw new IllegalStateException("Unsupported mode: " + mode);
		}
	}

	@Override
	public void dispose () {
		whiteTexture.dispose();

		paletteShader.dispose();
		verticalChannelShader.dispose();
		gridShader.dispose();

		if (loadExtendedShaders) {
			hsvShader.dispose();
			rgbShader.dispose();
		}
	}
}
