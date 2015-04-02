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
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.ui.IndeterminateCheckbox;
import com.kotcrab.vis.ui.widget.Tooltip;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.setCheckBoxState;

class ParticleEffectTable extends SpecificObjectTable {
	private IndeterminateCheckbox activeCheck;

	public ParticleEffectTable (EntityProperties properties) {
		super(properties, true);
		activeCheck = new IndeterminateCheckbox("Active on start");
		new Tooltip(activeCheck, "Controls whether to automatically start this effect on runtime.\nIn editor, particle effect are always active");

		activeCheck.addListener(properties.getSharedCheckBoxChangeListener());

		padTop(0);
		padLeft(3);
		left();
		add(activeCheck);
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		return entity instanceof ParticleObject;
	}

	@Override
	public void updateUIValues () {
		Array<EditorObject> entities = properties.getEntities();

		setCheckBoxState(entities, activeCheck, entity -> ((ParticleObject) entity).isActive());
	}

	@Override
	public void setValuesToEntities () {
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			ParticleObject obj = (ParticleObject) entity;

			if (activeCheck.isIndeterminate() == false) obj.setActive(activeCheck.isChecked());
		}
	}
}
