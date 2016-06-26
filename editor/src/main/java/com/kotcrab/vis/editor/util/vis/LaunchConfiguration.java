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

package com.kotcrab.vis.editor.util.vis;

/**
 * Contains all parameters that can be passed to VisEditor when launching from command line
 * @author Kotcrab
 */
public class LaunchConfiguration {
	/** If true ui scaling mode was enabled from command line arguments, this may be false if ui scaling is enabled from settings */
	public boolean scaleUIEnabled = false;

	/** Project that will be loaded after editor startup */
	public String projectPath;
	/** Scene that will be loaded after editor startup, only used if {@link #projectPath was set} */
	public String scenePath;

	public void verify () {
		if (scenePath != null && projectPath == null) {
			throw new IllegalStateException("--scene cannot be used without specifying project using --project <project path>");
		}
	}
}
