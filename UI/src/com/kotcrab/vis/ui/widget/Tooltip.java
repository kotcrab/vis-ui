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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ActorUtils;

/**
 * Tooltips are widgets that appear bellow other widget on mouse pointer hover. Each actor can have only one tooltip.
 * <p>
 * LibGDX 1.6.4 introduced it's own systems of tooltips. VisUI tooltips existed before that and are unrelated and
 * incompatible with libGDX tooltips. VisUI tooltips will remain supported.
 * @author Kotcrab
 * @since 0.5.0
 */
public class Tooltip extends VisTable {
	public static final float DEFAULT_FADE_TIME = 0.3f;
	public static final float DEFAULT_APPEAR_DELAY_TIME = 0.6f;

	private Actor target;
	private Actor content;
	private Cell<Actor> contentCell;

	private TooltipInputListener listener;

	private DisplayTask displayTask;

	private float fadeTime = DEFAULT_FADE_TIME;
	private float appearDelayTime = DEFAULT_APPEAR_DELAY_TIME;

	private Tooltip (Builder builder) {
		super(true);

		TooltipStyle style = builder.style;
		if (style == null) style = VisUI.getSkin().get("default", TooltipStyle.class);

		init(style, builder.target, builder.content);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (String text) {
		this("default", null, text, Align.center);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (Actor target, String text) {
		this("default", target, text, Align.center);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (Actor target, String text, int textAlign) {
		this("default", target, text, textAlign);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (String styleName, Actor target, String text) {
		this(styleName, target, text, Align.center);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (String styleName, Actor target, String text, int textAlign) {
		super(true);

		VisLabel label = new VisLabel(text);
		label.setAlignment(textAlign);
		init(VisUI.getSkin().get(styleName, TooltipStyle.class), target, label);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (Actor content) {
		super(true);
		init(VisUI.getSkin().get("default", TooltipStyle.class), null, content);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (Actor target, Actor content) {
		this("default", target, content);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (String styleName, Actor target, Actor content) {
		super(true);

		init(VisUI.getSkin().get(styleName, TooltipStyle.class), target, content);
	}

	@Deprecated
	/** @deprecated use {@link Tooltip.Builder} */
	public Tooltip (Actor target, Actor content, TooltipStyle style) {
		super(true);
		init(style, target, content);
	}

	/**
	 * Remove any attached tooltip from target actor
	 * @param target that tooltips will be removed
	 */
	public static void removeTooltip (Actor target) {
		Array<EventListener> listeners = target.getListeners();
		for (EventListener listener : listeners) {
			if (listener instanceof TooltipInputListener) target.removeListener(listener);
		}
	}

	private void init (TooltipStyle style, Actor target, Actor content) {
		this.target = target;
		this.content = content;
		this.listener = new TooltipInputListener();
		this.displayTask = new DisplayTask();

		setBackground(style.background);

		contentCell = add(content).padLeft(3).padRight(3).padBottom(2);
		pack();

		if (target != null) attach();

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				toFront();
				return true;
			}

			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1) {
					clearActions();
					addAction(Actions.sequence(Actions.fadeIn(fadeTime, Interpolation.fade)));
				}
			}

			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				if (pointer == -1)
					fadeOut();
			}
		});
	}

	/**
	 * Attaches tooltip to current target, must be called if tooltip listener was removed from target (for example by
	 * calling target.clearListeners() )
	 */
	public void attach () {
		if (target == null) return;
		Array<EventListener> listeners = target.getListeners();
		for (EventListener listener : listeners) {
			if (listener instanceof TooltipInputListener) {
				throw new IllegalStateException("More than one tooltip cannot be added to the same target!");
			}
		}

		target.addListener(listener);
	}

	/**
	 * Detaches tooltip form current target, does not change tooltip target meaning that this tooltip can be reatched to
	 * same target by calling {@link Tooltip#attach()}
	 */
	public void detach () {
		if (target == null) return;
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
		addAction(Actions.sequence(Actions.fadeOut(fadeTime, Interpolation.fade), Actions.removeActor()));
	}

	private VisTable fadeIn () {
		clearActions();
		setColor(1, 1, 1, 0);
		addAction(Actions.sequence(Actions.fadeIn(fadeTime, Interpolation.fade)));
		return this;
	}

	public Actor getContent () {
		return content;
	}

	public void setContent (Actor content) {
		this.content = content;
		contentCell.setActor(content);
		pack();
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition((int) x, (int) y);
	}

	public float getAppearDelayTime () {
		return appearDelayTime;
	}

	public void setAppearDelayTime (float appearDelayTime) {
		this.appearDelayTime = appearDelayTime;
	}

	public float getFadeTime () {
		return fadeTime;
	}

	public void setFadeTime (float fadeTime) {
		this.fadeTime = fadeTime;
	}

	private class DisplayTask extends Task {
		@Override
		public void run () {
			target.getStage().addActor(fadeIn());
			ActorUtils.keepWithinStage(getStage(), Tooltip.this);
		}
	}

	private class TooltipInputListener extends InputListener {
		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			displayTask.cancel();
			Tooltip.this.toFront();
			fadeOut();
			return true;
		}

		@Override
		public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
			if (pointer == -1) {
				Vector2 targetPos = target.localToStageCoordinates(new Vector2());

				setX(targetPos.x + (target.getWidth() - getWidth()) / 2);

				float tooltipY = targetPos.y - getHeight() - 6;
				float stageHeight = target.getStage().getHeight();

				//is there enough space to display above widget?
				if (stageHeight - tooltipY > stageHeight)
					setY(targetPos.y + target.getHeight() + 6); //display above widget
				else
					setY(tooltipY); //display bellow

				displayTask.cancel();
				Timer.schedule(displayTask, appearDelayTime);
			}
		}

		@Override
		public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
			if (pointer == -1) {
				displayTask.cancel();
				fadeOut();
			}
		}

		@Override
		public boolean mouseMoved (InputEvent event, float x, float y) {
			if (isVisible() && getActions().size == 0)
				fadeOut();

			return false;
		}
	}

	public static class TooltipStyle {
		public Drawable background;

		public TooltipStyle () {
		}

		public TooltipStyle (Drawable background) {
			this.background = background;
		}
	}

	public static class Builder {
		private final Actor content;

		private Actor target = null;
		private TooltipStyle style = null;

		public Builder (Actor content) {
			this.content = content;
		}

		public Builder (String text) {
			this(text, Align.center);
		}

		public Builder (String text, int textAlign) {
			VisLabel label = new VisLabel(text);
			label.setAlignment(textAlign);
			this.content = label;
		}

		public Builder target (Actor target) {
			this.target = target;
			return this;
		}

		public Builder style (String styleName) {
			return style(VisUI.getSkin().get(styleName, TooltipStyle.class));
		}

		public Builder style (TooltipStyle style) {
			this.style = style;
			return this;
		}

		public Tooltip build () {
			return new Tooltip(this);
		}
	}
}
