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

package com.kotcrab.vis.editor.util.gdx;

import com.badlogic.gdx.audio.Music;

/**
 * Dummy music is used to avoid loading actual file but still provide options to store music properties
 * @author Kotcrab
 */
public class DummyMusic implements Music {
	private boolean playing;
	private boolean looping;
	private float pan;
	private float volume;
	private float positon;

	@Override
	public void play () {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pause () {
		throw new UnsupportedOperationException();
	}

	@Override
	public void stop () {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPlaying () {
		return playing;
	}

	@Override
	public void setLooping (boolean isLooping) {
		this.looping = isLooping;
	}

	@Override
	public boolean isLooping () {
		return looping;
	}

	@Override
	public void setVolume (float volume) {
		this.volume = volume;
	}

	@Override
	public float getVolume () {
		return volume;
	}

	@Override
	public void setPan (float pan, float volume) {
		this.pan = pan;
		this.volume = volume;
	}

	@Override
	public void setPosition (float position) {
		this.positon = position;
	}

	@Override
	public float getPosition () {
		return positon;
	}

	@Override
	public void dispose () {

	}

	@Override
	public void setOnCompletionListener (OnCompletionListener listener) {
		throw new UnsupportedOperationException();
	}
}
