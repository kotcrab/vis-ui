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

package com.kotcrab.vis.editor.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.util.gdx.DummyMusic;
import com.kotcrab.vis.runtime.component.MusicComponent;
import com.kotcrab.vis.runtime.util.annotation.DeprecatedOn;

/**
 * Kryo serializer for {@link MusicComponent}
 * @author Kotcrab
 */
@Deprecated @DeprecatedOn(versionCode = 20)
public class MusicComponentSerializer extends EntityComponentSerializer<MusicComponent> {
	private static final int VERSION_CODE = 1;

	public MusicComponentSerializer (Kryo kryo) {
		super(kryo, MusicComponent.class);
	}

	@Override
	public void write (Kryo kryo, Output output, MusicComponent musicObj) {
		super.write(kryo, output, musicObj);

		output.writeInt(VERSION_CODE);

		output.writeBoolean(musicObj.isPlayOnStart());
		output.writeBoolean(musicObj.isLooping());
		output.writeFloat(musicObj.getVolume());
	}

	@Override
	public MusicComponent read (Kryo kryo, Input input, Class<MusicComponent> type) {
		super.read(kryo, input, type);

		input.readInt(); //version code

		MusicComponent component = new MusicComponent(new DummyMusic());
		component.setPlayOnStart(input.readBoolean());
		component.setLooping(input.readBoolean());
		component.setVolume(input.readFloat());

		return component;
	}

	@Override
	public MusicComponent copy (Kryo kryo, MusicComponent original) {
		super.copy(kryo, original);
		return new MusicComponent(original.music);
	}
}
