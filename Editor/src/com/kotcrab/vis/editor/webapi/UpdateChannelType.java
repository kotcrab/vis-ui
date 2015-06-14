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

package com.kotcrab.vis.editor.webapi;

import com.kotcrab.vis.runtime.util.PrettyEnum;

public enum UpdateChannelType implements PrettyEnum, ReleaseInformationProvider {
	STABLE {
		@Override
		public String toPrettyString () {
			return "Stable";
		}

		@Override
		public String getStorageURL () {
			return "http://dl.kotcrab.com/vis/editor/stable/";
		}
	},
	BETA {
		@Override
		public String toPrettyString () {
			return "Beta";
		}

		@Override
		public String getStorageURL () {
			return "http://dl.kotcrab.com/vis/editor/beta/";
		}
	},
	EDGE {
		@Override
		public String toPrettyString () {
			return "Cutting Edge";
		}

		@Override
		public String getStorageURL () {
			return "http://dl.kotcrab.com/vis/editor/edge/";
		}
	}
}
