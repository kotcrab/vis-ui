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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.component.proto.ProtoVisText;
import com.kotcrab.vis.runtime.properties.BoundsOwner;
import com.kotcrab.vis.runtime.properties.SizeOwner;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.annotation.VisInternal;
import com.kotcrab.vis.runtime.util.autotable.ATFieldId;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFile;
import com.kotcrab.vis.runtime.util.autotable.ATTextProperty;

/**
 * Text component storing all text properties.
 * @author Kotcrab
 */
public class VisText extends Component implements SizeOwner, BoundsOwner, UsesProtoComponent {
	private transient boolean dirty = true;

	@ATFieldId(id = "autoSetOriginToCenter")
	@ATProperty(fieldName = "Auto Set Origin to Center")
	private boolean autoSetOriginToCenter = true;
	@ATFieldId(id = "distanceFieldShaderEnabled")
	@ATProperty(fieldName = "Enable Distance Field", tooltip = "Controls whether to use distance field shader for rendering this text.\nNote that " +
			"this is only useful for fonts that were generated for\ndistance field shader and may produce unexpected results on standard fonts.")
	private boolean distanceFieldShaderEnabled;

	@ATTextProperty(fieldName = "Text")
	@ATFieldId(id = "text")
	private CharSequence text;

	@ATSelectFile(fieldName = "Font", extension = "ext handler overrides", handlerAlias = "font", extHandlerAlias = "font")
	private transient BitmapFontCache cache;
	private transient GlyphLayout textLayout;

	private Rectangle bounds = new Rectangle();
	private Matrix4 translationMatrix;

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
	}

	public VisText (VisText other) {
		this(other.cache.getFont(), other.getText());

		setAutoSetOriginToCenter(other.isAutoSetOriginToCenter());
		setDistanceFieldShaderEnabled(other.isDistanceFieldShaderEnabled());
	}

	public void setFont (BitmapFont font) {
		cache = new BitmapFontCache(font);
		setText(text);
	}

	public BitmapFontCache getCache () {
		return cache;
	}

	@VisInternal
	public void updateCache (Color tint) {
		cache.clear();
		cache.setColor(tint);
		textLayout = cache.setText(text, 0, 0);
		dirty = false;
	}

	@VisInternal
	public void updateBounds (Rectangle bounds) {
		this.bounds.set(bounds);
	}

	public GlyphLayout getGlyphLayout () {
		return textLayout;
	}

	public Matrix4 getTranslationMatrix () {
		return translationMatrix;
	}

	public String getText () {
		return text.toString();
	}

	public void setText (CharSequence str) {
		this.text = str;
		dirty = true;
	}

	public boolean isDirty () {
		return dirty;
	}

	public boolean isAutoSetOriginToCenter () {
		return autoSetOriginToCenter;
	}

	public void setAutoSetOriginToCenter (boolean autoSetOriginToCenter) {
		this.autoSetOriginToCenter = autoSetOriginToCenter;
	}

	public boolean isDistanceFieldShaderEnabled () {
		return distanceFieldShaderEnabled;
	}

	public void setDistanceFieldShaderEnabled (boolean distanceFieldShaderEnabled) {
		this.distanceFieldShaderEnabled = distanceFieldShaderEnabled;
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
	public Rectangle getBoundingRectangle () {
		return bounds;
	}

	@Override
	public ProtoComponent<VisText> toProtoComponent () {
		return new ProtoVisText(this);
	}
}
