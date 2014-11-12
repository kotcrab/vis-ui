/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.ui.widget;

import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Separator extends Widget {
	private Drawable bg;

	public Separator () {
		bg = VisUI.skin.getDrawable("splitpane");
	}

	@Override
	public float getPrefWidth () {
		return 1;
	}

	@Override
	public float getPrefHeight () {
		return 3;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		validate();
		bg.draw(batch, getX(), getY(), getWidth(), getHeight());
	}
}
