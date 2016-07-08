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

package com.kotcrab.vis.runtime.assets;

/**
 * References path to audio resource loaded as music file (it is streamed from file location instead of being loaded into memory
 * which for music may require big amount of memory)
 * @author Kotcrab
 */
public class MusicAsset extends PathAsset {
	@Deprecated
	public MusicAsset () {
	}

	public MusicAsset (String relativePath) {
		super(relativePath);
	}
}
