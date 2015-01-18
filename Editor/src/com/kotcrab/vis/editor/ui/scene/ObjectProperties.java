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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

public class ObjectProperties extends VisTable {
	private VisTable propertiesTable;
	private Cell<VisTable> tableCell;

	public ObjectProperties () {
		super(true);
		setBackground(VisUI.skin.getDrawable("window-bg"));

		propertiesTable = new VisTable(true);

		top();
		add(new VisLabel("Actor Properties"));
		tableCell = add(propertiesTable).fillY().expandY();
	}

	@Override
	public void setVisible (boolean visible) {
		super.setVisible(visible);
		invalidateHierarchy();
	}

	@Override
	public float getPrefHeight () {
		if(isVisible())
			return 300;
		else
			return 0;
	}
}
