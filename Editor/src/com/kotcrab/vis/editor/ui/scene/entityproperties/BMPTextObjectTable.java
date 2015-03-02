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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

 class BMPTextObjectTable extends SpecificObjectTable {
	private FileAccessModule fileAccessModule;

	private VisValidableTextField textField;
	private VisLabel fontLabel;
	private VisImageButton selectFontButton;

	public BMPTextObjectTable (FileAccessModule fileAccessModule, ChangeListener sharedChangeListener) {
		super(true);
		this.fileAccessModule = fileAccessModule;

		fontLabel = new VisLabel();
		fontLabel.setColor(Color.GRAY);
		fontLabel.setEllipsis(true);
		selectFontButton = new VisImageButton(Assets.getIcon(Icons.MORE));
		textField = new VisValidableTextField();
		textField.addListener(sharedChangeListener);
		textField.setProgrammaticChangeEvents(false);

		VisTable fontTable = new VisTable(true);
		fontTable.add(new VisLabel("Font"));
		fontTable.add(fontLabel).width(100);
		fontTable.add(selectFontButton);
		fontTable.add().expand().fill();

		VisTable textTable = new VisTable(true);
		textTable.add(new VisLabel("Text"));
		textTable.add(textField).expandX().fillX();

		defaults().left().expandX().fillX();
		add(textTable);
		row();
		add(fontTable);
	}

	private String getTextFieldText (Array<EditorEntity> entities) {
		TextObject textObj = (TextObject) entities.get(0);
		String firstText = textObj.getText();

		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			if (obj.getText().equals(firstText) == false) return "<multiple values>";
		}

		return firstText;
	}

	private String getFontLabelText (Array<EditorEntity> entities) {
		String firstText = getFontTextForEntity(entities.get(0));

		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			if (getFontTextForEntity(obj).equals(firstText) == false) return "<?>";
		}

		return firstText;
	}

	private String getFontTextForEntity (EditorEntity entity) {
		TextObject obj = (TextObject) entity;
		return obj.getRelativeFontPath().substring(fileAccessModule.getBMPFontFolderRelative().length() + 1);
	}

	@Override
	public boolean isSupported (EditorEntity entity) {
		if (entity instanceof TextObject == false) return false;
		TextObject obj = (TextObject) entity;
		return !obj.isUsesTTF();
	}

	@Override
	public void updateUIValues (Array<EditorEntity> entities) {
		textField.setText(getTextFieldText(entities));
		fontLabel.setText(getFontLabelText(entities));
	}

	@Override
	public void setValuesToEntities (Array<EditorEntity> entities) {
		for (EditorEntity entity : entities) {
			TextObject obj = (TextObject) entity;

			obj.setText(textField.getText());
		}
	}
}
