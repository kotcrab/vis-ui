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
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.util.EntityUtils;
import com.kotcrab.vis.ui.widget.Tooltip;

class BMPTextObjectTable extends TextObjectTable {
	private IndeterminateCheckbox distanceFieldCheck;

	public BMPTextObjectTable (final EntityProperties properties) {
		super(properties);

		distanceFieldCheck = new IndeterminateCheckbox("Use DF");
		distanceFieldCheck.addListener(properties.getSharedCheckBoxChangeListener());

		fontPropertiesTable.add(distanceFieldCheck);

		new Tooltip(distanceFieldCheck, "Use distance field shader for this text");
	}

	@Override
	protected String getFontExtension () {
		return "fnt";
	}

	@Override
	protected FileHandle getFontFolder () {
		return properties.getFileAccessModule().getBMPFontFolder();
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		if (!(entity instanceof TextObject)) return false;
		TextObject obj = (TextObject) entity;
		return !obj.isTrueType();
	}

	@Override
	int getRelativeFontFolderLength () {
		return properties.getFileAccessModule().getBMPFontFolderRelative().length();
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		EntityUtils.setCommonCheckBoxState(properties.getEntities(), distanceFieldCheck, entity -> ((TextObject) entity).isDistanceFieldShaderEnabled());
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();

		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			TextObject obj = (TextObject) entity;

			if (distanceFieldCheck.isIndeterminate() == false)
				obj.setDistanceFieldShaderEnabled(distanceFieldCheck.isChecked());
		}
	}
}
