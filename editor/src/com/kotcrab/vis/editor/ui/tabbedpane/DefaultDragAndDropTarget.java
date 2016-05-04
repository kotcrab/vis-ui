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
