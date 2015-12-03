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

package com.kotcrab.vis.runtime.util.json;

import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.scene.LayerCordsSystem;
import com.kotcrab.vis.runtime.scene.SceneViewport;

/** @author Kotcrab */
public class RuntimeJsonTags {
	public static void registerTags (JsonTagRegistrar registrar) {
		registrar.register("String", String.class);

		registrar.register("SceneData", SceneData.class);
		registrar.register("SceneViewport", SceneViewport.class);
		registrar.register("LayerCordsSystem", LayerCordsSystem.class);

		registrar.register("PathAsset", PathAsset.class);
		registrar.register("BmpFontAsset", BmpFontAsset.class);
		registrar.register("TtfFontAsset", TtfFontAsset.class);
		registrar.register("AtlasRegionAsset", AtlasRegionAsset.class);
		registrar.register("TextureRegionAsset", TextureRegionAsset.class);
		registrar.register("ShaderAsset", ShaderAsset.class);
		registrar.register("SpriterAsset", SpriterAsset.class);

		registrar.register("Transform", Transform.class);
		registrar.register("Position", Position.class);
		registrar.register("Size", Size.class);
		registrar.register("Tint", Tint.class);
		registrar.register("Origin", Origin.class);

		registrar.register("VisSprite", VisSprite.class);

		registrar.register("AssetComponent", AssetComponent.class);
		registrar.register("GroupComponent", GroupComponent.class);
		registrar.register("IDComponent", IDComponent.class);
		registrar.register("InvisibleComponent", InvisibleComponent.class);
		registrar.register("LayerComponent", LayerComponent.class);
		registrar.register("RenderableComponent", RenderableComponent.class);
		registrar.register("VariablesComponent", VariablesComponent.class);
		registrar.register("PhysicsPropertiesComponent", PhysicsPropertiesComponent.class);
		registrar.register("PolygonComponent", PolygonComponent.class);

		registrar.register("SimpleProtoComponent", SimpleProtoComponent.class);
		registrar.register("MusicProtoComponent", MusicProtoComponent.class);
		registrar.register("SoundProtoComponent", SoundProtoComponent.class);
		registrar.register("ParticleProtoComponent", ParticleProtoComponent.class);
		registrar.register("TextProtoComponent", TextProtoComponent.class);
		registrar.register("ShaderProtoComponent", ShaderProtoComponent.class);
		registrar.register("SpriterProtoComponent", SpriterProtoComponent.class);
	}
}
