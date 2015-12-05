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
import com.kotcrab.vis.runtime.component.proto.*;
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
		registrar.register("Tint", Tint.class);
		registrar.register("Origin", Origin.class);

		registrar.register("VisSprite", VisSprite.class);

		registrar.register("AssetComponent", AssetReference.class);
		registrar.register("GroupComponent", VisGroup.class);
		registrar.register("IDComponent", VisID.class);
		registrar.register("InvisibleComponent", Invisible.class);
		registrar.register("LayerComponent", Layer.class);
		registrar.register("RenderableComponent", Renderable.class);
		registrar.register("VariablesComponent", Variables.class);
		registrar.register("PhysicsPropertiesComponent", PhysicsProperties.class);
		registrar.register("PolygonComponent", Polygon.class);

		registrar.register("ProtoVisSprite", ProtoVisSprite.class);
		registrar.register("MusicProtoComponent", ProtoVisMusic.class);
		registrar.register("SoundProtoComponent", ProtoVisSound.class);
		registrar.register("ParticleProtoComponent", ProtoVisParticle.class);
		registrar.register("TextProtoComponent", ProtoVisText.class);
		registrar.register("ShaderProtoComponent", ProtoShader.class);
		registrar.register("SpriterProtoComponent", ProtoVisSpriter.class);
	}
}
