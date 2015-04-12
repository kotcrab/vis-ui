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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.ui.VisUI;

class TintImage extends Image {
	private final Drawable alphaBar = Assets.getMisc("alpha-grid-20x20");
	private final Drawable white = VisUI.getSkin().getDrawable("white");
	private final Drawable questionMark = Assets.getIcon(Icons.QUESTION);

	private boolean unknown;

	public TintImage () {
		super();
		setDrawable(white);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);

		if (unknown)
			questionMark.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
		else {
			alphaBar.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
			super.draw(batch, parentAlpha);
		}
	}

	public void setUnknown (boolean unknown) {
		this.unknown = unknown;
	}

	@Override
	public void setColor (Color color) {
		super.setColor(color);
	}
}
