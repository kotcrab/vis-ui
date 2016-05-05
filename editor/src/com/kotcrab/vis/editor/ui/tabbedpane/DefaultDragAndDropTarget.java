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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/** @author Kotcrab */
public class DefaultDragAndDropTarget implements DragAndDropTarget {
	private DragAndDrop.Target dummyTarget;

	public DefaultDragAndDropTarget () {
		dummyTarget = new DragAndDrop.Target(new Actor()) {
			@Override
			public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				return false;
			}

			@Override
			public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

			}
		};
	}

	@Override
	public float getCameraZoom () {
		return 1;
	}

	@Override
	public DragAndDrop.Target getDropTarget () {
		return dummyTarget;
	}

	@Override
	public float getPixelsPerUnit () {
		return 100;
	}
}
