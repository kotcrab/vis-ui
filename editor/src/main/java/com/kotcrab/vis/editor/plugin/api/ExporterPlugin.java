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

package com.kotcrab.vis.editor.plugin.api;

import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;

import java.util.UUID;

/**
 * Implementation of this class will have modules from from {@link ProjectModuleContainer} injected.
 * @author Kotcrab
 */
public interface ExporterPlugin {
	/** Must return the same UUID every time this method is called! Do NOT return UUID.randomUUID() */
	UUID getUUID ();

	/** Called after injecting modules */
	void init (Project project);

	String getName ();

	/** Not used for now */
	boolean isQuickExportSupported ();

	void export (boolean quickExport);

	boolean isSettingsUsed ();

	void showSettings ();
}
