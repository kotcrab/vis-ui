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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * Listens for events from target table and passes them to provided {@link ModuleInput}
 * @author Kotcrab
 */
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
