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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;

/**
 * Compatible with {@link Image}. Does not provide additional features.
 * @author Kotcrab
 * @see Image
 */
public class VisImage extends Image {
	public VisImage () {
	}

	public VisImage (NinePatch patch) {
		super(patch);
	}

	public VisImage (TextureRegion region) {
		super(region);
	}

	public VisImage (Texture texture) {
		super(texture);
	}

	public VisImage (String drawableName) {
		super(VisUI.getSkin(), drawableName);
	}

	public VisImage (Skin skin, String drawableName) {
		super(skin, drawableName);
	}

	public VisImage (Drawable drawable) {
		super(drawable);
	}

	public VisImage (Drawable drawable, Scaling scaling) {
		super(drawable, scaling);
	}

	public VisImage (Drawable drawable, Scaling scaling, int align) {
		super(drawable, scaling, align);
	}

	public void setDrawable (Texture texture) {
		setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
	}
}
