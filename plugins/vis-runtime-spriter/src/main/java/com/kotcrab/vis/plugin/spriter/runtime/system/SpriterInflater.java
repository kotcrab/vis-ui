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

package com.kotcrab.vis.plugin.spriter.runtime.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.assets.AssetManager;
import com.kotcrab.vis.plugin.spriter.runtime.assets.SpriterAsset;
import com.kotcrab.vis.plugin.spriter.runtime.component.ProtoVisSpriter;
import com.kotcrab.vis.plugin.spriter.runtime.component.VisSpriter;
import com.kotcrab.vis.plugin.spriter.runtime.loader.SpriterData;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class SpriterInflater extends InflaterSystem {
	private ComponentMapper<ProtoVisSpriter> protoCm;
	private ComponentMapper<AssetReference> assetCm;

	private AssetManager manager;

	public SpriterInflater (AssetManager manager) {
		super(Aspect.all(ProtoVisSpriter.class, AssetReference.class));
		this.manager = manager;
	}

	@Override
	public void inserted (int entityId) {
		AssetReference assetRef = assetCm.get(entityId);
		ProtoVisSpriter protoComponent = protoCm.get(entityId);

		SpriterAsset asset = (SpriterAsset) assetRef.asset;
		SpriterData data = manager.get(asset.getPath(), SpriterData.class);
		if (data == null)
			throw new IllegalStateException("Can't load scene, spriter data is missing: " + asset.getPath());
		VisSpriter component = new VisSpriter(data.loader, data.data, protoComponent.scale);
		protoComponent.fill(component);
		world.getEntity(entityId).edit().add(component);

		protoCm.remove(entityId);
	}
}
