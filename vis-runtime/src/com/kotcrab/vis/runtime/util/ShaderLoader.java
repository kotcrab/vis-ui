/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.kotcrab.vis.runtime.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.util.ShaderLoader.ShaderProgramParameter;

/**
 * Shader loader for {@link AssetManager}. Expects that fragment shader file ends with .frag and vertex shader file name
 * ends with .vert
 * @author Kotcrab
 */
public class ShaderLoader extends AsynchronousAssetLoader<ShaderProgram, ShaderProgramParameter> {
	public ShaderLoader () {
		this(new InternalFileHandleResolver());
	}

	public ShaderLoader (FileHandleResolver resolver) {
		super(resolver);
		ShaderProgram.pedantic = false;
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, ShaderProgramParameter parameter) {
		return null;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, ShaderProgramParameter parameter) {
	}

	@Override
	public ShaderProgram loadSync (AssetManager manager, String fileName, FileHandle file, ShaderProgramParameter parameter) {
		FileHandle vert = file.sibling(file.name() + ".vert");
		FileHandle frag = file.sibling(file.name() + ".frag");

		ShaderProgram shader = new ShaderProgram(vert, frag);
		if (!shader.isCompiled())
			Gdx.app.error("ShaderLoader", "Shader compilation failed:\n" + shader.getLog());

		return shader;
	}

	static public class ShaderProgramParameter extends AssetLoaderParameters<ShaderProgram> {
	}
}
