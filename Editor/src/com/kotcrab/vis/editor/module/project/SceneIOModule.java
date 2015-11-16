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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.ProjectMenuBarEvent;
import com.kotcrab.vis.editor.event.ProjectMenuBarEventType;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.serializer.cloner.BagCloner;
import com.kotcrab.vis.editor.serializer.cloner.IntArrayCloner;
import com.kotcrab.vis.editor.serializer.cloner.IntMapCloner;
import com.kotcrab.vis.editor.serializer.cloner.ObjectMapCloner;
import com.kotcrab.vis.editor.serializer.json.*;
import com.kotcrab.vis.editor.ui.scene.NewSceneDialog;
import com.kotcrab.vis.editor.util.vis.ProtoEntity;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.InvisibleComponent;
import com.kotcrab.vis.runtime.component.ProtoComponent;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.UsesProtoComponent;
import com.rits.cloning.Cloner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * Allows to load VisEditor scenes. This API should not be used directly. See {@link SceneCacheModule}
 * @author Kotcrab
 * @see SceneCacheModule
 */
@SuppressWarnings("rawtypes")
@EventBusSubscriber
public class SceneIOModule extends ProjectModule {
	private static final String TAG = "SceneIOModule";

	protected Cloner cloner;
	protected Gson gson;

	protected Stage stage;

	protected ExtensionStorageModule extensionStorage;

	protected FileAccessModule fileAccessModule;

	private FileHandle assetsFolder;
	private FileHandle sceneBackupFolder;

	@Override
	public void init () {
		assetsFolder = fileAccessModule.getAssetsFolder();
		sceneBackupFolder = fileAccessModule.getModuleFolder(".sceneBackup");

		cloner = new Cloner();
		cloner.setNullTransient(true);
		//TODO: [plugins] plugin entry point?
		cloner.registerFastCloner(Bag.class, new BagCloner());
		cloner.registerFastCloner(IntArray.class, new IntArrayCloner());
		cloner.registerFastCloner(IntMap.class, new IntMapCloner());
		cloner.registerFastCloner(ObjectMap.class, new ObjectMapCloner());

		setupSerializer();
	}

	@Subscribe
	public void handleProjectMenuBarEvent (ProjectMenuBarEvent event) {
		if (event.type == ProjectMenuBarEventType.SHOW_NEW_SCENE_DIALOG) {
			stage.addActor(new NewSceneDialog(projectContainer).fadeIn());
		}
	}

	protected void setupSerializer () {
		ClassJsonSerializer classSerializer;

		GsonBuilder builder = new GsonBuilder()
				.setPrettyPrinting()
				.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
				.registerTypeAdapter(Array.class, new ArrayJsonSerializer())
				.registerTypeAdapter(IntArray.class, new IntArrayJsonSerializer())
				.registerTypeAdapter(IntMap.class, new IntMapJsonSerializer())
				.registerTypeAdapter(ObjectMap.class, new ObjectMapJsonSerializer())
				.registerTypeAdapter(Class.class, classSerializer = new ClassJsonSerializer(Thread.currentThread().getContextClassLoader()))
				.registerTypeAdapter(AssetComponent.class, new AssetComponentSerializer());

		//TODO: [plugin] plugin entry point, allow plugin to simpler serializer registration, currently requires making EditorEntitySupport
		//register plugins serializers
		extensionStorage.getEntitiesSupports().forEach(
				support -> support.getJsonTypeAdapters().forEach(
						typeObjectEntry -> builder.registerTypeAdapter(typeObjectEntry.key, typeObjectEntry.value)));

		gson = builder.create();

		EditorJsonTags.registerTags(new GsonTagRegistrar(classSerializer));
	}

	public ProtoEntity createProtoEntity (EntityEngine entityEngine, Entity entity, boolean preserveEntityId) {
		return new ProtoEntity(this, entityEngine, entity, preserveEntityId);
	}

	public Bag<Component> cloneEntityComponents (Bag<Component> components) {
		Bag<Component> clonedComponents = new Bag<>();

		components.forEach(component -> {
			if (component instanceof InvisibleComponent) return;

			if (component instanceof UsesProtoComponent) {
				ProtoComponent protoComponent = ((UsesProtoComponent) component).getProtoComponent();
				clonedComponents.add(cloner.deepClone(protoComponent));
			} else {
				clonedComponents.add(cloner.deepClone(component));
			}
		});

		return clonedComponents;
	}

	public EditorScene load (FileHandle fullPathFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fullPathFile.file()));
			EditorScene scene = gson.fromJson(reader, EditorScene.class);
			scene.path = fileAccessModule.relativizeToAssetsFolder(fullPathFile);
			reader.close();

			scene.onDeserialize();

			return scene;
		} catch (IOException e) {
			Log.exception(e);
		}

		throw new IllegalStateException("There was an unknown error during scene loading");
	}

	public boolean save (EditorScene scene) {
		try {
			FileWriter writer = new FileWriter(getFileHandleForScene(scene).file());
			gson.toJson(scene, writer);
			writer.close();
			return true;
		} catch (Exception e) {
			Log.exception(e);
		}

		return false;
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
