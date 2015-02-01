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

import com.kotcrab.vis.editor.event.EventBus;
import com.kotcrab.vis.editor.util.Log;

public class App {
	public static final int VERSION_CODE = 2;
	public static final String VERSION = "0.0.2-SNAPSHOT";
	public static final boolean SNAPSHOT = VERSION.contains("SNAPSHOT");
	public static final boolean ERROR_REPORTS = true;

	public static EventBus eventBus;

	public static void init () {
		Log.init();

		eventBus = new EventBus();

		if (ERROR_REPORTS == false)
			Log.w("App", "Error reports are disabled!");
	}
}
