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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.kotcrab.vis.editor.module.project.BMPEditorFont;
import com.kotcrab.vis.editor.module.project.EditorFont;
import com.kotcrab.vis.runtime.entity.TextEntity;

/**
 * Text that you can scale, rotate, change color itp. Supports distance field fonts
 * @author Kotcrab
 */
public class TextObject extends TextEntity implements EditorObject {
	private transient EditorFont font;

	public TextObject (BMPEditorFont font, String text) {
		this(font, font.get(), text, BITMAP_FONT_SIZE);
	}

	public TextObject (EditorFont font, BitmapFont bitmapFont, String text, int fontSize) {
		super(null, bitmapFont, font.getRelativePath(), text, fontSize);
		this.font = font;
	}

	public TextObject (TextObject other) {
		super(other.getId(), other.cache.getFont(), other.getAssetPath(), other.getText(), other.getFontSize());
		this.font = other.font;

		setAutoSetOriginToCenter(other.isAutoSetOriginToCenter());
		setDistanceFieldShaderEnabled(other.isDistanceFieldShaderEnabled());
		setX(other.getX());
		setY(other.getY());
		setOrigin(other.getOriginX(), other.getOriginY());
		setScale(other.getScaleX(), other.getScaleY());
		setRotation(other.getRotation());
		setColor(other.getColor());
	}

	public void onDeserialize (EditorFont font) {
		setFont(font);
		setColor(getColor()); //update cache color
	}

	@Override
	public boolean isOriginSupported () {
		return true;
	}

	@Override
	public boolean isScaleSupported () {
		return true;
	}

	@Override
	public boolean isTintSupported () {
		return true;
	}

	@Override
	public boolean isRotationSupported () {
		return true;
	}

	public void setFontSize (int fontSize) {
		if (this.fontSize != fontSize) {
			this.fontSize = fontSize;
			BitmapFont bmpFont = font.get(fontSize);
			cache = new BitmapFontCache(bmpFont);
			setColor(getColor());
			textBounds = cache.setText(text, 0, 0);
			textChanged();
		}
	}

	public void setFont (EditorFont font) {
		if (this.font != font) {
			this.font = font;

			setAssetPath(font.getRelativePath());
			cache = new BitmapFontCache(font.get(fontSize));
			setColor(getColor());
			textBounds = cache.setText(text, 0, 0);
			textChanged();
		}
	}
}
