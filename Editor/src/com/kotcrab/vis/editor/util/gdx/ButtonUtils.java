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

package com.kotcrab.vis.editor.util.gdx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ButtonUtils {
	/**
	 * Allows to disable programmatic events on button. This uses workaround and should be removed
	 * when button would allow to disable programmatic change events. In your change listener you still have to
	 * check whether change event was stopped (event.isStopped())
	 */
	public static void disableProgrammaticEvents (Button target) {
		EventController controller = new EventController();

		//smuggling our listener as first so we can detect and disable programmatic change listeners
		target.getListeners().insert(0, new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				controller.allowEvent = true;
			}
		});

		target.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (controller.allowEvent == false)
					event.stop();

				controller.allowEvent = false;
			}
		});
	}

	private static class EventController {
		public boolean allowEvent;
	}
}
