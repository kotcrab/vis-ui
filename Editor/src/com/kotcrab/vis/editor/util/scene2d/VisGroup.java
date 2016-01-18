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

package com.kotcrab.vis.editor.util.scene2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;

/**
 * Special VisGroup allow use to detect when modal input was added or removed. This allow use to create input
 * listener that respect window modality. This must be set as {@link Stage#root} group but that field is private final
 * so reflection must be used for that.
 * @see ModalInputListener
 */
public class VisGroup extends Group {
	private Array<Window> modalWindows = new Array<>();

	public VisGroup (Stage stage) {
		setStage(stage);
	}

	@Override
	public void addActor (Actor actor) {
		super.addActor(actor);
		actorAdded(actor);
	}

	@Override
	public void addActorAfter (Actor actorAfter, Actor actor) {
		super.addActorAfter(actorAfter, actor);
		actorAdded(actor);
	}

	@Override
	public void addActorBefore (Actor actorBefore, Actor actor) {
		super.addActorBefore(actorBefore, actor);
		actorAdded(actor);
	}

	@Override
	public void addActorAt (int index, Actor actor) {
		super.addActorAt(index, actor);
		actorAdded(actor);
	}

	@Override
	public boolean removeActor (Actor actor) {
		boolean removed = super.removeActor(actor);
		actorRemoved(actor);
		return removed;
	}

	@Override
	public boolean removeActor (Actor actor, boolean unfocus) {
		boolean removed = super.removeActor(actor, unfocus);
		actorRemoved(actor);
		return removed;
	}

	private void actorAdded (Actor actor) {
		if (actor instanceof Window) {
			Window window = (Window) actor;

			if (window.isModal()) modalWindows.add(window);
		}
	}

	private void actorRemoved (Actor actor) {
		if (actor instanceof Window) {
			Window window = (Window) actor;

			if (window.isModal()) modalWindows.removeValue(window, true);
		}
	}

	public boolean isAnyWindowModal () {
		return modalWindows.size > 0;
	}
}
