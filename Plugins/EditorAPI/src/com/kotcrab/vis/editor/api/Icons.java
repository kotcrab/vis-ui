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

package com.kotcrab.vis.editor.api;

public enum Icons implements IconAsset {
	NEW {
		public String getIconName () {
			return "new";
		}
	},
	UNDO {
		public String getIconName () {
			return "undo";
		}
	},
	REDO {
		public String getIconName () {
			return "redo";
		}
	},
	SETTINGS {
		public String getIconName () {
			return "settings";
		}
	},
	SETTINGS_VIEW {
		public String getIconName () {
			return "settings-view";
		}
	},
	EXPORT {
		public String getIconName () {
			return "export";
		}
	},
	IMPORT {
		public String getIconName () {
			return "import";
		}
	},
	LOAD {
		public String getIconName () {
			return "load";
		}
	},
	SAVE {
		public String getIconName () {
			return "save";
		}
	},
	GLOBE {
		public String getIconName () {
			return "globe";
		}
	},
	INFO {
		public String getIconName () {
			return "info";
		}
	},
	EXIT {
		public String getIconName () {
			return "exit";
		}
	},
	FOLDER_OPEN {
		public String getIconName () {
			return "folder-open";
		}
	},
	SEARCH {
		public String getIconName () {
			return "search";
		}
	},
	QUESTION {
		public String getIconName () {
			return "question";
		}
	},
	MORE {
		public String getIconName () {
			return "more";
		}
	},
	SOUND {
		public String getIconName () {
			return "sound";
		}
	},
	MUSIC {
		public String getIconName () {
			return "music";
		}
	}
}

interface IconAsset {
	String getIconName ();
}
