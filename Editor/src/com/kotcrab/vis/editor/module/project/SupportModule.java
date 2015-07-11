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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryo.Serializer;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.plugin.PluginKryoSerializer;

/**
 * Manages {@link EditorEntitySupport} loaded from plugins.
 * @author Kotcrab
 */
public class SupportModule extends ProjectModule {
	private Array<EditorEntitySupport> supports = new Array<>();
	private Array<SupportSerializerDescriptor> descriptors;

	private Json json;
	private FileHandle descriptorFile;

	@Override
	public void init () {
		json = new Json();
		json.addClassTag("SupportSerializerDescriptor", SupportSerializerDescriptor.class);

		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		descriptorFile = fileAccess.getModuleFolder().child("supportDescriptor.json");

		if (descriptorFile.exists()) {
			descriptors = json.fromJson(new Array<SupportSerializerDescriptor>().getClass(), descriptorFile);
		} else {
			Log.info("ObjectSupportModule", "Support descriptor file does not exist, will be recreated");
			descriptors = new Array<>();
		}

		ExtensionStorageModule pluginContainer = container.get(ExtensionStorageModule.class);
		Array<EditorEntitySupport> supports = pluginContainer.getObjectSupports();
		for (EditorEntitySupport support : supports)
			register(support);
	}

	@Override
	public void dispose () {
		saveDescriptors();
	}

	private void saveDescriptors () {
		json.toJson(descriptors, descriptorFile);
	}

	public void register (EditorEntitySupport support) {
		supports.add(support);

		for (Serializer serializer : support.getSerializers()) {
			if (serializer instanceof PluginKryoSerializer == false)
				throw new IllegalStateException("All plugin serializer must implement PluginKryoSerializer interface");

			SupportSerializerDescriptor desc = getDescriptorByClazz(serializer.getClass());

			if (desc == null) {
				desc = new SupportSerializerDescriptor(serializer.getClass().getName(), getFreeId());
				descriptors.add(desc);
				saveDescriptors();
			}

			desc.serializer = serializer;
		}

		support.bindModules(projectContainer);
	}

	private SupportSerializerDescriptor getDescriptorByClazz (Class clazz) {
		String clazzName = clazz.getName();

		for (SupportSerializerDescriptor desc : descriptors)
			if (desc.serializerClazzName.equals(clazzName)) return desc;

		return null;
	}

	private int getFreeId () {
		int startId = SceneIOModule.KRYO_PLUGINS_RESERVED_ID_BEGIN;

		for (int i = startId; i < SceneIOModule.KRYO_PLUGINS_RESERVED_ID_END; i++)
			if (isIdUsed(i) == false) return i;

		throw new IllegalStateException("Free id for object support does not exist");
	}

	private boolean isIdUsed (int id) {
		for (SupportSerializerDescriptor desc : descriptors)
			if (desc.id == id) return true;

		return false;
	}

	public Array<EditorEntitySupport> getSupports () {
		return supports;
	}

	public Array<SupportSerializerDescriptor> getSerializerDescriptors () {
		return descriptors;
	}

	public static class SupportSerializerDescriptor {
		public transient Serializer serializer;
		public transient Class serializedClazz;

		public String serializerClazzName;
		public int id;

		public SupportSerializerDescriptor () {
		}

		public SupportSerializerDescriptor (String clazzName, int id) {
			this.serializerClazzName = clazzName;
			this.id = id;
		}
	}
}
