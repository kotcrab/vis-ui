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

package com.kotcrab.vis.editor.module.scene.system;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.util.undo.UndoableAction;

/**
 * Provides way to safely add or remove components for example from {@link UndoableAction}
 * @author Kotcrab
 */
public class VisComponentManipulator extends BaseSystem {
	private EntityManipulatorModule entityManipulatorModule;

	private Array<Job> jobs = new Array<>();

	public void createComponent (Entity entity, Class<? extends Component> component) {
		jobs.add(new CreateComponentJob(entity, component));
	}

	public void removeComponent (Entity entity, Class<? extends Component> component) {
		jobs.add(new RemoveComponentJob(entity, component));
	}

	public void modifyComposition (Entity entity, Component component, boolean add) {
		jobs.add(new ModifyCompositionJob(entity, component, add));
	}

	@Override
	protected void processSystem () {
		if (jobs.size == 0) return;

		for (Job job : jobs) {
			job.execute();
		}

		jobs.clear();

		entityManipulatorModule.markSceneDirty();
		Gdx.app.postRunnable(() -> entityManipulatorModule.selectedEntitiesChanged());
	}

	private static class CreateComponentJob extends Job {
		public Class<? extends Component> component;

		public CreateComponentJob (Entity entity, Class<? extends Component> component) {
			super(entity);
			this.component = component;
		}

		@Override
		public void execute () {
			EntityEdit editor = entity.edit();
			editor.create(component);
		}
	}

	private static class RemoveComponentJob extends Job {
		public Class<? extends Component> component;

		public RemoveComponentJob (Entity entity, Class<? extends Component> component) {
			super(entity);
			this.component = component;
		}

		@Override
		public void execute () {
			EntityEdit editor = entity.edit();
			editor.remove(component);
		}
	}

	private static class ModifyCompositionJob extends Job {
		public Component component;
		public boolean add;

		public ModifyCompositionJob (Entity entity, Component component, boolean add) {
			super(entity);
			this.component = component;
			this.add = add;
		}

		@Override
		public void execute () {
			EntityEdit editor = entity.edit();
			if (add)
				editor.add(component);
			else
				editor.remove(component);
		}
	}

	private abstract static class Job {
		public Entity entity;

		public Job (Entity entity) {
			this.entity = entity;
		}

		abstract void execute ();
	}
}
