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

package com.kotcrab.vis.editor.module.project.support.vc8;

import com.artemis.utils.Bag;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.entity.*;
import com.kotcrab.vis.editor.module.project.SceneCacheModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.scene.PhysicsSettings;
import com.kotcrab.vis.editor.serializer.*;
import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.scene.LayerCordsSystem;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.runtime.util.annotation.DeprecatedOn;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.UUID;

/**
 * Allows to load VisEditor scenes. This API should not be used directly. See {@link SceneCacheModule}
 * @author Kotcrab
 * @see SceneCacheModule
 */
@SuppressWarnings("rawtypes")
@Deprecated @DeprecatedOn(versionCode = 9)
public class SceneIOModuleVC8 extends SceneIOModule {
	@Override
	protected void setupKryo () {
		kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.setRegistrationRequired(true);

		//id configuration: (categories aren't strictly enforced but should be used)
		//0-8 kryo primitives
		//10-200 custom base types
		//	10-30 libs classes
		//	31-60 vis classes
		//	61-100 assets descriptors
		//	101-200 reserved for future use
		//201-400 components
		//401-800 plugins

		kryo.register(Array.class, new ArraySerializer(), 10);
		kryo.register(IntArray.class, new IntArraySerializer(), 11);
		kryo.register(Bag.class, new BagSerializer(), 12);
		kryo.register(Rectangle.class, 13);
		kryo.register(Matrix4.class, 14);
		kryo.register(Color.class, new ColorSerializer(), 15);
		kryo.register(Class.class, 16);
		kryo.register(float[].class, 17);
		kryo.register(UUID.class, new UUIDSerializer(), 18);
		kryo.register(IntMap.class, new IntMapSerializer(), 19);
		kryo.register(Vector2.class, 20);
		kryo.register(Vector2[].class, 21);
		kryo.register(Vector2[][].class, 22);
		kryo.register(BodyType.class, 23);
		kryo.register(ObjectMap.class, new ObjectMapSerializer(), 24);

		kryo.register(EditorScene.class, new EditorSceneSerializer(kryo), 31);
		kryo.register(EntityScheme.class, new EntitySchemeSerializerVC8(kryo, this), 32);
		kryo.register(SceneViewport.class, 33);
		kryo.register(Layer.class, 34);
		kryo.register(BitmapFontParameter.class, 35);
		kryo.register(TextureFilter.class, 36);
		kryo.register(LayerCordsSystem.class, 37);
		kryo.register(PhysicsSettings.class, 38);

		kryo.register(PathAsset.class, 61);
		kryo.register(TextureRegionAsset.class, 62);
		kryo.register(AtlasRegionAsset.class, 63);
		kryo.register(BmpFontAsset.class, 64);
		kryo.register(TtfFontAsset.class, 65);
		kryo.register(ShaderAsset.class, 66);

		registerEntityComponentSerializer(SpriteComponent.class, new SpriteComponentSerializer(kryo, textureCache), 201);
		registerEntityComponentSerializer(MusicComponent.class, new MusicComponentSerializer(kryo), 202);
		kryo.register(SoundComponent.class, 203);
		registerEntityComponentSerializer(ParticleComponent.class, new ParticleComponentSerializer(kryo, particleCache), 204);
		registerEntityComponentSerializer(TextComponent.class, new TextComponentSerializer(kryo, fontCache), 205);

		kryo.register(EditorPositionComponent.class, 206);
		kryo.register(ExporterDropsComponent.class, 207);
		kryo.register(PixelsPerUnitComponent.class, 208);
		kryo.register(UUIDComponent.class, 209);

		kryo.register(AssetComponent.class, 220);
		kryo.register(GroupComponent.class, 221);
		kryo.register(IDComponent.class, 222);
		kryo.register(InvisibleComponent.class, 223);
		kryo.register(LayerComponent.class, 224);
		kryo.register(RenderableComponent.class, 225);
		registerEntityComponentSerializer(ShaderComponent.class, new ShaderComponentSerializer(kryo, shaderCache), 226);
		kryo.register(PolygonComponent.class, 227);
		kryo.register(PhysicsPropertiesComponent.class, 228);
		kryo.register(VariablesComponent.class, 229);
	}
}
