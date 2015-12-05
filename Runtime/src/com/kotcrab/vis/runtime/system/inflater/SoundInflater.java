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
import com.badlogic.gdx.audio.Sound;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.SoundComponent;
import com.kotcrab.vis.runtime.component.proto.SoundProtoComponent;

/**
 * Inflates {@link SoundProtoComponent} into {@link SoundComponent}
 * @author Kotcrab
 */
public class SoundInflater extends InflaterSystem {
	private ComponentMapper<SoundProtoComponent> protoCm;
	private ComponentMapper<SoundComponent> soundCm;
	private ComponentMapper<AssetComponent> assetCm;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public SoundInflater (RuntimeConfiguration configuration, AssetManager manager) {
		super(Aspect.all(SoundProtoComponent.class, AssetComponent.class));
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	public void inserted (int entityId) {
		AssetComponent assetComponent = assetCm.get(entityId);

		PathAsset asset = (PathAsset) assetComponent.asset;

		Sound sound = manager.get(asset.getPath(), Sound.class);
		if (sound == null) throw new IllegalStateException("Can't load scene, sound is missing: " + asset.getPath());
		SoundComponent soundComponent = soundCm.create(entityId);
		soundComponent.sound = sound;

		if (configuration.removeAssetsComponentAfterInflating) assetCm.remove(entityId);
		protoCm.remove(entityId);
	}
}
