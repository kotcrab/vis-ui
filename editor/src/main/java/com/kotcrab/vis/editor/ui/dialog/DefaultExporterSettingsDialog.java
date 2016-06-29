/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.extension.DefaultExporter;
import com.kotcrab.vis.editor.extension.DefaultExporterSettings;
import com.kotcrab.vis.editor.module.editor.EditorSettingsIOModule;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.vis.DefaultEnumNameProvider;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

/** @author Kotcrab */
public class DefaultExporterSettingsDialog extends VisWindow {
	private EditorSettingsIOModule settingsIO;
	private DefaultExporterSettings settings;

	private final VisCheckBox skipDefaultCheck;
	private final VisCheckBox minimalOutputCheck;
	private final VisCheckBox packageSeparateAtlasForEachSceneCheck;
	private final EnumSelectBox<Texture.TextureFilter> migFilterSelectBox;
	private final EnumSelectBox<Texture.TextureFilter> magFilterSelectBox;

	public DefaultExporterSettingsDialog (EditorSettingsIOModule settingsIO, DefaultExporterSettings settings) {
		super("Settings");
		this.settingsIO = settingsIO;
		this.settings = settings;

		TableUtils.setSpacingDefaults(this);
		setModal(true);
		closeOnEscape();
		addCloseButton();

		left();
		defaults().left();

		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton okButton = new VisTextButton("OK");
		VisTable buttonTable = new VisTable(true);
		buttonTable.add(cancelButton);
		buttonTable.add(okButton);

		skipDefaultCheck = new VisCheckBox("Skip default values");

		VisImage skipDefaultHelpImage = new VisImage(Icons.QUESTION_BIG.drawable());
		new Tooltip.Builder("Reduces output file size by skipping default values like '0' or 'null'.\n" +
				"Typically there is no need to disable it but you can do it if you want to inspect\n" +
				"output scene file.", Align.left).target(skipDefaultHelpImage).build();
		add(skipDefaultCheck);
		add(skipDefaultHelpImage).size(22).row();

		minimalOutputCheck = new VisCheckBox("Use minimal output type");
		VisImage minimalOutputHelpImage = new VisImage(Icons.QUESTION_BIG.drawable());
		new Tooltip.Builder("If checked output JSON will use minimal format, unnecessary double quotes\n" +
				"will be skipped unless needed. This format may not be supported by all JSON parsers.\nUncheck" +
				"this to disable minimal format.", Align.left).target(minimalOutputHelpImage).build();
		add(minimalOutputCheck);
		add(minimalOutputHelpImage).size(22).row();

		packageSeparateAtlasForEachSceneCheck = new VisCheckBox("Package separate atlas for each scene");
		VisImage packageSeparateAtlasForEachSceneHelpImage = new VisImage(Icons.QUESTION_BIG.drawable());
		new Tooltip.Builder("If checked each exported scene will have it's own separate texture atlas. This is useful\n" +
				"when you have many texture assets but they are split between scenes.\nNote when this is checked 'master'" +
				"atlas containing all texture will not be created.", Align.left).target(packageSeparateAtlasForEachSceneHelpImage).build();
		add(packageSeparateAtlasForEachSceneCheck);
		add(packageSeparateAtlasForEachSceneHelpImage).size(22).row();

		migFilterSelectBox = new EnumSelectBox<>(Texture.TextureFilter.class, new DefaultEnumNameProvider<>());
		magFilterSelectBox = new EnumSelectBox<>(Texture.TextureFilter.class, new DefaultEnumNameProvider<>());
		add(TableBuilder.build(new VisLabel("Mig Texture Filter"), migFilterSelectBox)).row();
		add(TableBuilder.build(new VisLabel("Mag Texture Filter"), magFilterSelectBox)).row();

		add(buttonTable).right().colspan(2);

		cancelButton.addListener(new VisChangeListener((event1, actor1) -> {
			setUIFromSettings();
			fadeOut();
		}));

		okButton.addListener(new VisChangeListener((event, actor) -> {
			setToSettings();
			fadeOut();
		}));

		setUIFromSettings();

		pack();
		centerWindow();
	}

	private void setUIFromSettings () {
		skipDefaultCheck.setChecked(settings.skipDefaultValues);
		minimalOutputCheck.setChecked(settings.useMinimalOutputType);
		packageSeparateAtlasForEachSceneCheck.setChecked(settings.packageSeparateAtlasForEachScene);
		migFilterSelectBox.setSelectedEnum(settings.migTextureFilter);
		magFilterSelectBox.setSelectedEnum(settings.magTextureFilter);
	}

	private void setToSettings () {
		settings.skipDefaultValues = skipDefaultCheck.isChecked();
		settings.useMinimalOutputType = minimalOutputCheck.isChecked();
		settings.packageSeparateAtlasForEachScene = packageSeparateAtlasForEachSceneCheck.isChecked();
		settings.migTextureFilter = migFilterSelectBox.getSelectedEnum();
		settings.magTextureFilter = magFilterSelectBox.getSelectedEnum();
		settingsIO.save(settings, DefaultExporter.SETTINGS_FILE_NAME);
	}
}
