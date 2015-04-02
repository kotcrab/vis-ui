/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.ui.widget.VisLabel;

public class VisDropSource extends Source {
	private final DragAndDrop dragAndDrop;
	private EditorObject object;

	private boolean disposeOnNullTarget;

	private String viewText;
	private EditorObjectProvider provider;

	public VisDropSource (DragAndDrop dragAndDrop, Actor actor) {
		super(actor);
		this.dragAndDrop = dragAndDrop;
	}

	public VisDropSource defaultView (String text) {
		this.viewText = text;
		return this;
	}

	public VisDropSource setObjectProvider (EditorObjectProvider provider) {
		this.provider = provider;
		return this;
	}

	public VisDropSource disposeOnNullTarget () {
		disposeOnNullTarget = true;
		return this;
	}

	@Override
	public Payload dragStart (InputEvent event, float x, float y, int pointer) {
		Payload payload = new Payload();

		object = provider.newInstance();
		payload.setObject(object);

		Label label = new VisLabel(viewText);
		label.setAlignment(Align.center);
		payload.setDragActor(label);

		dragAndDrop.setDragActorPosition(-label.getWidth() / 2, label.getHeight() / 2);

		return payload;
	}

	@Override
	public void dragStop (InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
		if (target == null && disposeOnNullTarget)
			object.dispose();
	}
}
