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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.dialog.SelectFontDialog;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

import static com.kotcrab.vis.editor.util.EntityUtils.getCommonString;

abstract class TextObjectTable extends SpecificObjectTable {
	protected SelectFontDialog selectFontDialog;

	private VisValidableTextField textField;

	private VisLabel fontLabel;
	protected VisImageButton selectFontButton;

	protected VisTable fontPropertiesTable;

	@Override
	protected void init () {
		textField = new VisValidableTextField();
		textField.addListener(properties.getSharedChangeListener());
		textField.setProgrammaticChangeEvents(false);

		VisTable textTable = new VisTable(true);
		textTable.add(new VisLabel("Text"));
		textTable.add(textField).expandX().fillX();

		fontLabel = new VisLabel();
		fontLabel.setColor(Color.GRAY);
		fontLabel.setEllipsis(true);
		selectFontButton = new VisImageButton(Assets.getIcon(Icons.MORE));

		fontPropertiesTable = new VisTable(true);
		fontPropertiesTable.add(new VisLabel("Font"));
		fontPropertiesTable.add(fontLabel).width(100);
		fontPropertiesTable.add(selectFontButton);

		selectFontButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				selectFontDialog.rebuildFontList();
				properties.beginSnapshot();
				getStage().addActor(selectFontDialog.fadeIn());
			}
		});

		selectFontDialog = new SelectFontDialog(getFontExtension(), getFontFolder(), file -> {
			for (EditorObject entity : properties.getEntities()) {
				TextObject obj = (TextObject) entity;
				obj.setFont(properties.getFontCacheModule().get(file));
			}

			properties.getParentTab().dirty();
			properties.updateValues();
			properties.endSnapshot();
		});

		defaults().left();
		add(textTable).expandX().fillX();
		row();
		add(fontPropertiesTable);
	}

	protected abstract String getFontExtension ();

	protected abstract FileHandle getFontFolder ();

	abstract int getRelativeFontFolderLength ();

	private String getFontTextForEntity (EditorObject entity) {
		TextObject obj = (TextObject) entity;
		return obj.getAssetPath().substring(getRelativeFontFolderLength() + 1);
	}

	@Override
	public void updateUIValues () {
		Array<EditorObject> entities = properties.getEntities();

		textField.setText(getCommonString(entities, "<multiple values>", entity -> ((TextObject) entity).getText()));
		fontLabel.setText(getCommonString(entities, "<?>", this::getFontTextForEntity));
	}

	@Override
	public void setValuesToEntities () {
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			TextObject obj = (TextObject) entity;

			obj.setText(textField.getText());
		}
	}
}
