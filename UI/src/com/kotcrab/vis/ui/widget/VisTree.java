/*
 * Copyright 2014-2015 Pawel Pastuszak
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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;

public class VisTree extends Tree {
	public VisTree (String styleName) {
		super(VisUI.getSkin(), styleName);
		init();
	}

	public VisTree () {
		super(VisUI.getSkin());
		init();
	}

	public VisTree (TreeStyle style) {
		super(style);
		init();
	}

	private void init () {
		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				FocusManager.getFocus();
				return false;
			}
		});
	}
}
