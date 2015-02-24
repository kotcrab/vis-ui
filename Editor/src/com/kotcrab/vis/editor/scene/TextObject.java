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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Text that you can scale, rotate, change color itp. Supports distance field fonts
 * @author Kotcrab
 */
public class TextObject extends EditorEntity {
	private float x = 0, y = 0;
	private float originX = 0, originY = 0;
	private float scaleX = 1, scaleY = 1;
	private float rotation = 0;
	private BitmapFontCache cache;
	private TextBounds textBounds;
	private Rectangle boundingRectangle;
	private boolean autoSetOriginToCenter;
	private Matrix4 translationMatrix;

	public TextObject (BitmapFont bitmapFont) {
		this(bitmapFont, null);
	}

	public TextObject (BitmapFont bitmapFont, String text) {
		this(bitmapFont, text, 0, 0);
	}

	public TextObject (BitmapFont bitmapFont, String text, float x, float y) {
		this(bitmapFont, text, x, y, false);
	}

	public TextObject (BitmapFont bitmapFont, String text, float x, float y, boolean autoSetOriginToCenter) {
		this.autoSetOriginToCenter = autoSetOriginToCenter;
		this.x = x;
		this.y = y;
		translationMatrix = new Matrix4();
		cache = new BitmapFontCache(bitmapFont);
		if (text != null)
			textBounds = cache.setText(text, 0, 0);
		else
			textBounds = cache.getBounds();
		if (autoSetOriginToCenter == true) setOriginCenter();
		translate();
	}

