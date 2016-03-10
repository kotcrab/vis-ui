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

package com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable;

import com.artemis.Entity;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.util.scene2d.FieldUtils;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisText;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.VisLabel;

/**
 * @author Kotcrab
 */
public class TtfTextUITable extends TextUITable {
	private NumberInputField sizeInputField;

	@Override
	protected void init () {
		super.init();
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
	public boolean isSupported (EntityProxy proxy) {
		if (proxy.hasComponent(VisText.class) == false) return false;

		VisAssetDescriptor asset = proxy.getComponent(AssetReference.class).asset;
		if (asset instanceof TtfFontAsset == false)
			return false;

		return true;
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		sizeInputField.setText(EntityUtils.getEntitiesCommonFloatValue(properties.getSelectedEntities(),
				(Entity entity) -> ((TtfFontAsset) entity.getComponent(AssetReference.class).asset).getFontSize()));
	}

	@Override
	protected void updateEntitiesValues () {
		for (EntityProxy proxy : properties.getSelectedEntities()) {
			AssetReference assetRef = proxy.getComponent(AssetReference.class);
			VisText text = proxy.getComponent(VisText.class);

			TtfFontAsset ttfAsset = (TtfFontAsset) assetRef.asset;
			int fontSize = FieldUtils.getInt(sizeInputField, ttfAsset.getFontSize());

			if (ttfAsset.getFontSize() != fontSize) {
				TtfFontAsset newAsset = new TtfFontAsset(ttfAsset.getPath(), fontSize);
				assetRef.asset = newAsset;
				text.setFont(fontCache.get(newAsset, properties.getSceneModuleContainer().getScene().pixelsPerUnit));
			}
		}
	}
}
