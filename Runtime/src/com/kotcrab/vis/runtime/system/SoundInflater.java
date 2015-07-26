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

package com.kotcrab.vis.runtime.system;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.SoundComponent;
import com.kotcrab.vis.runtime.component.SoundProtoComponent;

/**
 * Inflates {@link SoundProtoComponent} into {@link SoundComponent}
 * @author Kotcrab
 */
@Wire
public class SoundInflater extends Manager {
	private ComponentMapper<SoundComponent> protoCm;
	private ComponentMapper<AssetComponent> assetCm;

	private EntityTransmuter transmuter;

	private RuntimeConfiguration configuration;
	private AssetManager manager;

	public SoundInflater (RuntimeConfiguration configuration, AssetManager manager) {
		this.configuration = configuration;
		this.manager = manager;
	}

	@Override
	protected void initialize () {
		EntityTransmuterFactory factory = new EntityTransmuterFactory(world).remove(SoundProtoComponent.class);
		if (configuration.removeAssetsComponentAfterInflating) factory.remove(AssetComponent.class);
		transmuter = factory.build();
	}

	@Override
	public void added (Entity e) {
		if (protoCm.has(e) == false) return;

		AssetComponent assetComponent = assetCm.get(e);

		PathAsset asset = (PathAsset) assetComponent.asset;

		Sound sound = manager.get(asset.getPath(), Sound.class);
		SoundComponent soundComponent = new SoundComponent(sound);

		transmuter.transmute(e);
		e.edit().add(soundComponent);
	}
}
