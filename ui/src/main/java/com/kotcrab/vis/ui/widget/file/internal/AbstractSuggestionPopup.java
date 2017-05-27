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

import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** @author Kotcrab */
public class AbstractSuggestionPopup extends PopupMenu {
	public static final int MAX_SUGGESTIONS = 10;

	final FileChooser chooser;

	public AbstractSuggestionPopup (FileChooser chooser) {
		super(chooser.getChooserStyle().popupMenuStyle);
		this.chooser = chooser;
	}

	protected MenuItem createMenuItem (String name) {
		MenuItem item = new MenuItem(name);
		item.getImageCell().size(0);
		item.getShortcutCell().space(0).pad(0);
		item.getSubMenuIconCell().size(0).space(0).pad(0);
		return item;
	}
}
