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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.editor.module.project.EditorFont;

/**
 * Text that you can scale, rotate, change color itp. Supports distance field fonts
 * @author Kotcrab
 */
public class TextObject extends EditorEntity {
	private String relativeFontPath;
	private int fontSize;
	private transient EditorFont font;
	private transient BitmapFontCache cache;

	private float x = 0, y = 0;
	private float originX = 0, originY = 0;
	private float scaleX = 1, scaleY = 1;
	private float rotation = 0;
	private TextBounds textBounds;
	private Rectangle boundingRectangle;
	private boolean autoSetOriginToCenter = true;
	private Matrix4 translationMatrix;
	private CharSequence text;

	public TextObject (EditorFont font, BitmapFont bitmapFont, String text, int fontSize) {
		this.font = font;
		this.text = text;
		this.relativeFontPath = font.getRelativePath();
		this.fontSize = fontSize;

		translationMatrix = new Matrix4();
		cache = new BitmapFontCache(bitmapFont);
		textBounds = cache.setText(text, 0, 0);
		if (autoSetOriginToCenter == true) setOriginCenter();
		translate();
	}

	/** Must be called after editor deserializaiton */
	public void afterDeserialize (EditorFont font) {
		setFont(font);
	}

	@Override
	public void render (Batch spriteBatch) {
		Matrix4 oldMatrix = spriteBatch.getTransformMatrix().cpy();
		spriteBatch.setTransformMatrix(translationMatrix);
		cache.draw(spriteBatch);
		spriteBatch.setTransformMatrix(oldMatrix);
	}

	private void translate () {
		translationMatrix.idt();
		translationMatrix.translate(x + originX, y + originY, 0);
		translationMatrix.rotate(0, 0, 1, rotation);
		translationMatrix.scale(scaleX, scaleY, 1);
		translationMatrix.translate(-originX, -originY, 0);
		translationMatrix.translate(0, textBounds.height, 0);
		calculateBoundingRectangle();
	}

	private void textChanged () {
		if (autoSetOriginToCenter == true) setOriginCenter();
		translate();
	}

	private void calculateBoundingRectangle () {
		Polygon polygon = new Polygon(new float[]{0, 0, textBounds.width, 0, textBounds.width, textBounds.height, 0,
				textBounds.height});
		polygon.setPosition(x, y);
		polygon.setRotation(rotation);
		polygon.setScale(scaleX, scaleY);
		polygon.setOrigin(originX, originY);
		boundingRectangle = polygon.getBoundingRectangle();
	}

	public String getText () {
		return text.toString();
	}

	public void setText (CharSequence str) {
		this.text = str;
		textBounds = cache.setText(str, 0, 0);
		textChanged();
	}

	public boolean isAutoSetOriginToCenter () {
		return autoSetOriginToCenter;
	}

	public void setOriginCenter () {
		setOrigin(textBounds.width / 2, -textBounds.height / 2);
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

	@Override
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		translate();
	}

	@Override
	public float getWidth () {
		return getBoundingRectangle().getWidth();
	}

	@Override
	public float getHeight () {
		return getBoundingRectangle().getHeight();
	}

	@Override
	public float getX () {
		return x;
	}

	@Override
	public void setX (float x) {
		this.x = x;
		translate();
	}

	@Override
	public float getY () {
		return y;
	}

	@Override
	public void setY (float y) {
		this.y = y;
		translate();
	}

	@Override
	public void setOrigin (float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
		translate();
	}

	@Override
	public float getOriginX () {
		return originX;
	}

	@Override
	public float getOriginY () {
		return originY;
	}

	@Override
	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		translate();
	}

	public void setScale (float scaleXY) {
		scaleX = scaleXY;
		scaleY = scaleXY;
		translate();
	}

	@Override
	public float getScaleX () {
		return scaleX;
	}

	@Override
	public float getScaleY () {
		return scaleY;
	}

	@Override
	public float getRotation () {
		return rotation;
	}

	@Override
	public void setRotation (float rotation) {
		this.rotation = rotation;
		translate();
	}

	@Override
	public Color getColor () {
		return cache.getColor();
	}

	@Override
	public void setColor (Color color) {
		cache.clear();
		cache.setColor(color);
		cache.setText(text, 0, 0);
	}

	public void setColor (float r, float g, float b, float a) {
		cache.setColor(new Color(r, g, b, a));
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return boundingRectangle;
	}

	public String getRelativeFontPath () {
		return relativeFontPath;
	}

	public int getFontSize () {
		return fontSize;
	}

	public void setFontSize (int fontSize) {
		if (this.fontSize != fontSize) {
			this.fontSize = fontSize;
			BitmapFont bmpFont = font.get(fontSize);
			cache = new BitmapFontCache(bmpFont);
			textBounds = cache.setText(text, 0, 0);
			textChanged();
		}
	}

	public void setFont (EditorFont font) {
		if(this.font != font) {
			this.font = font;

			relativeFontPath = font.getRelativePath();
			cache = new BitmapFontCache(font.get(fontSize));
			textBounds = cache.setText(text, 0, 0);
			textChanged();
		}
	}

	public EditorFont getFont () {
		return font;
	}
}
