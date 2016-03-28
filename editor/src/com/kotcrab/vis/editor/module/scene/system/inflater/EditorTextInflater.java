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

package com.kotcrab.vis.editor.module.scene.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.scene.AssetsLoadingMonitorModule;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisText;
import com.kotcrab.vis.runtime.component.proto.ProtoVisText;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorTextInflater extends InflaterSystem {
	private FontCacheModule fontCache;
	private AssetsLoadingMonitorModule loadingMonitor;

	private ComponentMapper<AssetReference> assetCm;
	private ComponentMapper<VisText> textCm;
	private ComponentMapper<ProtoVisText> protoCm;

	private float pixelsPerUnit;

	public EditorTextInflater (float pixelsPerUnit) {
		super(Aspect.all(ProtoVisText.class, AssetReference.class));
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	public void inserted (int entityId) {
		VisAssetDescriptor asset = assetCm.get(entityId).asset;
		ProtoVisText protoComponent = protoCm.get(entityId);

		BitmapFont font;
		try {
			font = fontCache.getGeneric(asset, pixelsPerUnit);
		} catch (GdxRuntimeException e) {
			Log.exception(e);
			font = new BitmapFont();
			loadingMonitor.addFailedResource(asset, e);
		}

		VisText text = textCm.create(entityId);
		text.init(font, protoComponent.text);

		protoComponent.fill(text);

		protoCm.remove(entityId);
	}
}
