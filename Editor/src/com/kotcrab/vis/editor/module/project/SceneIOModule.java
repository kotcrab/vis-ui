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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.scene.*;
import com.kotcrab.vis.editor.serializer.*;
import com.kotcrab.vis.editor.util.vis.ProtoEntity;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.component.SpriteComponent;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.runtime.util.EntityEngine;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Allows to load VisEditor scenes. This API should not be used directly. See {@link SceneCacheModule}
 * @author Kotcrab
 * @see SceneCacheModule
 */
@SuppressWarnings("rawtypes")
public class SceneIOModule extends ProjectModule {
	public static final int KRYO_PLUGINS_RESERVED_ID_BEGIN = 401;
	public static final int KRYO_PLUGINS_RESERVED_ID_END = 600;

	private Kryo kryo;

	@InjectModule private FileAccessModule fileAccessModule;

	private FileHandle assetsFolder;

	private Array<EntityComponentSerializer> entityComponentSerializers = new Array<>();

	@Override
	public void init () {
		assetsFolder = fileAccessModule.getAssetsFolder();

		TextureCacheModule textureCache = projectContainer.get(TextureCacheModule.class);
		FontCacheModule fontCache = projectContainer.get(FontCacheModule.class);
		ParticleCacheModule particleCache = projectContainer.get(ParticleCacheModule.class);

		kryo = new Kryo();
		kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);

		//id configuration:
		//0-8 kryo primitives
		//10-200 custom base types (sub categories don't have to be strictly enforced)
		//	10-30 libs classes
		//	31-60 vis classes
		//	61-100 assets descriptors
		//	101-200 reserved for future use
		//201-400 components, entities support (dep.)
		//401-600 plugins

		kryo.register(Array.class, new ArraySerializer(), 10);
		kryo.register(IntArray.class, new IntArraySerializer(), 11);
		kryo.register(Bag.class, new BagSerializer(), 12);
		kryo.register(Rectangle.class, 13);
		kryo.register(Matrix4.class, 14);
		kryo.register(Color.class, new ColorSerializer(), 15);

		kryo.register(EditorScene.class, new EditorSceneSerializer(kryo), 31);
		kryo.register(EntityScheme.class, new EntitySchemeSerializer(kryo, this), 32);
		kryo.register(SceneViewport.class, 33);

		kryo.register(PathAsset.class, 61);
		kryo.register(TextureRegionAsset.class, 62);
		kryo.register(AtlasRegionAsset.class, 63);

		kryo.register(MusicObject.class, new MusicObjectSerializer(kryo, projectContainer), 202);
		kryo.register(SoundObject.class, new SoundObjectSerializer(kryo, projectContainer), 203);
		kryo.register(ParticleEffectObject.class, new ParticleObjectSerializer(kryo, fileAccessModule, particleCache), 204);
		kryo.register(TextObject.class, new TextObjectSerializer(kryo, fileAccessModule, fontCache), 205);

		//TODO: [high] map other components
		registerEntityComponentSerializer(SpriteComponent.class, new SpriteComponentSerializer(kryo, textureCache), 201);
	}

	private void registerEntityComponentSerializer (Class<? extends Component> componentClass, EntityComponentSerializer serializer, int id) {
		kryo.register(componentClass, serializer, id);
		entityComponentSerializers.add(serializer);
	}

	@Override
	public void postInit () {
		ObjectSupportModule supportModule = projectContainer.get(ObjectSupportModule.class);

		for (ObjectSupport support : supportModule.getSupports())
			kryo.register(support.getObjectClass(), support.getSerializer(), support.getId());
	}

	public ProtoEntity createProtoEntity (EntityEngine entityEngine, Entity entity, boolean preserveEntity) {
		return new ProtoEntity(this, entityEngine, entity, preserveEntity);
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

	public void setEngineSeriazliationContext (ImmutableBag<Component> components) {
		entityComponentSerializers.forEach(entityComponentSerializer -> entityComponentSerializer.setComponents(components));
	}

	public void create (FileHandle relativeScenePath, SceneViewport viewport, int width, int height) {
		EditorScene scene = new EditorScene(relativeScenePath, viewport, width, height);
		save(scene);
	}

	public FileHandle getFileHandleForScene (EditorScene scene) {
		return assetsFolder.child(scene.path);
	}
}
