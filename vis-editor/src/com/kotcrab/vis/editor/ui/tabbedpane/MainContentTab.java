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

package com.kotcrab.vis.editor.ui.tabbedpane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * All VisEditor tabs must extend this class. Note that tabs managed by {@link QuickAccessModule} does not require this.
 */
public abstract class MainContentTab extends Tab {
	public MainContentTab () {
	}

	public MainContentTab (boolean savable) {
		super(savable);
	}

	public MainContentTab (boolean savable, boolean closeableByUser) {
		super(savable, closeableByUser);
	}

	public void render (Batch batch) {

	}

	public abstract TabViewMode getViewMode ();
}
