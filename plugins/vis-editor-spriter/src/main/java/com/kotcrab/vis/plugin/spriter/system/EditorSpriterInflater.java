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

package com.kotcrab.vis.plugin.spriter.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.scene.AssetsLoadingMonitorModule;
import com.kotcrab.vis.plugin.spriter.component.SpriterProperties;
import com.kotcrab.vis.plugin.spriter.module.SpriterCacheModule;
import com.kotcrab.vis.plugin.spriter.runtime.assets.SpriterAsset;
import com.kotcrab.vis.plugin.spriter.runtime.component.ProtoVisSpriter;
import com.kotcrab.vis.plugin.spriter.runtime.component.VisSpriter;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorSpriterInflater extends InflaterSystem {
	private SpriterCacheModule cache;
	private AssetsLoadingMonitorModule loadingMonitor;

	private ComponentMapper<ProtoVisSpriter> protoCm;
	private ComponentMapper<SpriterProperties> propertiesCm;
	private ComponentMapper<AssetReference> assetCm;

	public EditorSpriterInflater () {
		super(Aspect.all(ProtoVisSpriter.class, AssetReference.class));
	}

	@Override
	public void inserted (int entityId) {
		AssetReference assetRef = assetCm.get(entityId);
		ProtoVisSpriter protoComponent = protoCm.get(entityId);
		SpriterProperties propsComponent = propertiesCm.get(entityId);

		SpriterAsset asset = (SpriterAsset) assetRef.asset;

		try {
			VisSpriter component = cache.createComponent(asset, protoComponent.scale);

			protoComponent.fill(component);
			world.getEntity(entityId).edit().add(component);

			if (propsComponent.previewInEditor == false) component.setAnimationPlaying(false);
		} catch (GdxRuntimeException e) {
			Log.exception(e);
//			world.getEntity(entityId).edit().add(new VisSpriter(null, null, 1));
			loadingMonitor.addFailedResource(assetRef.asset, e);
		}

		protoCm.remove(entityId);
	}
}
