/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.internal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.CursorManager;

/**
 * Manages setting custom cursor for split panes.
 * This is VisUI internal class
 * @author Kotcrab
 * @since 1.4.0
 */
public abstract class SplitPaneCursorManager extends ClickListener {
	private Actor owner;
	private boolean vertical;

	private Cursor.SystemCursor currentCursor;

	public SplitPaneCursorManager (Actor owner, boolean vertical) {
		this.owner = owner;
		this.vertical = vertical;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return handleBoundsContains(x, y);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		super.touchDragged(event, x, y, pointer); //handles setting cursor when mouse returned to widget after exiting it while dragged
		if (contains(x, y)) {
			setCustomCursor();
		} else {
			clearCustomCursor();
		}
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		super.mouseMoved(event, x, y);
		if (handleBoundsContains(x, y)) {
			setCustomCursor();
		} else {
			clearCustomCursor();
		}

		return false;
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		super.exit(event, x, y, pointer, toActor);
		if (pointer == -1 && (toActor == null || toActor.isDescendantOf(owner) == false)) {
			clearCustomCursor();
		}
	}

	private void setCustomCursor () {
		Cursor.SystemCursor targetCursor;
		if (vertical) {
			targetCursor = Cursor.SystemCursor.VerticalResize;
		} else {
			targetCursor = Cursor.SystemCursor.HorizontalResize;
		}

		if (currentCursor != targetCursor) {
			Gdx.graphics.setSystemCursor(targetCursor);
			currentCursor = targetCursor;
		}
	}

	private void clearCustomCursor () {
		if (currentCursor != null) {
			CursorManager.restoreDefaultCursor();
			currentCursor = null;
		}
	}

	protected abstract boolean handleBoundsContains (float x, float y);

	protected abstract boolean contains (float x, float y);
}
