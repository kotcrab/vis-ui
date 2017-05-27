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

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.BusyBar;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;

public class TestBusyBar extends VisWindow {
	public TestBusyBar () {
		super("busybar");

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		addCloseButton();
		addVisWidgets();

		setResizable(true);
		setSize(320, 170);
		centerWindow();
	}

	private void addVisWidgets () {
		BusyBar busyBar = new BusyBar();
		add(busyBar).top().space(0).growX().row();
		add(new VisLabel("Working...", Align.center)).grow().center();
	}
}
