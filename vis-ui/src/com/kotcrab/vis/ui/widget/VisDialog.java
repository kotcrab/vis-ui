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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Displays a dialog, which is a modal window containing a content table with a button table underneath it. Methods are provided
 * to add a label to the content table and buttons to the button table, but any widgets can be added. When a button is clicked,
 * {@link #result(Object)} is called and the dialog is removed from the stage.
 * <p>
 * Due to scope of changes made this widget is not compatible with standard {@link Dialog}.
 * @author Nathan Sweet
 * @author Kotcrab
 */
public class VisDialog extends VisWindow {
	Table contentTable, buttonTable;
	private Skin skin;
	@SuppressWarnings({"unchecked", "rawtypes"})
	ObjectMap<Actor, Object> values = new ObjectMap();
	boolean cancelHide;
	Actor previousKeyboardFocus, previousScrollFocus;
	FocusListener focusListener;

	protected InputListener ignoreTouchDown = new InputListener() {
		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			event.cancel();
			return false;
		}
	};

	public VisDialog (String title) {
		super(title);
		this.skin = VisUI.getSkin();
		setSkin(skin);
		initialize();
	}

	public VisDialog (String title, String windowStyleName) {
		super(title, VisUI.getSkin().get(windowStyleName, WindowStyle.class));
		this.skin = VisUI.getSkin();
		setSkin(skin);
		initialize();
	}

	public VisDialog (String title, WindowStyle windowStyle) {
		super(title, windowStyle);
		this.skin = VisUI.getSkin();
		setSkin(skin);
		initialize();
	}

	private void initialize () {
		setModal(true);
		getTitleLabel().setAlignment(VisUI.getDefaultTitleAlign());

		defaults().space(6);
		add(contentTable = new Table(skin)).expand().fill();
		row();
		add(buttonTable = new Table(skin));

		contentTable.defaults().space(2).padLeft(3).padRight(3);
		buttonTable.defaults().space(6).padBottom(3);

		buttonTable.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (!values.containsKey(actor)) return;
				while (actor.getParent() != buttonTable)
					actor = actor.getParent();
				result(values.get(actor));
				if (!cancelHide) hide();
				cancelHide = false;
			}
		});

		focusListener = new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (!focused) focusChanged(event);
			}

			@Override
			public void scrollFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (!focused) focusChanged(event);
			}

			private void focusChanged (FocusEvent event) {
				Stage stage = getStage();
				if (isModal() && stage != null && stage.getRoot().getChildren().size > 0
						&& stage.getRoot().getChildren().peek() == VisDialog.this) { // Dialog is top most actor.
					Actor newFocusedActor = event.getRelatedActor();
					if (newFocusedActor != null && !newFocusedActor.isDescendantOf(VisDialog.this)) event.cancel();
				}
			}
		};
	}

	@Override
	protected void setStage (Stage stage) {
		if (stage == null)
			addListener(focusListener);
		else
			removeListener(focusListener);
		super.setStage(stage);
	}

	public Table getContentTable () {
		return contentTable;
	}

	public Table getButtonsTable () {
		return buttonTable;
	}

	/** Adds a label to the content table. The dialog must have been constructed with a skin to use this method. */
	public VisDialog text (String text) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
		return text(text, skin.get(LabelStyle.class));
	}

	/** Adds a label to the content table. */
	public VisDialog text (String text, LabelStyle labelStyle) {
		return text(new Label(text, labelStyle));
	}

	/** Adds the given Label to the content table */
	public VisDialog text (Label label) {
		contentTable.add(label);
		return this;
	}

	/**
	 * Adds a text button to the button table. Null will be passed to {@link #result(Object)} if this button is clicked. The dialog
	 * must have been constructed with a skin to use this method.
	 */
	public VisDialog button (String text) {
		return button(text, null);
	}

	/**
	 * Adds a text button to the button table. The dialog must have been constructed with a skin to use this method.
	 * @param object The object that will be passed to {@link #result(Object)} if this button is clicked. May be null.
	 */
	public VisDialog button (String text, Object object) {
		if (skin == null)
			throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
		return button(text, object, skin.get(VisTextButtonStyle.class));
	}

	/**
	 * Adds a text button to the button table.
	 * @param object The object that will be passed to {@link #result(Object)} if this button is clicked. May be null.
	 */
	public VisDialog button (String text, Object object, VisTextButtonStyle buttonStyle) {
		return button(new VisTextButton(text, buttonStyle), object);
	}

	/** Adds the given button to the button table. */
	public VisDialog button (Button button) {
		return button(button, null);
	}

	/**
	 * Adds the given button to the button table.
	 * @param object The object that will be passed to {@link #result(Object)} if this button is clicked. May be null.
	 */
	public VisDialog button (Button button, Object object) {
		buttonTable.add(button);
		setObject(button, object);
		return this;
	}

	/** {@link #pack() Packs} the dialog and adds it to the stage with custom action which can be null for instant show */
	public VisDialog show (Stage stage, Action action) {
		clearActions();
		removeCaptureListener(ignoreTouchDown);

		previousKeyboardFocus = null;
		Actor actor = stage.getKeyboardFocus();
		if (actor != null && !actor.isDescendantOf(this)) previousKeyboardFocus = actor;

		previousScrollFocus = null;
		actor = stage.getScrollFocus();
		if (actor != null && !actor.isDescendantOf(this)) previousScrollFocus = actor;

		pack();
		stage.addActor(this);
		stage.setKeyboardFocus(this);
		stage.setScrollFocus(this);
		if (action != null) addAction(action);

		return this;
	}

	/** {@link #pack() Packs} the dialog and adds it to the stage, centered with default fadeIn action */
	public VisDialog show (Stage stage) {
		show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}

	/** Hides the dialog with the given action and then removes it from the stage. */
	public void hide (Action action) {
		Stage stage = getStage();
		if (stage != null) {
			removeListener(focusListener);
			if (previousKeyboardFocus != null && previousKeyboardFocus.getStage() == null) previousKeyboardFocus = null;
			Actor actor = stage.getKeyboardFocus();
			if (actor == null || actor.isDescendantOf(this)) stage.setKeyboardFocus(previousKeyboardFocus);

			if (previousScrollFocus != null && previousScrollFocus.getStage() == null) previousScrollFocus = null;
			actor = stage.getScrollFocus();
			if (actor == null || actor.isDescendantOf(this)) stage.setScrollFocus(previousScrollFocus);
		}
		if (action != null) {
			addCaptureListener(ignoreTouchDown);
			addAction(sequence(action, Actions.removeListener(ignoreTouchDown, true), Actions.removeActor()));
		} else
			remove();
	}

	/**
	 * Hides the dialog. Called automatically when a button is clicked. The default implementation fades out the dialog over 400
	 * milliseconds and then removes it from the stage.
	 */
	public void hide () {
		hide(sequence(Actions.fadeOut(FADE_TIME, Interpolation.fade), Actions.removeListener(ignoreTouchDown, true),
				Actions.removeActor()));
	}

	public void setObject (Actor actor, Object object) {
		values.put(actor, object);
	}

	/**
	 * If this key is pressed, {@link #result(Object)} is called with the specified object.
	 * @see Keys
	 */
	public VisDialog key (final int keycode, final Object object) {
		addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode2) {
				if (keycode == keycode2) {
					result(object);
					if (!cancelHide) hide();
					cancelHide = false;
				}
				return false;
			}
		});
		return this;
	}

	/**
	 * Called when a button is clicked. The dialog will be hidden after this method returns unless {@link #cancel()} is called.
	 * @param object The object specified when the button was added.
	 */
	protected void result (Object object) {
	}

	public void cancel () {
		cancelHide = true;
	}
}
