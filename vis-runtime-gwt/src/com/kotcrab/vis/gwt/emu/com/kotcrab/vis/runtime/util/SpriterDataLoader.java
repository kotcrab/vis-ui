package com.kotcrab.vis.runtime.util;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.util.SpriterDataLoader.SpriterDataParameter;

/**
 * @author Kotcrab
 */
public class SpriterDataLoader extends SynchronousAssetLoader<SpriterData, SpriterDataParameter> {
	public SpriterDataLoader () {
		this(new InternalFileHandleResolver());
	}

	public SpriterDataLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public SpriterData load (AssetManager assetManager, String fileName, FileHandle file, SpriterDataParameter parameter) {
		throw new IllegalStateException("Spriter is not supported on GWT!");
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SpriterDataParameter parameter) {
		return null;
	}

	static public class SpriterDataParameter extends AssetLoaderParameters<SpriterData> {
	}
}
