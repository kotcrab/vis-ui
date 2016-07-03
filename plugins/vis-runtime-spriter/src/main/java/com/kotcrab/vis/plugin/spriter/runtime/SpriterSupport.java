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

package com.kotcrab.vis.plugin.spriter.runtime;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.plugin.spriter.runtime.assets.SpriterAsset;
import com.kotcrab.vis.plugin.spriter.runtime.loader.SpriterData;
import com.kotcrab.vis.plugin.spriter.runtime.loader.SpriterDataLoader;
import com.kotcrab.vis.plugin.spriter.runtime.system.SpriterInflater;
import com.kotcrab.vis.plugin.spriter.runtime.system.SpriterRenderSystem;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.plugin.VisPlugin;
import com.kotcrab.vis.runtime.scene.SceneConfig;
import com.kotcrab.vis.runtime.scene.SceneConfig.Priority;
import com.kotcrab.vis.runtime.scene.SystemProvider;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

@VisPlugin
public class SpriterSupport implements EntitySupport {
	@Override
	public void setLoaders (AssetManager manager) {
		manager.setLoader(SpriterData.class, new SpriterDataLoader(manager.getFileHandleResolver()));
	}

	@Override
	public void resolveDependencies (Array<AssetDescriptor> dependencies, EntityData entityData, Component component) {
		if (component instanceof AssetReference) {
			VisAssetDescriptor asset = ((AssetReference) component).asset;
			if (asset instanceof SpriterAsset) {
				SpriterAsset spriterAsset = (SpriterAsset) asset;
				dependencies.add(new AssetDescriptor<SpriterData>(spriterAsset.getPath(), SpriterData.class));
			}
		}
	}

	@Override
	public void registerSceneSystems (SceneConfig config) {
		config.addSystem(new SystemProvider() {
			@Override
			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
				return new SpriterInflater(context.assetsManager);
			}
		}, Priority.VIS_INFLATER);

		config.addSystem(new SystemProvider() {
			@Override
			public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
				return new SpriterRenderSystem(config.getSystem(RenderBatchingSystem.class));
			}
		}, Priority.VIS_RENDERER);
	}
}
