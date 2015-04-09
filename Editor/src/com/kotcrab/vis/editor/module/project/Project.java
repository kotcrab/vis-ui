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

package com.kotcrab.vis.editor.module.project;

public class Project {
	public Type type;

	/** Root of project, for LibGDX type this is root of Gradle folder, for generic this is same as assets folder */
	public String root;
	/** Assets export directory, for LibGDX this is usually gradle_root/android/assets, for generic this is directory provided by user */
	public String assets;

	public Project (Type type) {
		this.type = type;
	}

	public enum Type {LibGDX, Generic}
}
