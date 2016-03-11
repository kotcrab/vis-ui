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

package com.kotcrab.vis.editor.module.scene;

import com.kotcrab.vis.editor.module.project.SceneMetadataModule;

/**
 * Holds single scene metadata. Managed by {@link SceneMetadataModule}
 * @author Kotcrab
 */
public class SceneMetadata {
	public float lastCameraX;
	public float lastCameraY;
	public float lastCameraZoom;

	public SceneMetadata () {
	}

	public SceneMetadata (float lastCameraZoom, float lastCameraY, float lastCameraX) {
		this.lastCameraZoom = lastCameraZoom;
		this.lastCameraY = lastCameraY;
		this.lastCameraX = lastCameraX;
	}
}
