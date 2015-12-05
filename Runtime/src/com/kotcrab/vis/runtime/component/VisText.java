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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.component.proto.ProtoVisText;
import com.kotcrab.vis.runtime.properties.*;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;

/**
 * Text component storing all text properties
 * @author Kotcrab
 */
public class VisText extends Component implements PositionOwner, SizeOwner, BoundsOwner, ScaleOwner,
		RotationOwner, OriginOwner, TintOwner, UsesProtoComponent {
	private boolean distanceFieldShaderEnabled;

	private transient BitmapFontCache cache;
	private transient GlyphLayout textLayout;

	private float x = 0, y = 0;
	private float originX = 0, originY = 0;
	private float scaleX = 1, scaleY = 1;
	private float rotation = 0;
	private Color color = Color.WHITE;
	private Rectangle boundingRectangle;
	private boolean autoSetOriginToCenter = true;
	public Matrix4 translationMatrix;
	private CharSequence text;

	/** Creates empty component, {@link #init(BitmapFont, String)} must be called before use */
	public VisText () {
	}

	public VisText (BitmapFont bitmapFont, String text) {
		init(bitmapFont, text);
	}

	public void init (BitmapFont bitmapFont, String text) {
		this.text = text;

		cache = new BitmapFontCache(bitmapFont);
		translationMatrix = new Matrix4();
		textLayout = new GlyphLayout();
		setText(text);
		if (autoSetOriginToCenter == true) setOriginCenter();
		translate();
	}

	public VisText (VisText other) {
		this(other.cache.getFont(), other.getText());

		setAutoSetOriginToCenter(other.isAutoSetOriginToCenter());
		setDistanceFieldShaderEnabled(other.isDistanceFieldShaderEnabled());
		setX(other.getX());
		setY(other.getY());
		setOrigin(other.getOriginX(), other.getOriginY());
		setScale(other.getScaleX(), other.getScaleY());
		setRotation(other.getRotation());
		setTint(other.getTint());
	}

	public void setFont (BitmapFont font) {
		cache = new BitmapFontCache(font);
		setText(text);
		setTint(color);
	}

	private void translate () {
		translationMatrix.idt();
		translationMatrix.translate(x + originX, y + originY, 0);
		translationMatrix.rotate(0, 0, 1, rotation);
		translationMatrix.scale(scaleX, scaleY, 1);
		translationMatrix.translate(-originX, -originY, 0);
		translationMatrix.translate(0, textLayout.height, 0);
		calculateBoundingRectangle();
	}

	public BitmapFontCache getCache () {
		return cache;
	}

	protected void textChanged () {
		if (autoSetOriginToCenter == true) setOriginCenter();
		translate();
	}

	private void calculateBoundingRectangle () {
		Polygon polygon = new Polygon(new float[]{0, 0, textLayout.width, 0, textLayout.width, textLayout.height, 0, textLayout.height});
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
		textLayout = cache.setText(str, 0, 0);
		cache.setColor(color);
		textChanged();
	}

	public void setText (CharSequence str, Color color) {
		this.text = str;
		this.color = color;
		cache.clear();
		textLayout = cache.setText(str, 0, 0);
		cache.setColor(color);
		textChanged();
	}

	public boolean isAutoSetOriginToCenter () {
		return autoSetOriginToCenter;
	}

	public void setAutoSetOriginToCenter (boolean autoSetOriginToCenter) {
		this.autoSetOriginToCenter = autoSetOriginToCenter;
		textChanged();
	}

	public void setOriginCenter () {
		setOrigin(textLayout.width / 2, -textLayout.height / 2);
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
	public Color getTint () {
		return color;
	}

	@Override
	public void setTint (Color tint) {
		this.color = tint;
		setText(text);
	}

	public void setColor (float r, float g, float b, float a) {
		setTint(new Color(r, g, b, a));
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return boundingRectangle;
	}

	@Override
	public ProtoComponent toProtoComponent () {
		return new ProtoVisText(this);
	}

	public boolean isDistanceFieldShaderEnabled () {
		return distanceFieldShaderEnabled;
	}

	public void setDistanceFieldShaderEnabled (boolean distanceFieldShaderEnabled) {
		this.distanceFieldShaderEnabled = distanceFieldShaderEnabled;
	}
}
