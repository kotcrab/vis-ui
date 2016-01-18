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

package com.kotcrab.vis.ui.widget.tabbedpane;

/**
 * Listener used to get events from {@link TabbedPane}.
 * @author Kotcrab
 */
public interface TabbedPaneListener {
	/**
	 * Called when TabbedPane switched to new tab.
	 * @param tab that TabbedPane switched to. May be null if all tabs were disabled or if {@link TabbedPane#setAllowTabDeselect(boolean)} was set to
	 * true and all tabs were deselected.
	 */
	void switchedTab (Tab tab);

	/**
	 * Called when Tab was removed TabbedPane.
	 * @param tab that was removed.
	 */
	void removedTab (Tab tab);

	/** Called when all tabs were removed from TabbedPane. */
	void removedAllTabs ();
}
