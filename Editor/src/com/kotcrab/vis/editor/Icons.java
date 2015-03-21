/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor;

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
	public String getIconName ();
}
