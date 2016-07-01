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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.FileReference;
import com.brashmonkey.spriter.Loader;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Trixt0r
 * @author Kotcrab
 */
public class SpriterLoader extends Loader<Sprite> implements Disposable {

	public static int standardAtlasWidth = 2048, standardAtlasHeight = 2048;

	private PixmapPacker packer;
	private HashMap<FileReference, Pixmap> pixmaps;
	private HashMap<Pixmap, Boolean> pixmapsToDispose;
	private boolean pack;
	private int atlasWidth, atlasHeight;

	public SpriterLoader (Data data) {
		this(data, true);
	}

	public SpriterLoader (Data data, boolean pack) {
		this(data, standardAtlasWidth, standardAtlasHeight);
		this.pack = pack;
	}

	public SpriterLoader (Data data, int atlasWidth, int atlasHeight) {
		super(data);
		this.pack = true;
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.pixmaps = new HashMap<FileReference, Pixmap>();
		this.pixmapsToDispose = new HashMap<Pixmap, Boolean>();
	}

	@Override
	protected Sprite loadResource (FileReference ref) {
		FileHandle f;
		String path = super.root + "/" + data.getFile(ref).name;
		switch (Gdx.app.getType()) {
			case iOS:
				f = Gdx.files.absolute(path);
				break;
			default:
				f = Gdx.files.internal(path);
				break;
		}

		if (!f.exists())
			throw new GdxRuntimeException("Could not find file handle " + path + "! Please check your paths.");
		if (this.packer == null && this.pack)
			this.packer = new PixmapPacker(this.atlasWidth, this.atlasHeight, Pixmap.Format.RGBA8888, 2, true);
		final Pixmap pix = new Pixmap(f);
		this.pixmaps.put(ref, pix);
		return null;
	}

	/**
	 * Packs all loaded sprites into an atlas. Has to called after loading all sprites.
	 */
	protected void generatePackedSprites () {
		if (this.packer == null) return;
		TextureAtlas tex = this.packer.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);
		Set<FileReference> keys = this.resources.keySet();
		this.disposeNonPackedTextures();
		for (FileReference ref : keys) {
			TextureRegion texReg = tex.findRegion(data.getFile(ref).name);
			super.resources.put(ref, new Sprite(texReg));
		}
	}

	private void disposeNonPackedTextures () {
		for (Entry<FileReference, Sprite> entry : super.resources.entrySet())
			entry.getValue().getTexture().dispose();
	}

	@Override
	public void dispose () {
		if (this.pack && this.packer != null) this.packer.dispose();
		else this.disposeNonPackedTextures();
		super.dispose();
	}

	protected void finishLoading () {
		Set<FileReference> refs = this.resources.keySet();
		for (FileReference ref : refs) {
			Pixmap pix = this.pixmaps.get(ref);
			this.pixmapsToDispose.put(pix, false);
			this.createSprite(ref, pix);

			if (this.packer != null) packer.pack(data.getFile(ref).name, pix);
		}
		if (this.pack) generatePackedSprites();
		this.disposePixmaps();
	}

	protected void createSprite (FileReference ref, Pixmap image) {
		Texture tex = new Texture(image);
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion texRegion = new TextureRegion(tex);
		super.resources.put(ref, new Sprite(texRegion));
		pixmapsToDispose.put(image, true);
	}

	protected void disposePixmaps () {
		Pixmap[] maps = new Pixmap[this.pixmapsToDispose.size()];
		this.pixmapsToDispose.keySet().toArray(maps);
		for (Pixmap pix : maps) {
			try {
				while (pixmapsToDispose.get(pix)) {
					pix.dispose();
					pixmapsToDispose.put(pix, false);
				}
			} catch (GdxRuntimeException e) {
				System.err.println("Pixmap was already disposed!");
			}
		}
		pixmapsToDispose.clear();
	}

}
