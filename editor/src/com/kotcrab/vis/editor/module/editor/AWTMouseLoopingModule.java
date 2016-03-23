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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.editor.Log;

import java.awt.*;

/**
 * Uses AWT Robot which is not supported on all platforms.
 * @author Kotcrab
 * @see MouseLoopingModule
 */
class AWTMouseLoopingModule extends MouseLoopingModule {
	private static final int MARGIN = 15;

	private GlobalInputModule globalInput;

	private boolean catchEnabled = false;

	private Robot awtRobot;
	private Rectangle screenBounds;

	private float virtualDeltaX;
	private float virtualDeltaY;

	private float virtualMouseX;
	private float virtualMouseY;

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
				Point mousePos = MouseInfo.getPointerInfo().getLocation();

				virtualDeltaX = 0;
				virtualDeltaY = 0;
				virtualMouseX = mousePos.x;
				virtualMouseY = mousePos.y;
				return true;
			}

			@Override
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				if (catchEnabled == false || screenBounds == null) return;

				Point mousePos = MouseInfo.getPointerInfo().getLocation();

				int moveDeltaX = 0;
				int moveDeltaY = 0;

				if (mousePos.x <= screenBounds.x) {
					moveDeltaX = screenBounds.width - MARGIN;
				} else if (mousePos.x >= screenBounds.x + screenBounds.width - 1) {
					moveDeltaX = -screenBounds.width + MARGIN;
				}

				if (mousePos.y <= screenBounds.y) {
					moveDeltaY = screenBounds.height - MARGIN;
				} else if (mousePos.y >= screenBounds.y + screenBounds.height - 1) {
					moveDeltaY = -screenBounds.height + MARGIN;
				}

				boolean move = false;
				int newMouseX = mousePos.x;
				int newMouseY = mousePos.y;

				if (moveDeltaX != 0) {
					newMouseX = mousePos.x + moveDeltaX;
					virtualDeltaX -= moveDeltaX;
					move = true;
				}

				if (moveDeltaY != 0) {
					newMouseY = mousePos.y + moveDeltaY;
					virtualDeltaY -= moveDeltaY;
					move = true;
				}

				if (move) {
					awtRobot.mouseMove(newMouseX, newMouseY);
					mousePos.setLocation(newMouseX, newMouseY); //manually update mousePos
				}

				virtualMouseX = mousePos.x + virtualDeltaX;
				virtualMouseY = mousePos.y + virtualDeltaY;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				catchEnabled = false;
				virtualMouseX = 0;
				virtualMouseY = 0;
			}
		});
	}

	@Override
	public boolean isLoopingSupported () {
		return true;
	}

	@Override
	public void loopCursor () {
		this.catchEnabled = true;
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		virtualMouseX = mousePos.x;
		virtualMouseY = mousePos.y;
		screenBounds = getScreenBoundsAt(mousePos);
	}

	@Override
	public float getVirtualMouseX () {
		if (catchEnabled)
			return virtualMouseX;
		else
			return MouseInfo.getPointerInfo().getLocation().x;
	}

	@Override
	public float getVirtualMouseY () {
		if (catchEnabled)
			return virtualMouseY;
		else
			return MouseInfo.getPointerInfo().getLocation().y;
	}

	@Override
	public boolean isOnVirtualScreen () {
		return virtualDeltaX != 0 || virtualDeltaY != 0;
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
