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

package com.kotcrab.vis.editor.event;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Posted when scene should reload various types of resources
 * @author Kotcrab
 */
public class ResourceReloadedEvent {
	public enum ResourceType {
		TEXTURES, TEXTURE_ATLASES, PARTICLES, BMP_FONTS, TTF_FONTS, SHADERS;
	}

	public final Set<ResourceType> resourceTypes;

	public ResourceReloadedEvent (Set<ResourceType> resourceTypes) {
		this.resourceTypes = Sets.immutableEnumSet(resourceTypes);
	}
}
