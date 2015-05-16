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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.editor.util.gdx.EventStopper;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class PModeController extends PhysicsEditorModule {

	private VisTextButton createModeButton;
	private VisTextButton editModeButton;
	private VisTextButton testModeButton;

	public enum Mode {CREATION, EDITION, TEST}

	private PRigidBodiesScreen screen;

	private PModeChangeListener listener;
	private Mode mode = null;

	private VisTable table;

	@Override
	public void init () {
		screen = physicsContainer.get(PRigidBodiesScreen.class);
		createUI();
	}

	private void createUI () {
		table = new VisTable(true);
		table.addListener(new EventStopper());
		table.setTouchable(Touchable.enabled);
		table.setBackground(VisUI.getSkin().getDrawable("window-bg"));
		table.left();
		table.padTop(1);

		createModeButton = new VisTextButton("Create", "toggle");
		editModeButton = new VisTextButton("Edit", "toggle");
		testModeButton = new VisTextButton("Test", "toggle");

		createModeButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				setMode(Mode.CREATION);
			}
		});

		editModeButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				setMode(Mode.EDITION);
			}
		});

		testModeButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				setMode(Mode.TEST);
			}
		});

		createModeButton.setFocusBorderEnabled(false);
		editModeButton.setFocusBorderEnabled(false);
		testModeButton.setFocusBorderEnabled(false);

		table.add(new VisLabel("Mode: "), createModeButton, editModeButton, testModeButton);
	}

	public VisTable getControllerTable () {
		return table;
	}

	public Mode getMode () {
		return mode;
	}

	public void setMode (Mode mode) {
		this.mode = mode;
		if (listener != null) listener.changed(mode);

		createModeButton.setChecked(false);
		editModeButton.setChecked(false);
		testModeButton.setChecked(false);

		switch (mode) {
			case CREATION:
				createModeButton.setChecked(true);
				break;
			case EDITION:
				editModeButton.setChecked(true);
				break;
			case TEST:
				testModeButton.setChecked(true);
				break;
		}
	}

	private void setNextMode () {
		setMode((mode == Mode.CREATION ? Mode.EDITION : (mode == Mode.EDITION ? Mode.TEST : Mode.CREATION)));
	}

	public void setListener (PModeChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (screen.getSelectedModel() != null) {
			if (keycode == Input.Keys.TAB) {
				setNextMode();
				return true;
			}
		}

		return false;
	}

	public interface PModeChangeListener {
		void changed (Mode newMode);
	}

}
