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
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.SelectFontDialog;
import com.kotcrab.vis.editor.ui.SelectFontDialog.FontDialogListener;
import com.kotcrab.vis.editor.ui.tab.Tab;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

class TTFTextObjectTable extends SpecificObjectTable {
	private Array<EditorEntity> entities;
	private FileAccessModule fileAccessModule;
		
		private SelectFontDialog selectFontDialog;

		private VisValidableTextField textField;
		private VisLabel fontLabel;
		private VisImageButton selectFontButton;
		private NumberInputField sizeInputField;

		public TTFTextObjectTable (final Array<EditorEntity> entities, FileAccessModule fileAccessModule, final FontCacheModule fontCacheModule, final Tab parentTab, final EntityProperties properties, ChangeListener sharedChangeListener) {
			super(true);
			this.entities = entities;
			this.fileAccessModule = fileAccessModule;

			fontLabel = new VisLabel();
			fontLabel.setColor(Color.GRAY);
			fontLabel.setEllipsis(true);
			selectFontButton = new VisImageButton(Assets.getIcon(Icons.MORE));
			sizeInputField = new NumberInputField(sharedChangeListener);
			sizeInputField.addValidator(Validators.INTEGERS);
			sizeInputField.addValidator(new GreaterThanValidator(FontCacheModule.MIN_FONT_SIZE));
			sizeInputField.addValidator(new LesserThanValidator(FontCacheModule.MAX_FONT_SIZE));
			textField = new VisValidableTextField();
			textField.addListener(sharedChangeListener);
			textField.setProgrammaticChangeEvents(false);

			VisTable fontTable = new VisTable(true);
			fontTable.add(new VisLabel("Font"));
			fontTable.add(fontLabel).width(100);
			fontTable.add(selectFontButton);
			fontTable.add(new VisLabel("Size"));
			fontTable.add(sizeInputField).width(40);
			fontTable.add().expand().fill();

			VisTable textTable = new VisTable(true);
			textTable.add(new VisLabel("Text"));
			textTable.add(textField).expandX().fillX();

			defaults().left().expandX().fillX();
			add(textTable);
			row();
			add(fontTable);

			selectFontButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					selectFontDialog.rebuild();
					getStage().addActor(selectFontDialog.fadeIn());
				}
			});

			selectFontDialog = new SelectFontDialog(fileAccessModule, new FontDialogListener() {
				@Override
				public void selected (FileHandle file) {
					for (EditorEntity entity : entities) {
						TextObject obj = (TextObject) entity;
						obj.setFont(fontCacheModule.get(file));
					}

					parentTab.dirty();
					properties.updateValues();
				}
			});
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
			return obj.getRelativeFontPath().substring(fileAccessModule.getTTFFontFolderRelative().length() + 1);
		}

		@Override
		public boolean isSupported (EditorEntity entity) {
			if(entity instanceof TextObject == false) return false;
			TextObject obj = (TextObject) entity;
			return obj.isUsesTTF();
		}

		@Override
		public void updateUIValues (Array<EditorEntity> entities) {
			textField.setText(getTextFieldText(entities));
			fontLabel.setText(getFontLabelText(entities));
			sizeInputField.setText(Utils.getEntitiesFieldValue(entities, new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return ((TextObject) entity).getFontSize();
				}
			}));
		}

		@Override
		public void setValuesToEntities (Array<EditorEntity> entities) {
			for (EditorEntity entity : entities) {
				TextObject obj = (TextObject) entity;

				obj.setText(textField.getText());
				obj.setFontSize(FieldUtils.getInt(sizeInputField, obj.getFontSize()));
			}
		}
	}
