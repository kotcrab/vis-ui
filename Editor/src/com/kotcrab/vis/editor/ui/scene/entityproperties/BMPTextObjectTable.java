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
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisCheckBox;

class BMPTextObjectTable extends TextObjectTable {
	private VisCheckBox distanceFieldCheck;

	public BMPTextObjectTable (EntityProperties properties) {
		super(properties);

		distanceFieldCheck = new VisCheckBox("Use DF");
		distanceFieldCheck.addListener(properties.getSharedChangeListener());
		fontPropertiesTable.add(distanceFieldCheck);

		new Tooltip(distanceFieldCheck, "Use distance field shader for this text");
	}

	@Override
	public boolean isSupported (EditorEntity entity) {
		if (entity instanceof TextObject == false) return false;
		TextObject obj = (TextObject) entity;
		return !obj.isTrueType();
	}

	@Override
	protected int getRelativeFontFolderLength () {
		return properties.getFileAccessModule().getBMPFontFolderRelative().length();
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();
		setCheckForEntities();
	}

	private void setCheckForEntities () {
		Array<EditorEntity> entities = properties.getEntities();

		boolean enabled = ((TextObject)entities.first()).isDistanceFieldShaderEnabled();
		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			if (enabled != obj.isDistanceFieldShaderEnabled()) {
				distanceFieldCheck.setChecked(false);
				return;
			}
		}
		distanceFieldCheck.setChecked(enabled);
	}


	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();

		Array<EditorEntity> entities = properties.getEntities();
		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			obj.setDistanceFieldShaderEnabled(distanceFieldCheck.isChecked());
		}
	}
}
