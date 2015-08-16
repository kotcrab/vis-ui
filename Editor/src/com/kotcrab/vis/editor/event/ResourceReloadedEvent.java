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

package com.kotcrab.vis.editor.event;

/**
 * Posted when scene should reloed various types of resources
 * @author Kotcrab
 */
public class ResourceReloadedEvent {
	public static final int RESOURCE_TEXTURES = 0x0001;
	public static final int RESOURCE_TEXTURE_ATLASES = 0x0002;
	public static final int RESOURCE_PARTICLES = 0x0004;
	public static final int RESOURCE_BMP_FONTS = 0x0008;
	public static final int RESOURCE_TTF_FONTS = 0x0016;
	public static final int RESOURCE_SHADERS = 0x0032;

	public int resourceType;

	public ResourceReloadedEvent (int resourceType) {
		this.resourceType = resourceType;
	}
}
