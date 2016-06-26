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

package com.kotcrab.vis.plugin.spine.runtime;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.kotcrab.vis.plugin.spine.runtime.SkeletonDataLoader.SkeletonDataLoaderParameter;

/**
 * AssetLoader for {@link SkeletonData} instances.
 * Loads an exported Spine's skeleton data.
 * The atlas with the images will be loaded as a dependency. This has to be declared as a {@link SkeletonDataLoaderParameter}
 * in the  {@link AssetManager#load(String, Class, AssetLoaderParameters)} call.
 * Supports both binary and JSON skeleton format files. If the animation file name has a 'skel' extension,
 * it will be loaded as binary. Any other extension will be assumed as JSON.
 * <p>
 * Example: suppose you have 'data/spine/character.atlas', 'data/spine/character.png' and 'data/spine/character.skel'.
 * To load it with an asset manager, just do the following:
 * <pre>
 * assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(new InternalFileHandleResolver()));
 * SkeletonDataLoaderParameter parameter = new SkeletonDataLoaderParameter("data/spine/character.atlas");
 * assetManager.load("data/spine/character.skel", SkeletonData.class, parameter);
 * </pre>
 * @author Alvaro Barbeira
 */
public class SkeletonDataLoader extends AsynchronousAssetLoader<SkeletonData, SkeletonDataLoaderParameter> {
	SkeletonData skeletonData;

	public SkeletonDataLoader () {
		this(new InternalFileHandleResolver());
	}

	public SkeletonDataLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, SkeletonDataLoaderParameter parameter) {
		skeletonData = null;
		TextureAtlas atlas = manager.get(parameter.atlasPath, TextureAtlas.class);

		String extension = file.extension();
		if (extension.toLowerCase().equals("skel")) {
			SkeletonBinary skeletonBinary = new SkeletonBinary(atlas);
			skeletonBinary.setScale(parameter.scale);
			skeletonData = skeletonBinary.readSkeletonData(resolve(parameter.skeletonPath));
		} else {
			SkeletonJson skeletonJson = new SkeletonJson(atlas);
			skeletonJson.setScale(parameter.scale);
			skeletonData = skeletonJson.readSkeletonData(resolve(parameter.skeletonPath));
		}
	}

	@Override
	public SkeletonData loadSync (AssetManager manager, String fileName, FileHandle file, SkeletonDataLoaderParameter parameter) {
		return skeletonData;
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SkeletonDataLoaderParameter parameter) {
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		deps.add(new AssetDescriptor(parameter.atlasPath, TextureAtlas.class));
		return deps;
	}

	/**
	 * Mandatory parameter to be passed to {@link AssetManager#load(String, Class, AssetLoaderParameters)}.
	 * This will insure the skeleton data is loaded correctly
	 * @author Alvaro Barbeira
	 */
	static public class SkeletonDataLoaderParameter extends AssetLoaderParameters<SkeletonData> {
		// A SkeletonJson must be loaded from an atlas.
		public String atlasPath;
		public String skeletonPath;
		public float scale;

		public SkeletonDataLoaderParameter (String atlasPath, String skeletonPath, float scale) {
			this.atlasPath = atlasPath;
			this.skeletonPath = skeletonPath;
			this.scale = scale;
		}

		public SkeletonDataLoaderParameter (String atlasPath, String skeletonPath) {
			this(atlasPath, skeletonPath, 1);
		}
	}
}
