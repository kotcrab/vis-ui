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

import com.kotcrab.vis.editor.module.editor.QuickAccessModule;

/**
 * Possible view modes of {@link MainContentTab}. This is likely to be removed when better view management is done.
 * @author Kotcrab
 */
public enum TabViewMode {
	/** Only the tab itself is displayed without any other tab. */
	TAB_ONLY,
	/** Screen is spited, in this mode one tab of {@link QuickAccessModule} can be displayed. */
	SPLIT
}
