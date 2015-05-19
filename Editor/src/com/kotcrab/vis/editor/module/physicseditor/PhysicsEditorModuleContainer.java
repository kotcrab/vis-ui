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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.Module;
import com.kotcrab.vis.editor.module.ModuleContainer;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;
import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;

//TODO very similar to SceneModuleContainer, create same parent for them or sth

/**
 * Module container for modules making physics editor
 * @author Kotcrab
 */
public class PhysicsEditorModuleContainer extends ModuleContainer<PhysicsEditorModule> implements ModuleInput {
	private Project project;
	private EditorModuleContainer editorModuleContainer;
	private ProjectModuleContainer projectModuleContainer;

	private PhysicsEditorTab editorTab;

	public PhysicsEditorModuleContainer (ProjectModuleContainer projectModuleContainer, PhysicsEditorTab editorTab) {
		this.editorModuleContainer = projectModuleContainer.getEditorContainer();
		this.projectModuleContainer = projectModuleContainer;
		this.editorTab = editorTab;
	}

	@Override
	public void add (PhysicsEditorModule module) {
		module.setProject(projectModuleContainer.getProject());
		module.setProjectModuleContainer(projectModuleContainer);
		module.setContainer(editorModuleContainer);
		module.setObjects(this, editorTab);

		super.add(module);
	}

	@Override
	public <C extends Module> C findInHierarchy (Class<C> moduleClass) {
		C module = getOrNull(moduleClass);
		if (module != null) return module;

		return projectModuleContainer.findInHierarchy(moduleClass);
	}

	public Project getProject () {
		return project;
	}

	public void setProject (Project project) {
		if (getModuleCounter() > 0)
			throw new IllegalStateException("Project can't be changed while modules are loaded!");
		this.project = project;
	}

	public void render (Batch batch) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).render(batch);
	}

	public void onShow () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).onShow();
	}

	public void onHide () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).onHide();
	}

	public void save () {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).save();
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).touchDown(event, x, y, pointer, button)) returnValue = true;

		return returnValue;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).touchUp(event, x, y, pointer, button);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).touchDragged(event, x, y, pointer);
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).mouseMoved(event, x, y)) returnValue = true;

		return returnValue;
	}

	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).enter(event, x, y, pointer, fromActor);
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		for (int i = 0; i < modules.size; i++)
			modules.get(i).exit(event, x, y, pointer, toActor);
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).scrolled(event, x, y, amount)) returnValue = true;

		return returnValue;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyDown(event, keycode)) returnValue = true;

		return returnValue;
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyUp(event, keycode)) returnValue = true;

		return returnValue;
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		boolean returnValue = false;

		for (int i = 0; i < modules.size; i++)
			if (modules.get(i).keyTyped(event, character)) returnValue = true;

		return returnValue;
	}
}
