package com.kotcrab.vis.runtime.scene;

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
import com.kotcrab.vis.runtime.scene.ShaderLoader.ShaderProgramParameter;

public class ShaderLoader extends AsynchronousAssetLoader<ShaderProgram, ShaderProgramParameter> {
	public ShaderLoader () {
		this(new InternalFileHandleResolver());
	}

	public ShaderLoader (FileHandleResolver resolver) {
		super(resolver);
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
			Gdx.app.error("ShaderLoader", "compilation failed:\n" + shader.getLog());

		return shader;
	}

	static public class ShaderProgramParameter extends AssetLoaderParameters<ShaderProgram> {
	}
}
