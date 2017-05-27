/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.file;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.PopupMenu.PopupMenuStyle;

/** @author Kotcrab */
public class FileChooserStyle {
	public PopupMenuStyle popupMenuStyle;

	public Drawable highlight;
	public Drawable iconArrowLeft;
	public Drawable iconArrowRight;
	public Drawable iconFolder;
	public Drawable iconFolderParent;
	public Drawable iconFolderStar;
	public Drawable iconFolderNew;
	public Drawable iconDrive;
	public Drawable iconTrash;
	public Drawable iconStar;
	public Drawable iconStarOutline;
	public Drawable iconRefresh;
	public Drawable iconListSettings;

	public Drawable iconFileText;
	public Drawable iconFileImage;
	public Drawable iconFilePdf;
	public Drawable iconFileAudio;

	public Drawable contextMenuSelectedItem;
	public Drawable expandDropdown;

	public FileChooserStyle () {
	}

	public FileChooserStyle (FileChooserStyle style) {
		this.popupMenuStyle = style.popupMenuStyle;
		this.highlight = style.highlight;
		this.iconArrowLeft = style.iconArrowLeft;
		this.iconArrowRight = style.iconArrowRight;
		this.iconFolder = style.iconFolder;
		this.iconFolderParent = style.iconFolderParent;
		this.iconFolderStar = style.iconFolderStar;
		this.iconFolderNew = style.iconFolderNew;
		this.iconDrive = style.iconDrive;
		this.iconTrash = style.iconTrash;
		this.iconStar = style.iconStar;
		this.iconStarOutline = style.iconStarOutline;
		this.iconRefresh = style.iconRefresh;
		this.iconListSettings = style.iconListSettings;
		this.iconFileText = style.iconFileText;
		this.iconFileImage = style.iconFileImage;
		this.iconFilePdf = style.iconFilePdf;
		this.iconFileAudio = style.iconFileAudio;
		this.contextMenuSelectedItem = style.contextMenuSelectedItem;
		this.expandDropdown = style.expandDropdown;
	}
}
