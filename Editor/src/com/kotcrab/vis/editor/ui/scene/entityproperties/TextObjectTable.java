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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.dialog.SelectFontDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.getCommonString;

abstract class TextObjectTable extends SpecificObjectTable {
	protected SelectFontDialog selectFontDialog;

	private VisValidableTextField textField;

	private VisLabel fontLabel;
	protected VisImageButton selectFontButton;

	protected VisTable fontPropertiesTable;

	public TextObjectTable (final EntityProperties properties) {
		super(properties, true);

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
			for (EditorEntity entity : properties.getEntities()) {
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

	private String getFontTextForEntity (EditorEntity entity) {
		TextObject obj = (TextObject) entity;
		return obj.getRelativeFontPath().substring(getRelativeFontFolderLength() + 1);
	}

	@Override
	public void updateUIValues () {
		Array<EditorEntity> entities = properties.getEntities();

		textField.setText(getCommonString(entities, "<multiple values>", entity -> ((TextObject) entity).getText()));
		fontLabel.setText(getCommonString(entities, "<?>", this::getFontTextForEntity));
	}

	@Override
	public void setValuesToEntities () {
		Array<EditorEntity> entities = properties.getEntities();
		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			obj.setText(textField.getText());
		}
	}
}
