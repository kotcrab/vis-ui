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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.VisLabel;

class TTFTextObjectTable extends TextObjectTable {
	private NumberInputField sizeInputField;

	public TTFTextObjectTable (final EntityProperties properties) {
		super(properties);

		sizeInputField = properties.createNewNumberField();
		sizeInputField.addValidator(Validators.INTEGERS);
		sizeInputField.addValidator(new GreaterThanValidator(FontCacheModule.MIN_FONT_SIZE));
		sizeInputField.addValidator(new LesserThanValidator(FontCacheModule.MAX_FONT_SIZE));

		fontPropertiesTable.add(new VisLabel("Size"));
		fontPropertiesTable.add(sizeInputField).width(40);
		fontPropertiesTable.add().expand().fill();
	}

	@Override
	protected String getFontExtension () {
		return "ttf";
	}

	@Override
	protected FileHandle getFontFolder () {
		return properties.getFileAccessModule().getTTFFontFolder();
	}

	@Override
	int getRelativeFontFolderLength () {
		return properties.getFileAccessModule().getTTFFontFolderRelative().length();
	}

	@Override
	public boolean isSupported (EditorEntity entity) {
		if (entity instanceof TextObject == false) return false;
		TextObject obj = (TextObject) entity;
		return obj.isTrueType();
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		Array<EditorEntity> entities = properties.getEntities();

		sizeInputField.setText(Utils.getEntitiesFieldFloatValue(entities, entity -> ((TextObject) entity).getFontSize()));
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();
		Array<EditorEntity> entities = properties.getEntities();
		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			obj.setFontSize(FieldUtils.getInt(sizeInputField, obj.getFontSize()));
		}
	}
}
