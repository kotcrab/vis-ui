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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

/** @author Kotcrab */
public class StyleProviderModule extends EditorModule {
	private Skin skin;
	private Drawable greyDrawable;
	private Drawable redDrawable;

	@Override
	public void init () {
		skin = VisUI.getSkin();
		greyDrawable = skin.getDrawable("grey");
		redDrawable = skin.getDrawable("vis-red");
	}

	/** 'X' button style with no up drawable (transparent), over drawable is grey and down drawable is red */
	public VisImageButtonStyle transparentXButton () {
		VisImageButtonStyle style = new VisImageButtonStyle(skin.get("close", VisImageButtonStyle.class));
		style.up = null;
		style.over = greyDrawable;
		style.down = redDrawable;
		return style;
	}
}
