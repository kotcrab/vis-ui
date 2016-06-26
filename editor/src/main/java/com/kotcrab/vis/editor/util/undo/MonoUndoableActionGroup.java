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

package com.kotcrab.vis.editor.util.undo;

import com.badlogic.gdx.utils.Array;

/**
 * Allows to chain multiple {@link UndoableAction} into single group, supports generic if only one type of action needs
 * to be stored, see {@link UndoableActionGroup} if you need to mix different types of actions and want to avoid generics.
 * @author Kotcrab
 */
public class MonoUndoableActionGroup<T extends UndoableAction> implements UndoableAction {
	protected Array<T> actions = new Array<>();

	private boolean finalized;
	private boolean reversed;

	private String singularActionName;
	private String pluralActionName;

	public MonoUndoableActionGroup () {
	}

	public MonoUndoableActionGroup (String actionName) {
		this.singularActionName = actionName;
		this.pluralActionName = actionName;
	}

	public MonoUndoableActionGroup (String singularActionName, String pluralActionName) {
		this.singularActionName = singularActionName;
		this.pluralActionName = pluralActionName;
	}

	@Override
	public void execute () {
		if (!finalized) throw new IllegalStateException("Group must be finalized before use");

		if (reversed) {
			actions.reverse();
			reversed = false;
		}

		for (T a : actions)
			a.execute();
	}

	@Override
	public void undo () {
		if (!finalized) throw new IllegalStateException("Group must be finalized before use");

		if (reversed == false) {
			actions.reverse();
			reversed = true;
		}

		for (T a : actions)
			a.undo();
	}

	public void finalizeGroup () {
		finalized = true;
	}

	public int size () {
		return actions.size;
	}

	public void add (T action) {
		if (finalized) throw new IllegalStateException("Cannot add action to finalized group");

		actions.add(action);
	}

	public void execute (T action) {
		if (finalized) throw new IllegalStateException("Cannot add action to finalized group");

		action.execute();
		add(action);
	}

	@Override
	public String getActionName () {
		return size() == 1 ? singularActionName : pluralActionName;
	}
}
