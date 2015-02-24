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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.SceneTab;

public abstract class SceneModule extends ProjectModule {
	protected EditorScene scene;
	protected SceneTab sceneTab;
	protected SceneModuleContainer sceneContainer;

	public void setSceneObjects (SceneModuleContainer projectContainer, SceneTab sceneTab, EditorScene scene) {
		this.sceneContainer = projectContainer;
		this.sceneTab = sceneTab;
		this.scene = scene;
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

	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return false;
	}

	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	}

	public void touchDragged (InputEvent event, float x, float y, int pointer) {
	}

	public boolean mouseMoved (InputEvent event, float x, float y) {
		return false;
	}

	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
	}

	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
	}

	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		return false;
	}

	public boolean keyDown (InputEvent event, int keycode) {
		return false;
	}

	public boolean keyUp (InputEvent event, int keycode) {
		return false;
	}

	public boolean keyTyped (InputEvent event, char character) {
		return false;
	}
}
