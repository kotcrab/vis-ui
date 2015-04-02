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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.ui.widget.VisTextField;

/** Listens for events from target table and passes them to provided {@link ModuleInput} */
public class TableInputListener extends InputListener {
	private Table focusTarget;
	private ModuleInput inputProcessor;

	public TableInputListener (Table focusTarget, ModuleInput inputProcessor) {
		this.focusTarget = focusTarget;
		this.inputProcessor = inputProcessor;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		//we don't want to steal text field focus, so if event occurred on it, do not change focus
		if (event.getTarget() instanceof VisTextField == false)
			Editor.instance.getStage().setKeyboardFocus(focusTarget);

		Editor.instance.getStage().setScrollFocus(focusTarget);

		return inputProcessor.touchDown(event, Gdx.input.getX(), Gdx.input.getY(), pointer, button);
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		inputProcessor.touchUp(event, Gdx.input.getX(), Gdx.input.getY(), pointer, button);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		inputProcessor.touchDragged(event, Gdx.input.getX(), Gdx.input.getY(), pointer);
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		return inputProcessor.mouseMoved(event, Gdx.input.getX(), Gdx.input.getY());
	}

	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
		inputProcessor.enter(event, Gdx.input.getX(), Gdx.input.getY(), pointer, fromActor);
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		inputProcessor.exit(event, Gdx.input.getX(), Gdx.input.getY(), pointer, toActor);
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		return inputProcessor.scrolled(event, Gdx.input.getX(), Gdx.input.getY(), amount);
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		return inputProcessor.keyDown(event, keycode);
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		return inputProcessor.keyUp(event, keycode);
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		return inputProcessor.keyTyped(event, character);
	}
}
