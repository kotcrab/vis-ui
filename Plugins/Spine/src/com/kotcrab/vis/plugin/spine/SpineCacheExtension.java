/*
 * Spine Runtimes Software License
 * Version 2.3
 *
 * Copyright (c) 2013-2015, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to use, install, execute and perform the Spine
 * Runtimes Software (the "Software") and derivative works solely for personal
 * or internal use. Without the written permission of Esoteric Software (see
 * Section 2 of the Spine Software License Agreement), you may not (a) modify,
 * translate, adapt or otherwise create derivative works, improvements of the
 * Software or develop new applications using the Software or (b) remove,
 * delete, alter or obscure any trademarks or any copyright, trademark, patent
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.kotcrab.vis.editor.module.InjectModule;
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
	@InjectModule private FileAccessModule fileAccess;

	private ObjectMap<FileHandle, TextureAtlas> atlases = new ObjectMap<>();
	private ObjectMap<FileHandle, SkeletonData> skeletonsData = new ObjectMap<>();

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
