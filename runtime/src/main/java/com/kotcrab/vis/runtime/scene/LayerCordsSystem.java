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

package com.kotcrab.vis.runtime.scene;

import com.kotcrab.vis.runtime.util.PrettyEnum;

/**
 * Defines possible layer coordinates system.
 * @author Kotcrab
 */
public enum LayerCordsSystem implements PrettyEnum {
	/**
	 * Entities on this layer are storing position in world position. They are affected by game world camera position.
	 */
	WORLD {
		@Override
		public String toPrettyString () {
			return "World coordinates";
		}
	},
	/**
	 * Entities on this layer are storing position in screen coordinates, game world camera position is irreverent.
	 */
	SCREEN {
		@Override
		public String toPrettyString () {
			return "Screen coordinates";
		}
	}
}
