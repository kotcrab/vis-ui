/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
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
public class TextObject extends TextEntity implements EditorEntity {
	private transient EditorFont font;

	public TextObject (BMPEditorFont font, String text) {
		this(font, font.get(), text, BITMAP_FONT_SIZE);
	}

	public TextObject (EditorFont font, BitmapFont bitmapFont, String text, int fontSize) {
		super(bitmapFont, null, font.getRelativePath(), text, fontSize);
		this.font = font;
	}

	/** Must be called after editor deserializaiton */
	public void afterDeserialize (EditorFont font) {
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

			relativeFontPath = font.getRelativePath();
			cache = new BitmapFontCache(font.get(fontSize));
			setColor(getColor());
			textBounds = cache.setText(text, 0, 0);
			textChanged();
		}
	}
}
