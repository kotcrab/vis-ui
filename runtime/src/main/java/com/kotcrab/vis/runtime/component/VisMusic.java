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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.component.proto.ProtoVisMusic;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;
import com.kotcrab.vis.runtime.util.autotable.ATReflectedProperty;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFile;

/**
 * Stores entity Music.
 * @author Kotcrab
 */
public class VisMusic extends Component implements UsesProtoComponent {
	@ATSelectFile(fieldName = "Music", extension = "mp3|wav|ogg", hideExtension = false, handlerAlias = "music")
	@ATProperty(fieldName = "Loop")
	@ATReflectedProperty(targetType = boolean.class, getterName = "isLooping", setterName = "setLooping")
	public transient Music music;

	@ATProperty(fieldName = "Play on start")
	public boolean playOnStart;

	public VisMusic () {
	}

	public VisMusic (Music music) {
		this.music = music;
	}

	@Override
	public ProtoComponent<VisMusic> toProtoComponent () {
		return new ProtoVisMusic(this);
	}

	public boolean isPlayOnStart () {
		return playOnStart;
	}

	public void setPlayOnStart (boolean playOnStart) {
		this.playOnStart = playOnStart;
	}

	public void setOnCompletionListener (OnCompletionListener listener) {
		music.setOnCompletionListener(listener);
	}

	public float getPosition () {
		return music.getPosition();
	}

	public void setMusicPosition (float position) {
		music.setPosition(position);
	}

	public float getVolume () {
		return music.getVolume();
	}

	public void setVolume (float volume) {
		music.setVolume(volume);
	}

	public void setPan (float pan, float volume) {
		music.setPan(pan, volume);
	}

	public boolean isLooping () {
		return music.isLooping();
	}

	public void setLooping (boolean isLooping) {
		music.setLooping(isLooping);
	}

	public boolean isPlaying () {
		return music.isPlaying();
	}

	public void stop () {
		music.stop();
	}

	public void pause () {
		music.pause();
	}

	public void play () {
		music.play();
	}
}
