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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components;

import com.artemis.Entity;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.IndeterminateTextField;
import com.kotcrab.vis.editor.ui.scene.entityproperties.BasicEntityPropertiesTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.NumberInputField;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;
import com.kotcrab.vis.editor.util.scene2d.FieldUtils;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisText;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.lang.reflect.Field;

/** @author Kotcrab */
public class TextPropertiesComponentTable extends AutoComponentTable<VisText> {
	private FontCacheModule fontCache;

	private IndeterminateCheckbox autoSetOriginToCenter;
	private IndeterminateCheckbox distanceFieldShaderEnabled;

	private NumberInputField sizeInputField;
	private VisTable sizeTable;

	public TextPropertiesComponentTable (ModuleInjector injector) {
		super(injector, VisText.class, false);
	}

	@Override
	protected void init () {
		super.init();
		autoSetOriginToCenter = getUIByFieldId("autoSetOriginToCenter", IndeterminateCheckbox.class);
		distanceFieldShaderEnabled = getUIByFieldId("distanceFieldShaderEnabled", IndeterminateCheckbox.class);
		IndeterminateTextField textField = getUIByFieldId("text", IndeterminateTextField.class);

		autoSetOriginToCenter.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (autoSetOriginToCenter.isIndeterminate() == false && autoSetOriginToCenter.isChecked()) {
					properties.requestUIValuesUpdate();
				}
				properties.revalidateFieldLocks();
			}
		});

		distanceFieldShaderEnabled.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				EntityUtils.stream(properties.getSelectedEntities(), AssetReference.class, (proxy, assetReference) -> {
					VisAssetDescriptor desc = assetReference.asset;
					if (desc instanceof BmpFontAsset) {
						assetReference.asset = getNewBmpAsset((BmpFontAsset) desc, distanceFieldShaderEnabled.isChecked());
					}
				});
			}

			private VisAssetDescriptor getNewBmpAsset (BmpFontAsset original, boolean useDistanceFieldFilters) {
				BitmapFontLoader.BitmapFontParameter data = new BitmapFontLoader.BitmapFontParameter();

				if (useDistanceFieldFilters) {
					data.genMipMaps = true;
					data.minFilter = Texture.TextureFilter.MipMapLinearLinear;
					data.magFilter = Texture.TextureFilter.Linear;
				}

				return new BmpFontAsset(original.getPath(), data);
			}
		});

		try {
			Field writeEnters = VisTextField.class.getDeclaredField("writeEnters");
			writeEnters.setAccessible(true);
			writeEnters.set(textField.getTextField(), true);
		} catch (ReflectiveOperationException e) {
			Log.exception(e);
		}
		textField.getTextField().addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				EntityUtils.stream(properties.getSelectedEntities(), VisText.class, (proxy, text) -> {
					text.setText(text.getText()); //force dirty state
				});
			}
		});

		sizeInputField = properties.createNewNumberField();
		sizeInputField.addValidator(Validators.INTEGERS);
		sizeInputField.addValidator(new Validators.GreaterThanValidator(FontCacheModule.MIN_FONT_SIZE));
		sizeInputField.addValidator(new Validators.LesserThanValidator(FontCacheModule.MAX_FONT_SIZE));

		sizeTable = new VisTable(true);
		sizeTable.add(new VisLabel("Size"));
		sizeTable.add(sizeInputField).width(40);
		add(sizeTable).height(PrefHeightIfVisibleValue.INSTANCE);
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		sizeTable.setVisible(true);
		EntityUtils.stream(properties.getSelectedEntities(), AssetReference.class, (proxy, assetReference) -> {
			VisAssetDescriptor asset = assetReference.getAsset();

			if (asset instanceof BmpFontAsset) {
				sizeTable.setVisible(false);
			}
		});

		if (sizeTable.isVisible()) {
			sizeInputField.setText(EntityUtils.getCommonIntegerValue(properties.getSelectedEntities(),
					(Entity entity) -> ((TtfFontAsset) entity.getComponent(AssetReference.class).asset).getFontSize()));
		}
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();

		if (sizeTable.isVisible()) {
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

	@Override
	public void lockFields (VisText component) {
		if (component.isAutoSetOriginToCenter()) {
			properties.lockField(BasicEntityPropertiesTable.LockableField.ORIGIN);
		}
	}
}
