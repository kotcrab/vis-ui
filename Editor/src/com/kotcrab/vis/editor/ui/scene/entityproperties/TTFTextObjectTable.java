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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
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
	public boolean isSupported (EditorObject entity) {
		if (entity instanceof TextObject == false) return false;
		TextObject obj = (TextObject) entity;
		return obj.isTrueType();
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		Array<EditorObject> entities = properties.getEntities();

		sizeInputField.setText(Utils.getEntitiesFieldFloatValue(entities, entity -> ((TextObject) entity).getFontSize()));
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			TextObject obj = (TextObject) entity;

			obj.setFontSize(FieldUtils.getInt(sizeInputField, obj.getFontSize()));
		}
	}
}
