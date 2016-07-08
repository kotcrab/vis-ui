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
 * References path to audio resource loaded as sound (loaded into memory instead of being streamed from file location
 * like {@link MusicAsset}.
 * @author Kotcrab
 */
public class SoundAsset extends PathAsset {
	@Deprecated
	public SoundAsset () {
	}

	public SoundAsset (String relativePath) {
		super(relativePath);
	}
}
