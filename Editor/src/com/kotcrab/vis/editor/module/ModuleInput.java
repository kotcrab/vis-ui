/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

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
