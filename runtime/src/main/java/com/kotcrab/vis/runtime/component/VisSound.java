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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.audio.Sound;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.component.proto.ProtoVisSound;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFile;

/**
 * Stores single sound.
 * @author Kotcrab
 */
public class VisSound extends Component implements UsesProtoComponent {
	@ATSelectFile(fieldName = "Sound", extension = "mp3|wav|ogg", hideExtension = false, handlerAlias = "sound")
	public transient Sound sound;

	public VisSound () {
	}

	public VisSound (Sound sound) {
		this.sound = sound;
	}

	public long play () {
		return sound.play();
	}

	public long play (float volume) {
		return sound.play(volume);
	}

	public long play (float volume, float pitch, float pan) {
		return sound.play(volume, pitch, pan);
	}

	public long loop () {
		return sound.loop();
	}

	public long loop (float volume) {
		return sound.loop(volume);
	}

	public long loop (float volume, float pitch, float pan) {
		return sound.loop(volume, pitch, pan);
	}

	public void stop () {
		sound.stop();
	}

	public void pause () {
		sound.pause();
	}

	public void resume () {
		sound.resume();
	}

	@Override
	public ProtoComponent<VisSound> toProtoComponent () {
		return new ProtoVisSound();
	}
}
