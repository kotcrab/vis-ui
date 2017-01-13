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
 * Tooltips are widgets that appear below other widget on mouse pointer hover. Each actor can have only one tooltip.
 * <p>
 * LibGDX 1.6.4 introduced it's own systems of tooltips. VisUI tooltips existed before that and are unrelated and
 * incompatible with libGDX tooltips. VisUI tooltips will remain supported.
 * @author Kotcrab
 * @since 0.5.0
 */
public class Tooltip extends VisTable {
	public static float DEFAULT_FADE_TIME = 0.3f;
	public static float DEFAULT_APPEAR_DELAY_TIME = 0.6f;
	/**
	 * Controls whether to fade out tooltip when mouse was moved. Changing this will not affect already existing tooltips.
	 * @see #setMouseMoveFadeOut(boolean)
	 */
	public static boolean MOUSE_MOVED_FADEOUT = false;

	private Actor target;
	private Actor content;
	private Cell<Actor> contentCell;

	private boolean mouseMoveFadeOut = MOUSE_MOVED_FADEOUT;
	private TooltipInputListener listener;

	private DisplayTask displayTask;

	private float fadeTime = DEFAULT_FADE_TIME;
	private float appearDelayTime = DEFAULT_APPEAR_DELAY_TIME;

	private Tooltip (Builder builder) {
		super(true);

		TooltipStyle style = builder.style;
		if (style == null) style = VisUI.getSkin().get("default", TooltipStyle.class);

		init(style, builder.target, builder.content);
		if (builder.width != -1) {
			contentCell.width(builder.width);
			pack();
		}
	}

	public Tooltip () {
		this("default");
	}

	public Tooltip (String styleName) {
		super(true);
		init(VisUI.getSkin().get(styleName, TooltipStyle.class), null, null);
	}

	public Tooltip (TooltipStyle style) {
		super(true);
		init(style, null, null);
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
				if (pointer == -1) {
					fadeOut();
				}
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
	 * Detaches tooltip form current target, does not change tooltip target meaning that this tooltip can be reattached to
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

	public Actor getTarget () {
		return target;
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

	public Cell<Actor> getContentCell () {
		return contentCell;
	}

	/**
	 * Changes text tooltip to specified text. If tooltip content is not instance of VisLabel then previous tooltip content
	 * will be replaced by VisLabel instance.
	 * @param text next tooltip text
	 */
	public void setText (String text) {
		if (content instanceof VisLabel) {
			((VisLabel) content).setText(text);
		} else {
			setContent(new VisLabel(text));
		}
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

	public boolean isMouseMoveFadeOut () {
		return mouseMoveFadeOut;
	}

	/**
	 * @param mouseMoveFadeOut if true tooltip fill fade out when mouse was moved. If false tooltip will only fadeout on
	 * mouse click or when mouse has exited target widget. Default is {@link Tooltip#MOUSE_MOVED_FADEOUT}.
	 */
	public void setMouseMoveFadeOut (boolean mouseMoveFadeOut) {
		this.mouseMoveFadeOut = mouseMoveFadeOut;
	}

	private class DisplayTask extends Task {
		@Override
		public void run () {
			if(target.getStage() == null) return;
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
					setY(tooltipY); //display below

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
			if (mouseMoveFadeOut && isVisible() && getActions().size == 0) fadeOut();
			return false;
		}
	}

	public static class TooltipStyle {
		public Drawable background;

		public TooltipStyle () {
		}

		public TooltipStyle (TooltipStyle style) {
			this.background = style.background;
		}

		public TooltipStyle (Drawable background) {
			this.background = background;
		}
	}

	public static class Builder {
		private final Actor content;

		private Actor target = null;
		private TooltipStyle style = null;
		private float width = -1;

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

		/** Sets tooltip width. If tooltip content is text only then calling this will automatically enable label wrapping. */
		public Builder width (float width) {
			if (width < 0) throw new IllegalArgumentException("width must be > 0");
			this.width = width;
			if (content instanceof VisLabel) {
				((VisLabel) content).setWrap(true);
			}
			return this;
		}

		public Tooltip build () {
			return new Tooltip(this);
		}
	}
}
