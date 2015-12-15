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

package com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.dialog.SelectFileDialog;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisText;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;
import com.kotcrab.vis.ui.util.value.VisWidgetValue;
import com.kotcrab.vis.ui.widget.*;

import static com.kotcrab.vis.editor.util.vis.EntityUtils.getCommonString;
import static com.kotcrab.vis.editor.util.vis.EntityUtils.setCommonCheckBoxState;

/**
 * @author Kotcrab
 */
public abstract class TextUITable extends SpecificUITable {
	private static final int MAX_FONT_LABEL_WIDTH = 100;

	protected FontCacheModule fontCache;
	protected FileAccessModule fileAccess;

	protected SelectFileDialog selectFontDialog;

	private IndeterminateCheckbox autoCenterOrigin;

	private VisValidatableTextField textField;

	private VisLabel fontLabel;
	private Tooltip fontLabelTooltip;

	protected VisImageButton selectFontButton;
	protected VisTable fontPropertiesTable;

	@Override
	protected void init () {
		textField = new VisValidatableTextField();
		textField.addListener(properties.getSharedChangeListener());
		textField.setProgrammaticChangeEvents(false);

		VisTable textTable = new VisTable(true);
		textTable.add(new VisLabel("Text"));
		textTable.add(textField).expandX().fillX();

		fontLabel = new VisLabel();
		fontLabel.setColor(Color.GRAY);
		fontLabel.setEllipsis(true);
		fontLabelTooltip = new Tooltip(fontLabel, "");
		selectFontButton = new VisImageButton(Icons.MORE.drawable());

		fontPropertiesTable = new VisTable(true);
		fontPropertiesTable.add(new VisLabel("Font"));
		fontPropertiesTable.add(fontLabel).width(new VisWidgetValue(context -> Math.min(context.getMinWidth(), MAX_FONT_LABEL_WIDTH)));
		fontPropertiesTable.add(selectFontButton);

		autoCenterOrigin = new IndeterminateCheckbox("Auto Set Origin to Center");
		autoCenterOrigin.addListener(properties.getSharedCheckBoxChangeListener());

		properties.getSceneModuleContainer().injectModules(this);

		selectFontButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				selectFontDialog.rebuildFileList();
				properties.beginSnapshot();
				getStage().addActor(selectFontDialog.fadeIn());
			}
		});

		selectFontDialog = new SelectFileDialog(getFontExtension(), fileAccess.getAssetsFolder(), file -> {
			for (EntityProxy proxy : properties.getSelectedEntities()) {
				Entity entity = proxy.getEntity();
				VisText text = entity.getComponent(VisText.class);
				AssetReference assetRef = entity.getComponent(AssetReference.class);
				VisAssetDescriptor asset = assetRef.asset;

				VisAssetDescriptor newAsset = null;

				if (asset instanceof BmpFontAsset) {
					BmpFontAsset fontAsset = (BmpFontAsset) asset;
					newAsset = new BmpFontAsset(fileAccess.relativizeToAssetsFolder(file), fontAsset.getFontParameter());
				} else if (asset instanceof TtfFontAsset) {
					TtfFontAsset fontAsset = (TtfFontAsset) asset;
					newAsset = new TtfFontAsset(fileAccess.relativizeToAssetsFolder(file), fontAsset.getFontSize());
				} else
					throw new UnsupportedAssetDescriptorException(asset);

				text.setFont(fontCache.getGeneric(newAsset, properties.getSceneModuleContainer().getScene().pixelsPerUnit));
				assetRef.asset = newAsset;
			}

			properties.getParentTab().dirty();
			properties.selectedEntitiesChanged();
			properties.endSnapshot();
		});

		defaults().left();
		add(autoCenterOrigin).row();
		add(textTable).expandX().fillX();
		row();
		add(fontPropertiesTable);
	}

	protected abstract String getFontExtension ();

	@Override
	public void updateUIValues () {
		ImmutableArray<EntityProxy> proxies = properties.getSelectedEntities();

		setCommonCheckBoxState(proxies, autoCenterOrigin, (Entity entity) -> entity.getComponent(VisText.class).isAutoSetOriginToCenter());

		textField.setText(getCommonString(proxies, "<multiple values>", (Entity entity) -> entity.getComponent(VisText.class).getText()));
		fontLabel.setText(getCommonString(proxies, "<?>", (Entity entity) -> ((PathAsset) entity.getComponent(AssetReference.class).asset).getPath()));
		((VisLabel) fontLabelTooltip.getContent()).setText(fontLabel.getText());
		fontLabelTooltip.pack();
	}

	@Override
	public final void setValuesToEntities () {
		EntityUtils.stream(properties.getSelectedEntities(), VisText.class, (entity, text) -> {
			if (textField.getText().equals("<multiple values>") == false) { //TODO: lets hope that nobody will use <multiple values> as their text
				text.setText(textField.getText());
			}
		});

		updateEntitiesValues();

		EntityUtils.stream(properties.getSelectedEntities(), VisText.class, (entity, text) -> {
			if (autoCenterOrigin.isIndeterminate() == false) {
				text.setAutoSetOriginToCenter(autoCenterOrigin.isChecked());
				properties.selectedEntitiesBasicValuesChanged();
			}
		});
	}

	protected abstract void updateEntitiesValues ();
}
