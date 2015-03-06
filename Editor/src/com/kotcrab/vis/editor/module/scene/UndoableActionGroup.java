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

import com.badlogic.gdx.utils.Array;

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
