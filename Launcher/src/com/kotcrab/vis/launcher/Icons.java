package com.kotcrab.vis.launcher;

public enum Icons implements IconAsset {
	EDITOR_ICON {
		@Override
		public String getIconName () {
			return "editor-icon";
		}
	},
	CONTENT {
		@Override
		public String getIconName () {
			return "content";
		}
	},
	VIS_ICON {
		@Override
		public String getIconName () {
			return "vis-icon";
		}
	},
	GITHUB {
		@Override
		public String getIconName () {
			return "github";
		}
	},
	GLOBE {
		@Override
		public String getIconName () {
			return "globe";
		}
	},
	HOME {
		@Override
		public String getIconName () {
			return "home";
		}
	},
	TOOLS {
		@Override
		public String getIconName () {
			return "tools";
		}
	},
	TWITTER {
		@Override
		public String getIconName () {
			return "twitter";
		}
	}
}

interface IconAsset {
	String getIconName ();
}
