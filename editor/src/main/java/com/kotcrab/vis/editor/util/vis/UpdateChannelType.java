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

package com.kotcrab.vis.editor.util.vis;

import com.kotcrab.vis.runtime.util.PrettyEnum;
import com.kotcrab.vis.runtime.util.annotation.DeprecatedOn;

/**
 * Possible VisEditor update channels.
 * @author Kotcrab
 */
public enum UpdateChannelType implements PrettyEnum {
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
	@Deprecated @DeprecatedOn(versionCode = 20)
	BETA {
		@Override
		public String toPrettyString () {
			throw new IllegalStateException("Beta update channel is no longer supported");
		}

		@Override
		public String getStorageURL () {
			throw new IllegalStateException("Beta update channel is no longer supported");
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
	};

	public abstract String getStorageURL ();
}
