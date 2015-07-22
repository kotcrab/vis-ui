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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.dialog.SelectFontDialog;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.TextComponent;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

import static com.kotcrab.vis.editor.util.vis.EntityUtils.getCommonString;
import static com.kotcrab.vis.editor.util.vis.EntityUtils.setCommonCheckBoxState;

/**
 * @author Kotcrab
 */
public abstract class TextUITable extends SpecificUITable {
	@InjectModule protected FontCacheModule fontCache;
	@InjectModule protected FileAccessModule fileAccess;

	protected SelectFontDialog selectFontDialog;

	private IndeterminateCheckbox autoCenterOrigin;

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

		autoCenterOrigin = new IndeterminateCheckbox("Auto set origin to center");
		autoCenterOrigin.addListener(properties.getSharedCheckBoxChangeListener());

		properties.getSceneModuleContainer().injectModules(this);

		selectFontButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				selectFontDialog.rebuildFontList();
				properties.beginSnapshot();
				getStage().addActor(selectFontDialog.fadeIn());
			}
		});

		selectFontDialog = new SelectFontDialog(getFontExtension(), getFontFolder(), file -> {
			for (EntityProxy proxy : properties.getProxies()) {
				for (Entity entity : proxy.getEntities()) {
					TextComponent text = entity.getComponent(TextComponent.class);
					AssetComponent assetComponent = entity.getComponent(AssetComponent.class);
					VisAssetDescriptor asset = assetComponent.asset;

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

					assetComponent.asset = newAsset;
				}
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

	protected abstract FileHandle getFontFolder ();

	abstract int getRelativeFontFolderLength ();

	private String getFontTextForEntity (PathAsset asset) {
		return asset.getPath().substring(getRelativeFontFolderLength() + 1);
	}

	@Override
	public void updateUIValues () {
		Array<EntityProxy> proxies = properties.getProxies();

		setCommonCheckBoxState(proxies, autoCenterOrigin, (Entity entity) -> entity.getComponent(TextComponent.class).isAutoSetOriginToCenter());

		textField.setText(getCommonString(proxies, "<multiple values>", (Entity entity) -> entity.getComponent(TextComponent.class).getText()));
		fontLabel.setText(getCommonString(proxies, "<?>", (Entity entity) -> getFontTextForEntity((PathAsset) entity.getComponent(AssetComponent.class).asset)));
	}

	@Override
	public void setValuesToEntities () {
		for (EntityProxy proxy : properties.getProxies()) {
			for (Entity entity : proxy.getEntities()) {

				TextComponent text = entity.getComponent(TextComponent.class);
				if (textField.getText().equals("<multiple values>") == false) { //TODO: lets hope that nobody will use <multiple values> as their text
					text.setText(textField.getText());
				}

				if (autoCenterOrigin.isIndeterminate() == false)
					text.setAutoSetOriginToCenter(autoCenterOrigin.isChecked());
			}
		}
	}
}
