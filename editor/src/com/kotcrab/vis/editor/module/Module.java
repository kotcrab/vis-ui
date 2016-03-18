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

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.utils.Disposable;

/**
 * Base class for all VisEditor modules.
 * @author Kotcrab
 */
public abstract class Module implements Disposable {
	public void added () {
	}

	/**
	 * Called when module was removed from container. Modules can be only removed before module container initialization.
	 * So if this is called {@link #init()} and other method won't be ever called. (expect {@link #added()} which was called already)
	 * This is different than {@link #dispose()} which is called when whole module container is disposed in which case
	 * remove won't be called.
	 */
	public void removed () {

	}

	public void init () {
	}

	public void postInit () {

	}

	public void resize () {
	}

	@Override
	public void dispose () {
	}
}
