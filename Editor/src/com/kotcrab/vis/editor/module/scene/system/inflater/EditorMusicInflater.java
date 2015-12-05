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

package com.kotcrab.vis.editor.module.scene.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.kotcrab.vis.editor.util.gdx.DummyMusic;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.MusicComponent;
import com.kotcrab.vis.runtime.component.proto.MusicProtoComponent;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorMusicInflater extends InflaterSystem {
	private ComponentMapper<MusicComponent> musicCm;
	private ComponentMapper<MusicProtoComponent> protoCm;

	public EditorMusicInflater () {
		super(Aspect.all(MusicProtoComponent.class, AssetComponent.class));
	}

	@Override
	protected void inserted (int entityId) {
		MusicProtoComponent musicProtoComponent = protoCm.get(entityId);

		MusicComponent musicComponent = musicCm.create(entityId);
		musicComponent.music = new DummyMusic();
		musicProtoComponent.fill(musicComponent);

		protoCm.remove(entityId);
	}
}
