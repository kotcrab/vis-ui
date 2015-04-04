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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.project.ProjectModule;

/**
 * Base class for all physics editor modules
 * @author Kotcrab
 */
public abstract class PhysicsEditorModule extends ProjectModule implements ModuleInput {
	protected PhysicsEditorModuleContainer physicsContainer;
	protected PhysicsEditorTab editorTab;

	public void setObjects (PhysicsEditorModuleContainer physicsContainer, PhysicsEditorTab editorTab) {
		this.physicsContainer = physicsContainer;
		this.editorTab = editorTab;
	}

	public void render (Batch batch) {
	}

	/** Called by module container, when editor tab has been switched to tab, that this module belongs to */
	public void onShow () {

	}

	/** Called by module container, when editor tab has been switched to some other tab */
	public void onHide () {

	}

	public void save () {

	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		return false;
	}

	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		return false;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		return false;
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		return false;
	}

}
