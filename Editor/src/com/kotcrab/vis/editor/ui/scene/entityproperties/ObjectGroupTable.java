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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.ObjectGroup;
import com.kotcrab.vis.editor.ui.IndeterminateCheckbox;
import com.kotcrab.vis.ui.widget.Tooltip;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.setCheckBoxState;

class ObjectGroupTable extends SpecificObjectTable {
	private IndeterminateCheckbox preserveCheck;

	public ObjectGroupTable (EntityProperties properties) {
		super(properties, true);
		preserveCheck = new IndeterminateCheckbox("Preserve on runtime");
		new Tooltip(preserveCheck, "Controls whether to preserve this group on runtime.\nIf enabled it will be possible to get this group by ID");

		preserveCheck.addListener(properties.getSharedCheckBoxChangeListener());

		padTop(0);
		padLeft(3);
		left();
		add(preserveCheck);
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		return entity instanceof ObjectGroup;
	}

	@Override
	public void updateUIValues () {
		Array<EditorObject> entities = properties.getEntities();

		setCheckBoxState(entities, preserveCheck, entity -> ((ObjectGroup) entity).isPreserveOnRuntime());
	}

	@Override
	public void setValuesToEntities () {
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			ObjectGroup obj = (ObjectGroup) entity;

			if (preserveCheck.isIndeterminate() == false) obj.setPreserveForRuntime(preserveCheck.isChecked());
		}
	}
}
