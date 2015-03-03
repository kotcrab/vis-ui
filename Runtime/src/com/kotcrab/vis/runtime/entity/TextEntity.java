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

package com.kotcrab.vis.runtime.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Text that you can scale, rotate, change color itp. Supports distance field fonts
 * @author Kotcrab
 */
public class TextEntity extends Entity {
	/** Value used for fontSize filed when BMP font is used */
	public static final int BITMAP_FONT_SIZE = -1;

	protected String relativeFontPath;
	protected int fontSize;
	protected transient BitmapFontCache cache;
	protected boolean distanceFieldShaderEnabled;

	private float x = 0, y = 0;
	private float originX = 0, originY = 0;
	private float scaleX = 1, scaleY = 1;
	private float rotation = 0;
	private Color color = Color.WHITE;
	protected TextBounds textBounds;
	private Rectangle boundingRectangle;
	private boolean autoSetOriginToCenter = true;
	private Matrix4 translationMatrix;
	protected CharSequence text;

	public TextEntity (BitmapFont bitmapFont, String id, String relativeFontPath, String text) {
		this(bitmapFont, id, relativeFontPath, text, BITMAP_FONT_SIZE);
	}

	public TextEntity (BitmapFont bitmapFont, String id, String relativeFontPath, String text, int fontSize) {
		super(id);
		this.relativeFontPath = relativeFontPath;
		this.text = text;
		this.fontSize = fontSize;

		translationMatrix = new Matrix4();
		cache = new BitmapFontCache(bitmapFont);
		textBounds = cache.setText(text, 0, 0);
		if (autoSetOriginToCenter == true) setOriginCenter();
		translate();
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

	protected void textChanged () {
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
		cache.clear();
		textBounds = cache.setText(str, 0, 0);
		textChanged();
	}

	public boolean isAutoSetOriginToCenter () {
		return autoSetOriginToCenter;
	}

	public void setAutoSetOriginToCenter (boolean autoSetOriginToCenter) {
		this.autoSetOriginToCenter = autoSetOriginToCenter;
	}

	public void setOriginCenter () {
		setOrigin(textBounds.width / 2, -textBounds.height / 2);
	}

	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		translate();
	}

	public float getWidth () {
		return getBoundingRectangle().getWidth();
	}

	public float getHeight () {
		return getBoundingRectangle().getHeight();
	}

	public float getX () {
		return x;
	}

	public void setX (float x) {
		this.x = x;
		translate();
	}

	public float getY () {
		return y;
	}

	public void setY (float y) {
		this.y = y;
		translate();
	}

	public void setOrigin (float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
		translate();
	}

	public float getOriginX () {
		return originX;
	}

	public float getOriginY () {
		return originY;
	}

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

	public float getScaleX () {
		return scaleX;
	}

	public float getScaleY () {
		return scaleY;
	}

	public float getRotation () {
		return rotation;
	}

	public void setRotation (float rotation) {
		this.rotation = rotation;
		translate();
	}

	public Color getColor () {
		return color;
	}

	public void setColor (Color color) {
		this.color = color;
		cache.setColor(color);
		cache.setText(text, 0, 0);
	}

	public void setColor (float r, float g, float b, float a) {
		setColor(new Color(r, g, b, a));
	}

	public Rectangle getBoundingRectangle () {
		return boundingRectangle;
	}

	public String getRelativeFontPath () {
		return relativeFontPath;
	}

	public int getFontSize () {
		return fontSize;
	}

	public boolean isTrueType () {
		return fontSize != BITMAP_FONT_SIZE;
	}


	public void setRelativeFontPathForSerialize (String relativeFontPathForSerialize) {
		this.relativeFontPath = relativeFontPathForSerialize;
	}

	public boolean isDistanceFieldShaderEnabled () {
		return distanceFieldShaderEnabled;
	}

	public void setDistanceFieldShaderEnabled (boolean distanceFieldShaderEnabled) {
		this.distanceFieldShaderEnabled = distanceFieldShaderEnabled;
	}
}
