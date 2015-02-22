/*
 * Copyright 2014-2015 Pawel Pastuszak
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

public class FileChooserLocale {
	public String titleChooseFiles = "Choose file";
	public String titleChooseDirectories = "Choose directory";
	public String titleChooseFilesAndDirectories = "Choose directory or file";

	public String cancel = "Cancel";
	public String fileName = "File name:";
	public String desktop = "Desktop";

	/** Used on confirm button when dialog is in OPEN mode */
	public String open = "Open";
	/** Used on confirm button when dialog is in SAVE mode */
	public String save = "Save";

	public String popupTitle = "Message";
	public String popupChooseFile = "You must choose a file!";
	public String popupDirectoryDoesNotExist = "This directory does not exist!";
	public String popupOnlyDirectoreis = "Only directories are allowed!";
	public String popupFilenameInvalid = "File name is invalid!";
	public String popupFileExistOverwrite = "This file already exist, do you want to overwrite it?";
	public String popupMutipleFileExistOverwrite = "Those files already exist, do you want to overwrite them?";
	public String popupYes = "Yes";
	public String popupNo = "No";
	public String popupOK = "OK";

	public String contextMenuDelete = "Delete";
	public String contextMenuShowInExplorer = "Show in Explorer";
	public String contextMenuAddToFavorites = "Add To Favorites";
	public String contextMenuRemoveFromFavorites = "Remove From Favorites";
	public String contextMenuDeleteWarning = "This file will be deleted permanently? Are you sure?";

	public FileChooserLocale () {
	}

	public FileChooserLocale (String title, String cancel, String open, String save) {
		this(cancel, open, save);
		this.titleChooseFiles = title;
		this.titleChooseDirectories = title;
		this.titleChooseFilesAndDirectories = title;
	}

	public FileChooserLocale (String cancel, String open, String save) {
		this.cancel = cancel;
		this.open = open;
		this.save = save;
	}
}
