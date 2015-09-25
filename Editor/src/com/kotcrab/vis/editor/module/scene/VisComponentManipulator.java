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

package com.kotcrab.vis.editor.module.scene;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityEdit;
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

	public void addJob (Entity entity, Component component, boolean add) {
		jobs.add(new Job(entity, component, add));
	}

	@Override
	protected void processSystem () {
		if (jobs.size == 0) return;

		for (Job job : jobs) {
			EntityEdit editor = job.entity.edit();

			if (job.add)
				editor.add(job.component);
			else
				editor.remove(job.component);
		}

		jobs.clear();

		entityManipulatorModule.markSceneDirty();
		entityManipulatorModule.selectedEntitiesChanged();
	}

	private static class Job {
		public Entity entity;
		public Component component;
		public boolean add;

		public Job (Entity entity, Component component, boolean add) {
			this.entity = entity;
			this.component = component;
			this.add = add;
		}
	}
}
