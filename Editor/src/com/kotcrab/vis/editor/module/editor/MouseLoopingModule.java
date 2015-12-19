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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;

import javax.swing.SwingUtilities;
import java.awt.*;

/**
 * Allows to catch mouse inside current screen. Used for example when dragging objects. Uses AWT Robot.
 * @author Kotcrab
 */
public class MouseLoopingModule extends EditorModule {
	private GlobalInputModule globalInput;

	private boolean catchEnabled = false;
	private boolean resetOnTouchUp = true;

	private Robot awtRobot;
	private Rectangle screenBounds;

	@Override
	public void init () {
		try {
			awtRobot = new Robot();
		} catch (AWTException e) {
			Log.exception(e);
		}

		globalInput.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (catchEnabled == false) return false;
				Point pos = new Point((int) x, (int) y);
				SwingUtilities.convertPointToScreen(pos, Editor.instance.getSwingFrame());
				screenBounds = getScreenBoundsAt(pos);
				return true;
			}

			@Override
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				if (catchEnabled == false || screenBounds == null) return;

				Point mousePos = MouseInfo.getPointerInfo().getLocation();

				if (mousePos.x <= screenBounds.x) {
					awtRobot.mouseMove(screenBounds.x + screenBounds.width, mousePos.y);
				} else if (mousePos.x >= screenBounds.x + screenBounds.width - 1)
					awtRobot.mouseMove(screenBounds.x, mousePos.y);

				if (mousePos.y <= screenBounds.y) {
					awtRobot.mouseMove(mousePos.x, screenBounds.y + screenBounds.height);
				} else if (mousePos.y >= screenBounds.y + screenBounds.height - 1) {
					awtRobot.mouseMove(mousePos.x, screenBounds.y);
				}
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (resetOnTouchUp) catchEnabled = false;
			}
		});
	}

	public void setCatchEnabled (boolean catchEnabled) {
		this.catchEnabled = catchEnabled;
	}

	public void setResetOnTouchUp (boolean resetOnTouchUp) {
		this.resetOnTouchUp = resetOnTouchUp;
	}

	private Rectangle getScreenBoundsAt (Point pos) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice devices[] = env.getScreenDevices();

		for (GraphicsDevice gd : devices) {
			GraphicsConfiguration config = gd.getDefaultConfiguration();
			Rectangle screenBounds = config.getBounds();
			if (screenBounds.contains(pos)) {
				return config.getBounds();
			}
		}

		return null;
	}
}
