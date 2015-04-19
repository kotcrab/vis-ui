package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.editor.plugin.ContainerExtension;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

@VisPlugin
public class SpineCacheExtension implements ContainerExtension<ProjectModule> {
	@Override
	public ProjectModule getModule () {
		return new SpineCacheModule();
	}

	@Override
	public ExtensionScope getScope () {
		return ExtensionScope.PROJECT;
	}
}

//TODO support dynamic refreshing
class SpineCacheModule extends ProjectModule {
	private FileAccessModule fileAccess;

	private ObjectMap<FileHandle, TextureAtlas> atlases = new ObjectMap<>();
	private ObjectMap<FileHandle, SkeletonData> skeletonsData = new ObjectMap<>();

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);
	}

	public SkeletonData get (String relativeAtlasPath, String relativeSkeletonPath) {
		return get(Gdx.files.absolute(fileAccess.derelativizeFromAssetsFolder(relativeAtlasPath)),
				Gdx.files.absolute(fileAccess.derelativizeFromAssetsFolder(relativeSkeletonPath)));
	}

	public SkeletonData get (FileHandle atlasFile, FileHandle skeletonFile) {
		SkeletonData data = skeletonsData.get(skeletonFile);

		if (data == null) {
			if (skeletonFile.extension().equals("json")) {
				SkeletonJson json = new SkeletonJson(getAtlas(atlasFile));
				data = json.readSkeletonData(skeletonFile);
				skeletonsData.put(skeletonFile, data);
			}

			if (skeletonFile.extension().equals("skel")) {
				SkeletonBinary binary = new SkeletonBinary(getAtlas(atlasFile));
				data = binary.readSkeletonData(skeletonFile);
				skeletonsData.put(skeletonFile, data);
			}
		}

		return data;
	}

	private TextureAtlas getAtlas (FileHandle atlasFile) {
		TextureAtlas atlas = atlases.get(atlasFile);

		if (atlas == null) {
			atlas = new TextureAtlas(atlasFile);
			atlases.put(atlasFile, atlas);
		}

		return atlas;
	}

	@Override
	public void dispose () {
		for (TextureAtlas atlas : atlases.values())
			atlas.dispose();
	}
}
