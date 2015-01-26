/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;

public class Tooltip extends VisTable {
	private static final Drawable background = VisUI.skin.getDrawable("tooltip-bg");
	private static final float FADE_TIME = 0.3f;
	private static final float APPEAR_DELAY_TIME = 0.6f;
	private static Vector2 tempTargetStagePos;

	private Actor target;

	private DisplayTask displayTask;

	public Tooltip (Actor target, Actor actor) {
		super(true);
		this.target = target;

		displayTask = new DisplayTask();

		setBackground(background);

		add(actor).padLeft(3).padRight(3).padBottom(2);

		pack();
	}

	public static Tooltip updateTooltip (Actor target, Tooltip currentTooltip, Actor actor) {
		if (actor == null) {
			if (currentTooltip != null) target.removeListener(currentTooltip.getListener());
			return null;
		}

		Tooltip tooltip = new Tooltip(target, actor);
		target.addListener(tooltip.getListener());
		return tooltip;
	}

	public static Tooltip updateTooltip (Actor target, Tooltip currentTooltip, String text) {
		VisLabel label = new VisLabel(text);
		label.setAlignment(Align.center);
		return updateTooltip(target, currentTooltip, label);
	}

	public void fadeOut () {
		clearActions();
		addAction(Actions.sequence(Actions.fadeOut(FADE_TIME, Interpolation.fade), Actions.removeActor()));
	}

	public Table fadeIn () {
		clearActions();
		setColor(1, 1, 1, 0);
		addAction(Actions.sequence(Actions.fadeIn(FADE_TIME, Interpolation.fade)));
		return this;
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition((int) x, (int) y);
	}

	public InputListener getListener () {
		return new InputListener() {
			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				tempTargetStagePos = target.localToStageCoordinates(new Vector2());

				setX(tempTargetStagePos.x + (target.getWidth() - getWidth()) / 2);

				float tooltipY = tempTargetStagePos.y - getHeight() - 10;
				float stageHeight = target.getStage().getHeight();
				if (stageHeight - tooltipY > stageHeight) //is there enough space to display bellow widget
					setY(tempTargetStagePos.y + target.getHeight() + 10); //display above widget
				else
					setY(tooltipY); //display bellow

				displayTask.cancel();
				Timer.schedule(displayTask, APPEAR_DELAY_TIME);
			}

			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				displayTask.cancel();
				fadeOut();
			}

			@Override
			public boolean mouseMoved (InputEvent event, float x, float y) {
				if (isVisible() && getActions().size == 0)
					fadeOut();

				return false;
			}
		};
	}

	private class DisplayTask extends Task {
		@Override
		public void run () {
			target.getStage().addActor(fadeIn());
		}
	}
}
