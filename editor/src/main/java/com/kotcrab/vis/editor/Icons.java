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

package com.kotcrab.vis.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;

/**
 * VisEditor built-in icons enum.
 * @author Kotcrab
 */
public enum Icons {
	NEW("new"),
	UNDO("undo"),
	REDO("redo"),
	SETTINGS("settings"),
	SETTINGS_VIEW("settings-view"),
	EXPORT("export"),
	IMPORT("import"),
	LOAD("load"),
	SAVE("save"),
	GLOBE("globe"),
	INFO("info"),
	EXIT("exit"),
	FOLDER_OPEN("folder-open"),
	SEARCH("search"),
	MORE("more"),

	WARNING("warning"),
	LAYER_ADD("layer-add"),
	LAYER_REMOVE("layer-remove"),
	LAYER_UP("layer-up"),
	LAYER_DOWN("layer-down"),
	EYE("eye"),
	EYE_DISABLED("eye-disabled"),
	LOCKED("locked"),
	UNLOCKED("unlocked"),
	ALIGN_LEFT("align-left"),
	ALIGN_RIGHT("align-right"),
	ALIGN_BOTTOM("align-bottom"),
	ALIGN_TOP("align-top"),
	ALIGN_CENTER_X("align-center-x"),
	ALIGN_CENTER_Y("align-center-y"),
	CURSOR("cursor"),
	POLYGON("polygon"),
	PLUS("plus"),
	PROGRESS("progress"),
	CHECK("check"),

	TOOL_MOVE("tool-move"),
	TOOL_ROTATE("tool-rotate"),
	TOOL_SCALE("tool-scale"),

	FOLDER_MEDIUM("folder-medium"),
	FOLDER_SOUND_MEDIUM("folder-sound-medium"),
	FOLDER_MUSIC_MEDIUM("folder-music-medium"),

	QUESTION_BIG("question-big"),
	SOUND_BIG("sound-big"),
	MUSIC_BIG("music-big"),
	POINT_BIG("point-big"),
	PARTICLE_BIG("particle-big"),

	FOLDER_NEW("icon-folder-new", true),
	FOLDER_PARENT("icon-folder-parent", true),
	CLOSE("icon-close", true),
	FOLDER("icon-folder", true),
	ARROW_LEFT("icon-arrow-left", true),
	ARROW_RIGHT("icon-arrow-right", true);

	private final String name;
	private final boolean fromVisUI;

	Icons (String name) {
		this.name = name;
		fromVisUI = false;
	}

	Icons (String name, boolean fromVisUI) {
		this.name = name;
		this.fromVisUI = fromVisUI;
	}

	public Drawable drawable () {
		if (fromVisUI)
			return VisUI.getSkin().getDrawable(name);
		else
			return new TextureRegionDrawable(textureRegion());
	}

	public TextureRegion textureRegion () {
		if (fromVisUI)
			return VisUI.getSkin().getRegion(name);
		else
			return Assets.getIconRegion(name);
	}
}
