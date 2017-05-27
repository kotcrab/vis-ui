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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestGenerateDisabledImage extends VisWindow {
	public TestGenerateDisabledImage () {
		super("generate disabled image");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		addVisWidgets();

		setSize(300, 150);
		centerWindow();
	}

	private void addVisWidgets () {
		Drawable icon = VisUI.getSkin().getDrawable("icon-folder");
		VisImageButton normal = new VisImageButton(icon);
		VisImageButton disabled = new VisImageButton(icon);
		disabled.setGenerateDisabledImage(true);
		disabled.setDisabled(true);
		add(new VisLabel("VisImageButton normal"));
		add(normal).row();
		add(new VisLabel("VisImageButton disabled"));
		add(disabled).row();

		VisImageTextButton normalText = new VisImageTextButton("text", icon);
		VisImageTextButton disabledText = new VisImageTextButton("text", icon);
		disabledText.setGenerateDisabledImage(true);
		disabledText.setDisabled(true);
		add(new VisLabel("VisImageTextButton normal"));
		add(normalText).row();
		add(new VisLabel("VisImageTextButton disabled"));
		add(disabledText).padBottom(3f).row();
	}
}
