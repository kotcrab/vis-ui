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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.runtime.assets.ShaderAsset;

/**
 * @author Kotcrab
 */
public class ShaderCacheModule extends ProjectModule implements WatchListener {
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private AssetsWatcherModule watcherModule;
	@InjectModule private ToastModule toastModule;

	private ObjectMap<ShaderAsset, ShaderProgram> shaders = new ObjectMap<>();

	private ReloadShaderTask reloadTask = new ReloadShaderTask();

	@Override
	public void init () {
		watcherModule.addListener(this);
		ShaderProgram.pedantic = false;
		reloadShaders(false);
	}

	@Override
	public void dispose () {
		watcherModule.removeListener(this);
		for (ShaderProgram shader : shaders.values()) {
			shader.dispose();
		}
	}

	private void reloadShaders (boolean showSuccessMessage) {
		shaders.clear();
		Array<FileHandle> files = new Array<>(fileAccess.getShaderFolder().list());
		Array<FileHandle> handled = new Array<>();

		for (FileHandle file : files) {
			if (handled.contains(file, false)) continue;

			FileHandle vertexFile = null;
			FileHandle fragmentFile = null;

			if (file.extension().equals("frag")) {
				fragmentFile = file;
				vertexFile = file.sibling(file.nameWithoutExtension() + ".vert");
			}

			if (file.extension().equals("vert")) {
				vertexFile = file;
				fragmentFile = file.sibling(file.nameWithoutExtension() + ".frag");
			}

			if (vertexFile != null && vertexFile.exists())
				handled.add(vertexFile);

			if (fragmentFile != null && fragmentFile.exists())
				handled.add(fragmentFile);

			if (vertexFile == null && fragmentFile == null) {
				continue;
			}

			if (vertexFile == null || vertexFile.exists() == false) {
				toastModule.show(new DetailsToast("Shader compilation not possible, missing vertex file!", "Missing vertex file for fragment: " + fragmentFile.name()));
				continue;
			}

			if (fragmentFile == null || fragmentFile.exists() == false) {
				toastModule.show(new DetailsToast("Shader compilation not possible, missing fragment file!", "Missing fragment file for fragment: " + vertexFile.name()));
				continue;
			}

			ShaderProgram shader = new ShaderProgram(vertexFile, fragmentFile);
			if (shader.isCompiled() == false) {
				toastModule.show(new DetailsToast("Shader " + vertexFile.nameWithoutExtension() + " compilation failed!", shader.getLog()), 5);
			} else {
				if (showSuccessMessage)
					toastModule.show("Shader " + vertexFile.nameWithoutExtension() + " successfully compiled", 2);

				String vertPath = fileAccess.relativizeToAssetsFolder(vertexFile);
				String fragPath = fileAccess.relativizeToAssetsFolder(fragmentFile);
				ShaderAsset asset = new ShaderAsset(vertPath, fragPath);
				shaders.put(asset, shader);
			}
		}
	}

	@Override
	public void fileChanged (FileHandle file) {
		String relativePath = fileAccess.relativizeToAssetsFolder(file);
		if (relativePath.startsWith("shader") == false) return;

		if (reloadTask.isScheduled()) return;

		Timer.schedule(reloadTask, 0.5f);
	}

	public ShaderProgram get (ShaderAsset asset) {
		return shaders.get(asset);
	}

	private class ReloadShaderTask extends Task {
		@Override
		public void run () {
			ObjectMap<ShaderAsset, ShaderProgram> shadersCopy = new ObjectMap<>(shaders);

			reloadShaders(true);
			App.eventBus.post(new ResourceReloadedEvent(ResourceReloadedEvent.RESOURCE_SHADERS));

			for (ShaderProgram shader : shadersCopy.values()) {
				shader.dispose();
			}
		}
	}
}
