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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.utils.I18NBundle;
import com.kotcrab.vis.ui.Locales;
import com.kotcrab.vis.ui.i18n.BundleText;

/**
 * Contains texts for chooser access via I18NBundle.
 * @author Kotcrab
 * @since 0.7.0
 */
public enum FileChooserText implements BundleText {
	TITLE_CHOOSE_FILES("titleChooseFiles"),
	TITLE_CHOOSE_DIRECTORIES("titleChooseDirectories"),
	TITLE_CHOOSE_FILES_AND_DIRECTORIES("titleChooseFilesAndDirectories"),

	CANCEL("cancel"),
	FILE_NAME("fileName"),
	FILE_TYPE("fileType"),
	ALL_FILES("allFiles"),
	DESKTOP("desktop"),
	COMPUTER("computer"),

	OPEN("open"),
	SAVE("save"),

	BACK("back"),
	FORWARD("forward"),
	PARENT_DIRECTORY("parentDirectory"),
	NEW_DIRECTORY("newDirectory"),

	DIRECTORY_NO_LONGER_EXISTS("directoryNoLongerExists"),

	POPUP_TITLE("popupTitle"),
	POPUP_CHOOSE_FILE("popupChooseFile"),
	POPUP_SELECTED_FILE_DOES_NOT_EXIST("popupSelectedFileDoesNotExist"),
	POPUP_DIRECTORY_DOES_NOT_EXIST("popupDirectoryDoesNotExist"),
	POPUP_ONLY_DIRECTORIES("popupOnlyDirectories"),
	POPUP_FILENAME_INVALID("popupFilenameInvalid"),
	POPUP_FILE_EXIST_OVERWRITE("popupFileExistOverwrite"),
	POPUP_MULTIPLE_FILE_EXIST_OVERWRITE("popupMultipleFileExistOverwrite"),
	POPUP_DELETE_FILE_FAILED("popupDeleteFileFailed"),

	CONTEXT_MENU_DELETE("contextMenuDelete"),
	CONTEXT_MENU_MOVE_TO_TRASH("contextMenuMoveToTrash"),
	CONTEXT_MENU_SHOW_IN_EXPLORER("contextMenuShowInExplorer"),
	CONTEXT_MENU_REFRESH("contextMenuRefresh"),
	CONTEXT_MENU_ADD_TO_FAVORITES("contextMenuAddToFavorites"),
	CONTEXT_MENU_REMOVE_FROM_FAVORITES("contextMenuRemoveFromFavorites"),
	CONTEXT_MENU_DELETE_WARNING("contextMenuDeleteWarning"),
	CONTEXT_MENU_MOVE_TO_TRASH_WARNING("contextMenuMoveToTrashWarning"),
	CONTEXT_MENU_NEW_DIRECTORY("contextMenuNewDirectory"),
	CONTEXT_MENU_SORT_BY("contextMenuSortBy"),

	SORT_BY_NAME("sortByName"),
	SORT_BY_DATE("sortByDate"),
	SORT_BY_SIZE("sortBySize"),
	SORT_BY_ASCENDING("sortByAscending"),
	SORT_BY_DESCENDING("sortByDescending"),

	NEW_DIRECTORY_DIALOG_TITLE("newDirectoryDialogTitle"),
	NEW_DIRECTORY_DIALOG_TEXT("newDirectoryDialogText"),
	NEW_DIRECTORY_DIALOG_ILLEGAL_CHARACTERS("newDirectoryDialogIllegalCharacters"),
	NEW_DIRECTORY_DIALOG_ALREADY_EXISTS("newDirectoryDialogAlreadyExists"),

	CHANGE_VIEW_MODE("changeViewMode"),
	VIEW_MODE_LIST("viewModeList"),
	VIEW_MODE_DETAILS("viewModeDetails"),
	VIEW_MODE_BIG_ICONS("viewModeBigIcons"),
	VIEW_MODE_MEDIUM_ICONS("viewModeMediumIcons"),
	VIEW_MODE_SMALL_ICONS("viewModeSmallIcons");

	private final String name;

	FileChooserText (final String name) {
		this.name = name;
	}

	private static I18NBundle getBundle () {
		return Locales.getFileChooserBundle();
	}

	@Override
	public final String getName () {
		return name;
	}

	@Override
	public final String get () {
		return getBundle().get(name);
	}

	@Override
	public final String format () {
		return getBundle().format(name);
	}

	@Override
	public final String format (final Object... arguments) {
		return getBundle().format(name, arguments);
	}

	@Override
	public final String toString () {
		return get();
	}
}
