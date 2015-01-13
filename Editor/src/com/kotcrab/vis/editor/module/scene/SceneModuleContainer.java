/**
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

import com.kotcrab.vis.editor.module.EditorModuleContainer;
import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.BaseModuleContainer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class SceneModuleContainer extends BaseModuleContainer<SceneModule> {
	private Project project;
	private EditorModuleContainer editorModuleContainer;
	private ProjectModuleContainer projectModuleContainer;

	private EditorScene scene;

	public SceneModuleContainer (ProjectModuleContainer projectModuleContainer, EditorScene scene) {
		this.editorModuleContainer = projectModuleContainer.getEditorContainer();
		this.projectModuleContainer = projectModuleContainer;
		this.scene = scene;
	}

	@Override
	public void add (SceneModule module) {
		module.setProject(projectModuleContainer.getProject());
		module.setProjectModuleContainter(projectModuleContainer);
		module.setContainer(editorModuleContainer);
		module.setSceneObjects(this, scene);

		super.add(module);
	}

	public void setProject (Project project) {
		if (getModuleCounter() > 0) throw new IllegalStateException("Project can't be changed while modules are loaded!");
		this.project = project;
	}

	public Project getProject () {
		return project;
	}

	public void render (Batch batch) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).render(batch);
	}

	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).touchDown(event, x, y, pointer, button)) return true;

		return false;
	}

	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).touchUp(event, x, y, pointer, button);
	}

	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).touchDragged(event, x, y, pointer);
	}

	public boolean mouseMoved (InputEvent event, float x, float y) {
		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).mouseMoved(event, x, y)) return true;

		return false;
	}

	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).enter(event, x, y, pointer, fromActor);
	}

	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).exit(event, x, y, pointer, toActor);
	}

	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).scrolled(event, x, y, amount)) return true;

		return false;
	}

	public boolean keyDown (InputEvent event, int keycode) {
		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyDown(event, keycode)) return true;

		return false;
	}

	public boolean keyUp (InputEvent event, int keycode) {
		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyUp(event, keycode)) return true;

		return false;
	}

	public boolean keyTyped (InputEvent event, char character) {
		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyTyped(event, character)) return true;

		return false;
	}
}
