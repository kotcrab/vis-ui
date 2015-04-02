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

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.kotcrab.vis.ui.widget.VisTable;

public class ContentTable extends VisTable {
	private BaseModuleContainer container;

	public <T extends BaseModuleContainer & ModuleInput> ContentTable (T container) {
		super(false);
		this.container = container;
		setTouchable(Touchable.enabled);
		addListener(new TableInputListener(this, container));
	}

	@Override
	protected void sizeChanged () {
		super.sizeChanged();
		container.resize();
	}
}
