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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.VisUI;

/**
 * Tooltips are widgets that appear bellow other widget after user hovers mouse pointer on that other widget.
 * @author Kotcrab
 * @since 0.5.0
 */
public class Tooltip extends VisTable {
	private static final Drawable BACKGROUND = VisUI.getSkin().getDrawable("tooltip-bg");
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

		setBackground(BACKGROUND);

		contentCell = add(content).padLeft(3).padRight(3).padBottom(2);
		pack();

		attach();
	}

	/**
	 * Attaches tooltip to current target, must be called if tooltip listener was removed from target (for example by
	 * calling target.clearListeners() )
	 */
	public void attach () {
		Array<EventListener> listeners = target.getListeners();
		for (EventListener listener : listeners)
			if (listener instanceof TooltipInputListener)
				throw new IllegalStateException("More than one tooltip cannot be added to the same target!");

		target.addListener(listener);
	}

	/**
	 * Deatches tooltip form current target, does not change tooltip target meaning that this tooltip can be reatched to
	 * same target by calling {@link Tooltip#attach()}
	 */
	public void detach () {
		target.removeListener(listener);
	}

	/** Sets new target for this tooltip, tooltip will be automatically detached from old target. */
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

	private void keepWithinStage () {
		Stage stage = getStage();
		Camera camera = stage.getCamera();
		if (camera instanceof OrthographicCamera) {
			OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
			float parentWidth = stage.getWidth();
			float parentHeight = stage.getHeight();
			if (getX(Align.right) - camera.position.x > parentWidth / 2 / orthographicCamera.zoom)
				setPosition(camera.position.x + parentWidth / 2 / orthographicCamera.zoom, getY(Align.right), Align.right);
			if (getX(Align.left) - camera.position.x < -parentWidth / 2 / orthographicCamera.zoom)
				setPosition(camera.position.x - parentWidth / 2 / orthographicCamera.zoom, getY(Align.left), Align.left);
			if (getY(Align.top) - camera.position.y > parentHeight / 2 / orthographicCamera.zoom)
				setPosition(getX(Align.top), camera.position.y + parentHeight / 2 / orthographicCamera.zoom, Align.top);
			if (getY(Align.bottom) - camera.position.y < -parentHeight / 2 / orthographicCamera.zoom)
				setPosition(getX(Align.bottom), camera.position.y - parentHeight / 2 / orthographicCamera.zoom, Align.bottom);
		} else if (getParent() == stage.getRoot()) {
			float parentWidth = stage.getWidth();
			float parentHeight = stage.getHeight();
			if (getX() < 0) setX(0);
			if (getRight() > parentWidth) setX(parentWidth - getWidth());
			if (getY() < 0) setY(0);
			if (getTop() > parentHeight) setY(parentHeight - getHeight());
		}
	}

	private class DisplayTask extends Task {
		@Override
		public void run () {
			target.getStage().addActor(fadeIn());
			keepWithinStage();
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
