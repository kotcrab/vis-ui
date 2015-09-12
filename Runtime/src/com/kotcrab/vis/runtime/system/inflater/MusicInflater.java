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

import com.artemis.*;
import com.artemis.annotations.Wire;
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
@Wire
public class MusicInflater extends Manager {
	private ComponentMapper<AssetComponent> assetCm;
	private ComponentMapper<MusicProtoComponent> protoCm;

	private Entity flyweight;

	private EntityTransmuter transmuter;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public MusicInflater (RuntimeConfiguration configuration, AssetManager manager) {
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		flyweight = Entity.createFlyweight(world);
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(MusicProtoComponent.class);
		if (configuration.removeAssetsComponentAfterInflating) factory.remove(AssetComponent.class);
		transmuter = factory.build();
	}

	@Override
	public void added (int entityId) {
		flyweight.id = entityId;
		if (protoCm.has(entityId) == false) return;

		AssetComponent assetComponent = assetCm.get(entityId);
		MusicProtoComponent musicProtoComponent = protoCm.get(entityId);

		PathAsset asset = (PathAsset) assetComponent.asset;

		Music music = manager.get(asset.getPath(), Music.class);
		if(music == null) throw new IllegalStateException("Can't load scene music is missing: " + asset.getPath());
		MusicComponent musicComponent = new MusicComponent(music);

		musicComponent.setLooping(musicProtoComponent.looping);
		musicComponent.setPlayOnStart(musicProtoComponent.playOnStart);
		musicComponent.setVolume(musicProtoComponent.volume);

		transmuter.transmute(flyweight);
		flyweight.edit().add(musicComponent);
	}
}
