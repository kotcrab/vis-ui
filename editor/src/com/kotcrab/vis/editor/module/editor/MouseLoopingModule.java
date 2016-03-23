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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.util.OsUtils;

/**
 * Allows to catch mouse inside current screen. Used for example when dragging objects.
 * @author Kotcrab
 * @see AWTMouseLoopingModule
 */
public abstract class MouseLoopingModule extends EditorModule {
	/** @return true if implementation can support mouse looping, false otherwise */
	public abstract boolean isLoopingSupported ();

	/**
	 * Enables cursor looping. Looping will be auto disabled after touchUp event. This should be called only once for
	 * single touchDown event. May be called after touchDown event already occurred. If implementation does not support
	 * looping no action is performed.
	 */
	public abstract void loopCursor ();

	public abstract float getVirtualMouseX ();

	public abstract float getVirtualMouseY ();

	/** @return true if mouse cords are on virtual screen (was looped at least once in any direction) */
	public abstract boolean isOnVirtualScreen ();

	/**
	 * Creates and returns new instance of {@link MouseLoopingModule}. Returned instance may not support looping
	 * module if platform does not support it, see {@link #isLoopingSupported()}.
	 */
	public static MouseLoopingModule newInstance () {
		if (OsUtils.isMac()) return new DefaultMouseLoopingModule();

		return new AWTMouseLoopingModule();
	}

	private static class DefaultMouseLoopingModule extends MouseLoopingModule {
		@Override
		public boolean isLoopingSupported () {
			return false;
		}

		@Override
		public void loopCursor () {

		}

		@Override
		public float getVirtualMouseX () {
			return Gdx.input.getX();
		}

		@Override
		public float getVirtualMouseY () {
			return Gdx.input.getY();
		}

		@Override
		public boolean isOnVirtualScreen () {
			return false;
		}
	}
}
