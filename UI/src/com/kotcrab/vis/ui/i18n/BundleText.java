/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.ui.i18n;

/**
 * A simple interface for one text line of the bundle file.
 * @author MJ
 */
public interface BundleText {

	/** @return name of the bundle text in the bundle file. */
	public String getName ();

	/** @return text's unformatted message as it appears in the bundle. */
	public String get ();

	/** @return text's formatted message without any arguments. */
	public String format ();

	/** @return text's formatted message with the passes arguments filling bundle placeholders. */
	public String format (Object... arguments);
}
