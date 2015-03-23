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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.kotcrab.vis.ui.i18n.BundleText;

import java.util.Locale;

enum FileChooserText implements BundleText {
	// @formatter:off
	TITLE_CHOOSE_FILES 					{public String getName () {return "titleChooseFiles";}},
	TITLE_CHOOSE_DIRECTORIES			{public String getName () {return "titleChooseDirectories";}},
	TITLE_CHOOSE_FILES_AND_DIRECTORIES	{public String getName () {return "titleChooseFilesAndDirectories";}},

	CANCEL 								{public String getName () {return "cancel";}},
	FILE_NAME 							{public String getName () {return "fileName";}},
	DESKTOP 							{public String getName () {return "desktop";}},

	OPEN 								{public String getName () {return "open";}},
	SAVE 								{public String getName () {return "save";}},

	POPUP_TITLE 						{public String getName () {return "popupTitle";}},
	POPUP_CHOOSE_FILE 					{public String getName () {return "popupChooseFile";}},
	POPUP_DIRECTORY_DOES_NOT_EXIST 		{public String getName () {return "popupDirectoryDoesNotExist";}},
	POPUP_ONLY_DIRECTORIES 				{public String getName () {return "popupOnlyDirectories";}},
	POPUP_FILENAME_INVALID 				{public String getName () {return "popupFilenameInvalid";}},
	POPUP_FILE_EXIST_OVERWRITE 			{public String getName () {return "popupFileExistOverwrite";}},
	POPUP_MULTIPLE_FILE_EXIST_OVERWRITE {public String getName () {return "popupMultipleFileExistOverwrite";}},
	POPUP_YES 							{public String getName () {return "popupYes";}},
	POPUP_NO 							{public String getName () {return "popupNo";}},
	POPUP_OK 							{public String getName () {return "popupOK";}},

	CONTEXT_MENU_DELETE 				{public String getName () {return "contextMenuDelete";}},
	CONTEXT_MENU_SHOW_IN_EXPLORER		{public String getName () {return "contextMenuShowInExplorer";}},
	CONTEXT_MENU_ADD_TO_FAVORITES 		{public String getName () {return "contextMenuAddToFavorites";}},
	CONTEXT_MENU_REMOVE_FROM_FAVORITES 	{public String getName () {return "contextMenuRemoveFromFavorites";}},
	CONTEXT_MENU_DELETE_WARNING 		{public String getName () {return "contextMenuDeleteWarning";}};
	// @formatter:on

	@Override
	public String get () {
		throw new UnsupportedOperationException();
	}

	@Override
	public String format () {
		throw new UnsupportedOperationException();
	}

	@Override
	public String format (Object... arguments) {
		throw new UnsupportedOperationException();
	}
}

/** @author Kotcrab */
public class FileChooserLocale {
	private I18NBundle bundle;

	public FileChooserLocale () {
		FileHandle file = Gdx.files.classpath("com/kotcrab/vis/ui/i18n/FileChooser");
		Locale locale = new Locale("en");
		bundle = I18NBundle.createBundle(file, locale);
	}

	public FileChooserLocale (I18NBundle bundle) {
		this.bundle = bundle;
	}

	public String get (BundleText text) {
		return bundle.get(text.getName());
	}
}

