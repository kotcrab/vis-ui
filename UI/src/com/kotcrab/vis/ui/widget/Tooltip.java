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
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;

public class Tooltip extends VisTable {
	private static final Drawable background = VisUI.skin.getDrawable("tooltip-bg");
	private static final float FADE_TIME = 0.3f;
	private static final float APPEAR_DELAY_TIME = 0.6f;

	private Actor target;
	private Actor content;
	private Cell<Actor> contentCell;

	private TooltipInputListener listener;

	private DisplayTask displayTask;

	public Tooltip (Actor target, String text) {
		super(true);
		VisLabel label = new VisLabel(text);
		label.setAlignment(Align.center);
		init(target, label);
	}

	public Tooltip (Actor target, Actor content) {
		super(true);
		init(target, content);
	}

	/**
	 * Remove any attached tooltip from target actor
	 * @param target that tooltips will be removed
	 */
	public static void removeTooltip (Actor target) {
		Array<EventListener> listeners = target.getListeners();
		for (EventListener listener : listeners)
			if (listener instanceof TooltipInputListener) target.removeListener(listener);
	}

	private void init (Actor target, Actor content) {
		this.target = target;
		this.content = content;
		this.listener = new TooltipInputListener();
		this.displayTask = new DisplayTask();

		setBackground(background);

		contentCell = add(content).padLeft(3).padRight(3).padBottom(2);
		pack();

		attach();
	}

	private void attach () {
		Array<EventListener> listeners = target.getListeners();
		for (EventListener listener : listeners)
			if (listener instanceof TooltipInputListener)
				throw new IllegalStateException("More than one tooltip cannot be added to same target!");

		target.addListener(listener);
	}

	public void detach () {
		target.removeListener(listener);
	}

	public void setTarget (Actor newTarget) {
		detach();
		target = newTarget;
		attach();
	}

	private void fadeOut () {
		clearActions();
		addAction(Actions.sequence(Actions.fadeOut(FADE_TIME, Interpolation.fade), Actions.removeActor()));
	}

	private Table fadeIn () {
		clearActions();
		setColor(1, 1, 1, 0);
		addAction(Actions.sequence(Actions.fadeIn(FADE_TIME, Interpolation.fade)));
		return this;
	}

	public Actor getContent () {
		return content;
	}

	public void setContent (Actor content) {
		this.content = content;
		contentCell.setActor(content);
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition((int) x, (int) y);
	}

	private class DisplayTask extends Task {
		@Override
		public void run () {
			target.getStage().addActor(fadeIn());
		}
	}

	private class TooltipInputListener extends InputListener {
		@Override
		public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
			Vector2 targetPos = target.localToStageCoordinates(new Vector2());

			setX(targetPos.x + (target.getWidth() - getWidth()) / 2);

			float tooltipY = targetPos.y - getHeight() - 6;
			float stageHeight = target.getStage().getHeight();
			if (stageHeight - tooltipY > stageHeight) //is there enough space to display bellow widget
				setY(targetPos.y + target.getHeight() + 6); //display above widget
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
	}
}
