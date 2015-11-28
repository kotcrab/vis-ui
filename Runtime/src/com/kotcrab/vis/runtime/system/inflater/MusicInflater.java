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

package com.kotcrab.vis.runtime.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.MusicComponent;
import com.kotcrab.vis.runtime.component.MusicProtoComponent;

/**
 * Inflates {@link MusicProtoComponent} into {@link MusicComponent}
 * @author Kotcrab
 */
public class MusicInflater extends InflaterSystem {
	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<MusicComponent> musicCm;
	private ComponentMapper<MusicProtoComponent> protoCm;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public MusicInflater (RuntimeConfiguration configuration, AssetManager manager) {
		super(Aspect.all(MusicProtoComponent.class, AssetComponent.class));
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	protected void inserted (int entityId) {
		AssetComponent assetComponent = assetCm.get(entityId);
		MusicProtoComponent musicProtoComponent = protoCm.get(entityId);

		PathAsset asset = (PathAsset) assetComponent.asset;

		Music music = manager.get(asset.getPath(), Music.class);
		if (music == null) throw new IllegalStateException("Can't load scene, music is missing: " + asset.getPath());
		MusicComponent musicComponent = musicCm.create(entityId);
		musicComponent.music = music;
		musicProtoComponent.fill(musicComponent);
		if (musicComponent.playOnStart) musicComponent.music.play();

		if (configuration.removeAssetsComponentAfterInflating) assetCm.remove(entityId);
		protoCm.remove(entityId);
	}
}
