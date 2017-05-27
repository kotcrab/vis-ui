/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;

/** @author Kotcrab */
public class IconStack extends WidgetGroup {
	private VisImage icon;
	private VisCheckBox checkBox;

	public IconStack (VisImage icon, VisCheckBox checkBox) {
		this.icon = icon;
		this.checkBox = checkBox;
		setTouchable(Touchable.childrenOnly);
		addActor(icon);
		addActor(checkBox);
	}

	@Override
	public void layout () {
		icon.setBounds(getWidth() / 2, getHeight() / 2, getPrefWidth(), getPrefHeight());
		float checkHeight = checkBox.getStyle().checkBackground.getMinHeight();
		checkBox.setBounds(3, getHeight() - checkHeight - 3, checkBox.getPrefWidth(), checkBox.getPrefHeight());
	}
}
