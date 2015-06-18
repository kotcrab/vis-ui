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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.utils.Array;

/**
 * Allows to chain multiple {@link UndoableAction} into single group.
 * @author Kotcrab
 */
public class UndoableActionGroup implements UndoableAction {
	protected Array<UndoableAction> actions = new Array<>();

	private boolean finalized;
	private boolean reversed;

	@Override
	public void execute () {
		if (!finalized) throw new IllegalStateException("Group must be finalized before use");

		if (reversed) {
			actions.reverse();
			reversed = false;
		}

		for (UndoableAction a : actions)
			a.execute();
	}

	@Override
	public void undo () {
		if (!finalized) throw new IllegalStateException("Group must be finalized before use");

		if (reversed == false) {
			actions.reverse();
			reversed = true;
		}

		for (UndoableAction a : actions)
			a.undo();
	}

	public void finalizeGroup () {
		finalized = true;
	}

	public int size () {
		return actions.size;
	}

	public void add (UndoableAction action) {
		if (finalized) throw new IllegalStateException("Cannot add action to finalized group");

		actions.add(action);
	}

	public void execute (UndoableAction action) {
		if (finalized) throw new IllegalStateException("Cannot add action to finalized group");

		action.execute();
		add(action);
	}

}
