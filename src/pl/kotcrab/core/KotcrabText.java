/*******************************************************************************
 * Copyright 2013 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/** Text that you can scale, rotate, change color itp. Supports distance field fonts
 * 
 * @author Pawel Pastuszak */
public class KotcrabText {
	private float x, y;
	private float originX = 0, originY = 0;
	private float scaleX = 1, scaleY = 1;

	private float rotation;

	private BitmapFontCache bitmapFontCache;
	private TextBounds textBounds;
	private Rectangle boundingRectangle;

	private boolean autoSetOriginToCenter;

	private Matrix4 oldMatrix;
	private Matrix4 newMatrix;

	public KotcrabText (BitmapFont bitmapFont, String text, boolean autoSetOriginToCenter, float x, float y) {
		this.autoSetOriginToCenter = autoSetOriginToCenter;
		this.x = x;
		this.y = y;

		rotation = 0;

		newMatrix = new Matrix4();
		bitmapFontCache = new BitmapFontCache(bitmapFont);
		textBounds = bitmapFontCache.setText(text, 0, 0);

		if (autoSetOriginToCenter == true) setOriginCenter();


		translate();
	}

	public void draw (SpriteBatch spriteBatch) {
		oldMatrix = spriteBatch.getTransformMatrix().cpy();
		spriteBatch.setTransformMatrix(newMatrix);
		bitmapFontCache.draw(spriteBatch);
		spriteBatch.setTransformMatrix(oldMatrix);
	}

	private void translate () {
		newMatrix.idt();

		newMatrix.translate(x + originX, y + originY, 0);
		newMatrix.rotate(0, 0, 1, rotation);
		newMatrix.scale(scaleX, scaleY, 1);
		newMatrix.translate(-originX, -originY, 0);
		newMatrix.translate(0, textBounds.height, 0);

		calculateBoundingRectangle();
	}

	private void calculateBoundingRectangle () {
		Polygon polygon = new Polygon(new float[] {0, 0, textBounds.width, 0, textBounds.width, textBounds.height, 0,
			textBounds.height});

		polygon.setPosition(x, y);
		polygon.setRotation(rotation);
		polygon.setScale(scaleX, scaleY);
		polygon.setOrigin(originX, originY);

		boundingRectangle = polygon.getBoundingRectangle();
	}

	public void setText (String text) {
		textBounds = bitmapFontCache.setText(text, 0, 0);

		if (autoSetOriginToCenter == true) setOriginCenter();

		translate();
	}

	public void setOriginCenter () {
		setOrigin(textBounds.width / 2, -textBounds.height / 2);
	}

	public void center (int screenWidth) {
		x = (screenWidth - textBounds.width * scaleX) / 2;
		translate();
	}

	// Getter and setter
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;

		translate();
	}

	public void setX (float x) {
		this.x = x;

		translate();
	}

	public void setY (float y) {
		this.y = y;

		translate();
	}

	public float getX () {
		return x;
	}

	public float getY () {
		return y;
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
		return bitmapFontCache.getColor();
	}

	public void setColor (float r, float g, float b, float a) {
		bitmapFontCache.setColor(new Color(r, g, b, a));
	}

	public void setColor (Color color) {
		bitmapFontCache.setColor(color);
	}

	public void setSize (float width, float height) {
		setScale(width / textBounds.width, height / textBounds.height);
	}

	public float getWidth () {
		return boundingRectangle.width;
	}

	public float getHeight () {
		return boundingRectangle.height;
	}

	public Rectangle getBoundingRectangle () {
		return boundingRectangle;
	}

}
