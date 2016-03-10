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

package com.kotcrab.vis.editor.module.project.assetsmanager;

/**
 * Used by {@link AssetsUIModule} to display more information about given file. Plugins returns this.
 * @author Kotcrab
 */
public class ContentItemProperties {
	public final String type;
	public final String title;
	public final boolean hideExtension;

	public ContentItemProperties (String type, String title) {
		this(type, title, false);
	}

	public ContentItemProperties (String type, String title, boolean hideExtension) {
		this.type = type;
		this.title = title;
		this.hideExtension = hideExtension;
	}
}
