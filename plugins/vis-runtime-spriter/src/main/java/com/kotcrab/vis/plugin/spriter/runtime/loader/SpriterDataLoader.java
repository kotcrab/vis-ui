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

package com.kotcrab.vis.plugin.spriter.runtime.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.SCMLReader;

/**
 * @author Kotcrab
 */
public class SpriterDataLoader extends SynchronousAssetLoader<SpriterData, SpriterDataLoader.SpriterDataParameter> {
	public SpriterDataLoader () {
		this(new InternalFileHandleResolver());
	}

	public SpriterDataLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public SpriterData load (AssetManager assetManager, String fileName, FileHandle file, SpriterDataParameter parameter) {
		Data data = new SCMLReader(file.read()).getData();
		Loader<Sprite> loader = new SpriterLoader(data);
		loader.load(file.file());
		return new SpriterData(data, loader);
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SpriterDataParameter parameter) {
		return null;
	}

	static public class SpriterDataParameter extends AssetLoaderParameters<SpriterData> {
	}
}
