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

import com.badlogic.gdx.utils.Disposable;

/**
 * Implemented by plugin classes that required some resource loading. Note that your resources will be loaded on editor
 * startup on will be stored until exit, so you should only load must have resources. Contact Kotcrab if you need
 * more specialized loader.
 * <p>
 * Single loader can load multiple resources.
 * @author Kotcrab
 */
public interface ResourceLoader extends Disposable {
	/** Called when you should load your resources. This will be called from OpenGl context. */
	void load ();

	/** Name of this resource bundle that will be used for logging or bundle identification. eg. MyPluginAssets */
	String getName ();
}
