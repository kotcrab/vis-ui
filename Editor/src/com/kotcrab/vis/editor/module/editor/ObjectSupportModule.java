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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.editor.plugin.ObjectSupport;

public class ObjectSupportModule extends ProjectModule {

	private ObjectMap<Class, ObjectSupport> supportMap = new ObjectMap<>();
	private Array<SupportDescriptor> descriptors;

	private Json json = new Json();
	private FileHandle descriptorFile;

	@Override
	public void init () {
		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		descriptorFile = fileAccess.getModuleFolder().child("supportDescriptor.json");

		if (descriptorFile.exists()) {
			descriptors = json.fromJson(new Array<SupportDescriptor>().getClass(), descriptorFile);
		} else
			descriptors = new Array<>();
	}

	@Override
	public void dispose () {
		saveDescriptors();
	}

	private void saveDescriptors () {
		json.toJson(descriptors, descriptorFile);
	}

	public void register (ObjectSupport support) {
		supportMap.put(support.getObjectClass(), support);

		SupportDescriptor desc = getDescriptorBySupport(support);

		if (desc == null) {
			desc = new SupportDescriptor(support.getObjectClass().getName(), getFreeId());
			descriptors.add(desc);
			saveDescriptors();
		}

		support.assignId(desc.id);
	}

	private SupportDescriptor getDescriptorBySupport (ObjectSupport support) {
		String clazzName = support.getObjectClass().getName();

		for (SupportDescriptor desc : descriptors)
			if (desc.clazzName.equals(clazzName)) return desc;

		return null;
	}

	public ObjectSupport get (Class key) {
		return supportMap.get(key);
	}

	public Values<ObjectSupport> getSupports () {
		return supportMap.values();
	}

	public ObjectMap<Class, ObjectSupport> getSupportsMap () {
		return supportMap;
	}

	private int getFreeId () {
		int startId = 60; //plugins can use id 60-100

		for (int i = startId; i < 100; i++)
			if (isIdUsed(i) == false) return i;

		throw new IllegalStateException("Free id for object support does not exist");
	}

	private boolean isIdUsed (int id) {
		for (SupportDescriptor desc : descriptors)
			if (desc.id == id) return true;

		return false;
	}

	public static class SupportDescriptor {
		public String clazzName;
		public int id;

		public SupportDescriptor (String clazzName, int id) {
			this.clazzName = clazzName;
			this.id = id;
		}
	}
}
