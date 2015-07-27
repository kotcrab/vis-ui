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

package com.kotcrab.vis.editor.module.project;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.entity.*;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.project.SupportModule.SupportSerializedTypeDescriptor;
import com.kotcrab.vis.editor.module.project.SupportModule.SupportSerializerDescriptor;
import com.kotcrab.vis.editor.plugin.PluginKryoSerializer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.serializer.*;
import com.kotcrab.vis.editor.util.vis.ProtoEntity;
import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.runtime.util.EntityEngine;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Allows to load VisEditor scenes. This API should not be used directly. See {@link SceneCacheModule}
 * @author Kotcrab
 * @see SceneCacheModule
 */
@SuppressWarnings("rawtypes")
public class SceneIOModule extends ProjectModule {
	public static final int KRYO_PLUGINS_RESERVED_ID_BEGIN = 401;
	public static final int KRYO_PLUGINS_RESERVED_ID_END = 800;

	private Kryo kryo;

	@InjectModule private FileAccessModule fileAccessModule;
	@InjectModule private SupportModule supportModule;

	@InjectModule private TextureCacheModule textureCache;
	@InjectModule private ParticleCacheModule particleCache;
	@InjectModule private FontCacheModule fontCache;

	private FileHandle assetsFolder;
	private FileHandle sceneBackupFolder;

	private Array<EntityComponentSerializer> entityComponentSerializers = new Array<>();

	@Override
	public void init () {
		assetsFolder = fileAccessModule.getAssetsFolder();
		sceneBackupFolder = fileAccessModule.getModuleFolder(".sceneBackup");

		kryo = new Kryo();
		kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.setRegistrationRequired(true);
		//id configuration:
		//0-8 kryo primitives
		//10-200 custom base types (sub categories don't have to be strictly enforced)
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

		kryo.register(EditorScene.class, new EditorSceneSerializer(kryo), 31);
		kryo.register(EntityScheme.class, new EntitySchemeSerializer(kryo, this), 32);
		kryo.register(SceneViewport.class, 33);
		kryo.register(Layer.class, 34);
		kryo.register(BitmapFontParameter.class, 35);
		kryo.register(TextureFilter.class, 36);

		kryo.register(PathAsset.class, 61);
		kryo.register(TextureRegionAsset.class, 62);
		kryo.register(AtlasRegionAsset.class, 63);
		kryo.register(BmpFontAsset.class, 64);
		kryo.register(TtfFontAsset.class, 65);

		registerEntityComponentSerializer(SpriteComponent.class, new SpriteComponentSerializer(kryo, textureCache), 201);
		registerEntityComponentSerializer(MusicComponent.class, new MusicComponentSerializer(kryo), 202);
		kryo.register(SoundComponent.class, 203);
		registerEntityComponentSerializer(ParticleComponent.class, new ParticleComponentSerializer(kryo, particleCache), 204);
		registerEntityComponentSerializer(TextComponent.class, new TextComponentSerializer(kryo, fontCache), 205);

		kryo.register(PositionComponent.class, 206);
		kryo.register(ExporterDropsComponent.class, 207);
		kryo.register(PixelsPerUnitComponent.class, 208);
		kryo.register(UUIDComponent.class, 209);

		kryo.register(AssetComponent.class, 220);
		kryo.register(GroupComponent.class, 221);
		kryo.register(IDComponent.class, 222);
		kryo.register(InvisibleComponent.class, 223);
		kryo.register(LayerComponent.class, 224);
		kryo.register(RenderableComponent.class, 225);
	}

	private void registerEntityComponentSerializer (Class<? extends Component> componentClass, EntityComponentSerializer serializer, int id) {
		kryo.register(componentClass, serializer, id);
		entityComponentSerializers.add(serializer);
	}

	@Override
	public void postInit () {
		SupportModule supportModule = projectContainer.get(SupportModule.class);

		for (SupportSerializerDescriptor support : supportModule.getSerializerDescriptors()) {
			if (support.serializer == null) {
				Log.error("Missing plugin serializer: " + support.getSerializerClassName() + " (a plugin could be missing or failed to load)");
				continue;
			}

			kryo.register(((PluginKryoSerializer) support.serializer).getSerializedClassType(), support.serializer, support.id);

			if (support.serializer instanceof EntityComponentSerializer) {
				entityComponentSerializers.add((EntityComponentSerializer) support.serializer);
			}
		}

		for (SupportSerializedTypeDescriptor descriptor : supportModule.getTypesDescriptors()) {
			if (descriptor.clazz == null) {
				Log.error("Missing class from plugin: " + descriptor.getSerializedClassName() + " (a plugin could be missing or failed to load)");
				continue;
			}

			kryo.register(descriptor.clazz, descriptor.id);
		}
	}

	public ProtoEntity createProtoEntity (EntityEngine entityEngine, Entity entity, boolean preserveEntityId) {
		return new ProtoEntity(this, entityEngine, entity, preserveEntityId);
	}

	public Bag<Component> cloneEntityComponents (Bag<Component> components) {
		Bag<Component> clonedComponents = new Bag<>();

		entityComponentSerializers.forEach(entityComponentSerializer -> entityComponentSerializer.setComponents(components));
		components.forEach(component -> clonedComponents.add(kryo.copy(component)));
		entityComponentSerializers.forEach(entityComponentSerializer -> entityComponentSerializer.setComponents(null));

		return clonedComponents;
	}

	/** Use only when you need kryo instance for creating serializers. For (de)serialization use methods inside this class. */
	public Kryo getKryo () {
		return kryo;
	}

	public EditorScene load (FileHandle fullPathFile) {
		try {
			Input input = new Input(new FileInputStream(fullPathFile.file()));
			EditorScene scene = kryo.readObject(input, EditorScene.class);
			scene.path = fileAccessModule.relativizeToAssetsFolder(fullPathFile);
			input.close();

			return scene;
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		throw new IllegalStateException("There was an unknown error during scene loading");
	}

	public boolean save (EditorScene scene) {
		try {
			Output output = new Output(new FileOutputStream(getFileHandleForScene(scene).file()));
			kryo.writeObject(output, scene);
			output.close();
			return true;
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		return false;
	}

	public void setEngineSerializationContext (ImmutableBag<Component> components) {
		entityComponentSerializers.forEach(entityComponentSerializer -> entityComponentSerializer.setComponents(components));
	}

	public void create (FileHandle relativeScenePath, SceneViewport viewport, float width, float height, int pixelsPerUnit) {
		EditorScene scene = new EditorScene(relativeScenePath, viewport, width, height, pixelsPerUnit);
		save(scene);
	}

	public FileHandle getSceneBackupFolder () {
		return sceneBackupFolder;
	}

	public FileHandle getFileHandleForScene (EditorScene scene) {
		return assetsFolder.child(scene.path);
	}
}