	public void draw (SpriteBatch spriteBatch) {
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

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text.
	 * @see #addText(CharSequence, float, float, int, int)
	 */
	public void setText (CharSequence str) {
		textBounds = cache.setText(str, 0, 0);
		textChanged();
	}

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text.
	 * @see #addText(CharSequence, float, float, int, int)
	 */
	public void setText (CharSequence str, float x, float y) {
		textBounds = cache.setText(str, x, y);
		textChanged();
	}

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text.
	 * @see #addText(CharSequence, float, float, int, int)
	 */
	public void setText (CharSequence str, float x, float y, int start, int end) {
		textBounds = cache.addText(str, x, y, start, end);
		textChanged();
	}

	/**
	 * Adds glyphs for the specified text.
	 * @see #addText(CharSequence, float, float, int, int)
	 */
	public void addText (CharSequence str, float originX, float originY) {
		textBounds = cache.addText(str, originX, originY);
		textChanged();
	}

	/**
	 * Adds glyphs for the the specified text.
	 * @param x The x position for the left most character.
	 * @param y The y position for the top of most capital letters in the font (the {@link BitmapFont#getCapHeight() cap height
	 * textChanged();}).
	 * @param start The first character of the string to draw.
	 * @param end The last character of the string to draw (exclusive).
	 * @return The bounds of the cached string (the height is the distance from y to the baseline).
	 */
	public void addText (CharSequence str, float x, float y, int start, int end) {
		textBounds = cache.addText(str, x, y, start, end);
		textChanged();
	}

	public void setMultiLineText (CharSequence str) {
		textBounds = cache.addMultiLineText(str, 0, 0, 0, HAlignment.LEFT);
		textChanged();
	}

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text, which may contain newlines (\n).
	 * @see #addMultiLineText(CharSequence, float, float, float, HAlignment)
	 */
	public void setMultiLineText (CharSequence str, float x, float y) {
		textBounds = cache.addMultiLineText(str, x, y, 0, HAlignment.LEFT);
		textChanged();
	}

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text, which may contain newlines (\n).
	 * @see #addMultiLineText(CharSequence, float, float, float, HAlignment)
	 */
	public void setMultiLineText (CharSequence str, float x, float y, float alignmentWidth, HAlignment alignment) {
		textBounds = cache.addMultiLineText(str, x, y, alignmentWidth, alignment);
		textChanged();
	}

	/**
	 * Adds glyphs for the specified text, which may contain newlines (\n).
	 * @see #addMultiLineText(CharSequence, float, float, float, HAlignment)
	 */
	public void addMultiLineText (CharSequence str, float x, float y) {
		textBounds = cache.addMultiLineText(str, x, y, 0, HAlignment.LEFT);
		textChanged();
	}

	/**
	 * Adds glyphs for the specified text, which may contain newlines (\n). Each line is aligned horizontally within a rectangle of
	 * the specified width.
	 * @param x The x position for the left most character.
	 * @param y The y position for the top of most capital letters in the font (the {@link BitmapFont#getCapHeight() cap height
	 * textChanged();}).
	 * @param alignment The horizontal alignment of wrapped line.
	 * @return The bounds of the cached string (the height is the distance from y to the baseline of the last line).
	 */
	public void addMultiLineText (CharSequence str, float x, float y, float alignmentWidth, HAlignment alignment) {
		textBounds = cache.addMultiLineText(str, x, y, alignmentWidth, alignment);
		textChanged();
	}

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text, which may contain newlines (\n) and is automatically
	 * wrapped within the specified width.
	 * @see #addWrappedText(CharSequence, float, float, float, HAlignment)
	 */
	public void setWrappedText (CharSequence str, float wrapWidth) {
		textBounds = cache.addWrappedText(str, 0, 0, wrapWidth, HAlignment.LEFT);
		textChanged();
	}

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text, which may contain newlines (\n) and is automatically
	 * wrapped within the specified width.
	 * @see #addWrappedText(CharSequence, float, float, float, HAlignment)
	 */
	public void setWrappedText (CharSequence str, float x, float y, float wrapWidth) {
		textBounds = cache.addWrappedText(str, x, y, wrapWidth, HAlignment.LEFT);
		textChanged();
	}

	/**
	 * Clears any cached glyphs and adds glyphs for the specified text, which may contain newlines (\n) and is automatically
	 * wrapped within the specified width.
	 * @see #addWrappedText(CharSequence, float, float, float, HAlignment)
	 */
	public void setWrappedText (CharSequence str, float x, float y, float wrapWidth, HAlignment alignment) {
		textBounds = cache.addWrappedText(str, x, y, wrapWidth, alignment);
		textChanged();
	}

	/**
	 * Adds glyphs for the specified text, which may contain newlines (\n) and is automatically wrapped within the specified width.
	 * @see #addWrappedText(CharSequence, float, float, float, HAlignment)
	 */
	public void addWrappedText (CharSequence str, float x, float y, float wrapWidth) {
		textBounds = cache.addWrappedText(str, x, y, wrapWidth, HAlignment.LEFT);
		textChanged();
	}

	/**
	 * Adds glyphs for the specified text, which may contain newlines (\n) and is automatically wrapped within the specified width.
	 * @param x The x position for the left most character.
	 * @param y The y position for the top of most capital letters in the font (the {@link BitmapFont#getCapHeight() cap height
	 * textChanged();}).
	 * @param alignment The horizontal alignment of wrapped line.
	 * @return The bounds of the cached string (the height is the distance from y to the baseline of the last line).
	 */
	public void addWrappedText (CharSequence str, float x, float y, float wrapWidth, HAlignment alignment) {
		textBounds = cache.addWrappedText(str, x, y, wrapWidth, alignment);
		textChanged();
	}

	public boolean isAutoSetOriginToCenter () {
		return autoSetOriginToCenter;
	}

	public void enableAutoSetOriginToCenter () {
		autoSetOriginToCenter = true;
	}

	public void disableAutoSetOriginToCenter () {
		autoSetOriginToCenter = false;
	}

	/** Removes all glyphs in the cache. */
	public void clearCache () {
		cache.clear();
	}

	public void setOriginCenter () {
		setOrigin(textBounds.width / 2, -textBounds.height / 2);
	}

	public void center (int screenWidth) {
		x = (screenWidth - textBounds.width * scaleX) / 2;
		translate();
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
		cache.setColor(color);
	}

	public void setColor (float r, float g, float b, float a) {
		cache.setColor(new Color(r, g, b, a));
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return boundingRectangle;
	}

}
