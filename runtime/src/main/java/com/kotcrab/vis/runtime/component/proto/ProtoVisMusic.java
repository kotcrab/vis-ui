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

package com.kotcrab.vis.runtime.component.proto;

import com.kotcrab.vis.runtime.component.VisMusic;
import com.kotcrab.vis.runtime.system.inflater.MusicInflater;

/**
 * {@link ProtoComponent} for {@link VisMusic}.
 * @author Kotcrab
 * @see MusicInflater
 */
public class ProtoVisMusic extends ProtoComponent<VisMusic> {
	public boolean playOnStart;
	public boolean looping;
	public float volume;

	public ProtoVisMusic () {
	}

	public ProtoVisMusic (VisMusic component) {
		playOnStart = component.isPlayOnStart();
		volume = component.getVolume();
		looping = component.isLooping();
	}

	@Override
	public void fill (VisMusic component) {
		component.setLooping(looping);
		component.setPlayOnStart(playOnStart);
		component.setVolume(volume);
	}
}
