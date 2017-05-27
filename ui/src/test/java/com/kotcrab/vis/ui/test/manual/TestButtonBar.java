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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.ButtonBar;
import com.kotcrab.vis.ui.widget.ButtonBar.ButtonType;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/** @author Kotcrab */
public class TestButtonBar extends VisWindow {

	public TestButtonBar () {
		super("buttonbar");

		addCloseButton();
		closeOnEscape();

		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();

		add(new VisLabel("Windows: "));
		add(createTable(ButtonBar.WINDOWS_ORDER)).expand().fill();
		row();

		add(new VisLabel("Linux: "));
		add(createTable(ButtonBar.LINUX_ORDER)).expand().fill();
		row();

		add(new VisLabel("Mac: "));
		add(createTable(ButtonBar.OSX_ORDER)).expand().fill();
		row();

		pack();
		setPosition(300, 245);
	}

	private VisTable createTable (String order) {
		ButtonBar buttonBar = new ButtonBar(order);

		ChangeListener dummyListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {

			}
		};

		buttonBar.setButton(ButtonType.LEFT, dummyListener);
		buttonBar.setButton(ButtonType.RIGHT, dummyListener);
		buttonBar.setButton(ButtonType.HELP, dummyListener);
		buttonBar.setButton(ButtonType.NO, dummyListener);
		buttonBar.setButton(ButtonType.YES, dummyListener);
		buttonBar.setButton(ButtonType.CANCEL, dummyListener);
		buttonBar.setButton(ButtonType.BACK, dummyListener);
		buttonBar.setButton(ButtonType.NEXT, dummyListener);
		buttonBar.setButton(ButtonType.APPLY, dummyListener);
		buttonBar.setButton(ButtonType.FINISH, dummyListener);
		buttonBar.setButton(ButtonType.OK, dummyListener);
		return buttonBar.createTable();
	}
}
