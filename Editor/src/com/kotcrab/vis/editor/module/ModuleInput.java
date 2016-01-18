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

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

/**
 * Interface for classes that accepts input events from {@link ModuleContainer}
 * @author Kotcrab
 */
public interface ModuleInput {
	default boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return false;
	}

	default void touchUp (InputEvent event, float x, float y, int pointer, int button) {

	}

	default void touchDragged (InputEvent event, float x, float y, int pointer) {

	}

	default boolean mouseMoved (InputEvent event, float x, float y) {
		return false;

	}

	default void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
	}

	default void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
	}

	default boolean scrolled (InputEvent event, float x, float y, int amount) {
		return false;
	}

	default boolean keyDown (InputEvent event, int keycode) {
		return false;
	}

	default boolean keyUp (InputEvent event, int keycode) {
		return false;
	}

	default boolean keyTyped (InputEvent event, char character) {
		return false;

	}
}
