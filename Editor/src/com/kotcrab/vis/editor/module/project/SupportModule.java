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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.plugin.PluginKryoSerializer;
import com.kotcrab.vis.editor.ui.toast.ExceptionToast;

import java.util.UUID;

/**
 * Manages {@link EditorEntitySupport} loaded from plugins.
 * @author Kotcrab
 */
public class SupportModule extends ProjectModule {
	@InjectModule private ToastModule toastModule;

	private Array<EditorEntitySupport> supports = new Array<>();

	private Json json;
	private FileHandle descriptorFile;

	private DescriptorsStorage descriptorsStorage;

	@Override
	public void init () {
		json = new Json();
		json.addClassTag("DescriptorsStorage", DescriptorsStorage.class);
		json.addClassTag("SupportSerializerDescriptor", SupportSerializerDescriptor.class);
		json.addClassTag("SupportSerializedTypesDescriptor", SupportSerializedTypeDescriptor.class);

		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		descriptorFile = fileAccess.getModuleFolder().child("supportDescriptor.json");

		if (descriptorFile.exists()) {
			descriptorsStorage = loadDescriptor();
		} else {
			Log.info("ObjectSupportModule", "Support descriptor file does not exist, will be recreated");
			descriptorsStorage = new DescriptorsStorage();
		}

		ExtensionStorageModule pluginContainer = container.get(ExtensionStorageModule.class);
		pluginContainer.getObjectSupports().forEach(this::register);
	}

	private DescriptorsStorage loadDescriptor () {
		try {
			return json.fromJson(DescriptorsStorage.class, descriptorFile);
		} catch (Exception e) {
			toastModule.show(new ExceptionToast("Support descriptor file couldn't be loaded, plugins may not function properly.\nIt's not recommend to continue.", e));

			String backupPath = descriptorFile.sibling(descriptorFile.name() + ".bak").path();
			FileHandle backup = Gdx.files.absolute(backupPath);
			if (backup.exists()) {
				backup = Gdx.files.absolute(backupPath + UUID.randomUUID());
			}

			descriptorFile.moveTo(backup);
			Log.exception(e);
			return new DescriptorsStorage();
		}
	}

	@Override
	public void dispose () {
		saveDescriptors();
	}

	private void saveDescriptors () {
		json.toJson(descriptorsStorage, descriptorFile);
	}

	public void register (EditorEntitySupport support) {
		supports.add(support);

		support.bindModules(projectContainer);

		for (Serializer serializer : support.getSerializers()) {
			if (serializer instanceof PluginKryoSerializer == false) {
				throw new IllegalStateException("All plugin serializer must implement PluginKryoSerializer interface");
			}

			SupportSerializerDescriptor desc = getSerializerDescriptorByClass(serializer.getClass());

			if (desc == null) {
				desc = new SupportSerializerDescriptor(getFreeId(), serializer.getClass().getName());
				descriptorsStorage.serializers.add(desc);
			}

			desc.serializer = serializer;
		}

		if (support.getSerializedTypes() != null) {
			for (Class<?> clazz : support.getSerializedTypes()) {
				SupportSerializedTypeDescriptor desc = getTypeDescriptorByClass(clazz);

				if (desc == null) {
					desc = new SupportSerializedTypeDescriptor(getFreeId(), clazz.getName());
					descriptorsStorage.types.add(desc);
				}

				desc.clazz = clazz;
			}
		}
		saveDescriptors();
	}

	private SupportSerializerDescriptor getSerializerDescriptorByClass (Class clazz) {
		String clazzName = clazz.getName();

		for (SupportSerializerDescriptor desc : descriptorsStorage.serializers) {
			if (desc.serializerClassName.equals(clazzName)) return desc;
		}

		return null;
	}

	private SupportSerializedTypeDescriptor getTypeDescriptorByClass (Class clazz) {
		String clazzName = clazz.getName();

		for (SupportSerializedTypeDescriptor desc : descriptorsStorage.types) {
			if (desc.serializedClassName.equals(clazzName)) return desc;
		}

		return null;
	}

	private int getFreeId () {
		int startId = SceneIOModule.KRYO_PLUGINS_RESERVED_ID_BEGIN;

		for (int i = startId; i < SceneIOModule.KRYO_PLUGINS_RESERVED_ID_END; i++) {
			if (isIdUsed(i) == false) return i;
		}

		throw new IllegalStateException("Free id for object support does not exist");
	}

	private boolean isIdUsed (int id) {
		for (SupportSerializerDescriptor desc : descriptorsStorage.serializers) {
			if (desc.id == id) return true;
		}

		for (SupportSerializedTypeDescriptor desc : descriptorsStorage.types) {
			if (desc.id == id) return true;
		}

		return false;
	}

	public Array<EditorEntitySupport> getSupports () {
		return supports;
	}

	public Array<SupportSerializerDescriptor> getSerializerDescriptors () {
		return descriptorsStorage.serializers;
	}

	public Array<SupportSerializedTypeDescriptor> getTypesDescriptors () {
		return descriptorsStorage.types;
	}

	public static class DescriptorsStorage {
		private Array<SupportSerializerDescriptor> serializers = new Array<>();
		private Array<SupportSerializedTypeDescriptor> types = new Array<>();

		public DescriptorsStorage () {
		}
	}

	public static class SupportSerializedTypeDescriptor {
		public int id;

		private String serializedClassName;
		public transient Class clazz;

		public SupportSerializedTypeDescriptor () {
		}

		public SupportSerializedTypeDescriptor (int id, String clazzName) {
			this.id = id;
			this.serializedClassName = clazzName;
		}

		public String getSerializedClassName () {
			return serializedClassName;
		}
	}

	public static class SupportSerializerDescriptor {
		public int id;

		private String serializerClassName;
		public transient Serializer serializer;

		public SupportSerializerDescriptor () {
		}

		public SupportSerializerDescriptor (int id, String clazzName) {
			this.serializerClassName = clazzName;
			this.id = id;
		}

		public String getSerializerClassName () {
			return serializerClassName;
		}
	}
}
