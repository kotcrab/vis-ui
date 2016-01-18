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

package com.kotcrab.vis.editor.module.scene.action;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class GroupAction implements UndoableAction {
	private final Array<EntityProxy> entities;
	private final int groupId;
	private final boolean group;
	private final int groupIdBefore;

	/** @param group if true group will be created, if false ungroup will be performed */
	public GroupAction (ImmutableArray<EntityProxy> entities, int groupId, int groupIdBefore, boolean group) {
		this.entities = new Array<>(entities.toArray());
		this.groupIdBefore = groupIdBefore;
		this.groupId = groupId;
		this.group = group;
	}

	@Override
	public void execute () {
		reload();

		if (group)
			group();
		else
			ungroup();
	}

	@Override
	public void undo () {
		reload();

		if (group)
			ungroup();
		else
			group();
	}

	@Override
	public String getActionName () {
		return group ? "Group" : "Ungroup";
	}

	private void group () {
		for (EntityProxy entity : entities)
			entity.addGroup(groupId, groupIdBefore);
	}

	private void ungroup () {
		for (EntityProxy entity : entities)
			entity.removeGroup(groupId);
	}

	private void reload () {
		for (EntityProxy entity : entities)
			entity.reload();
	}
}
