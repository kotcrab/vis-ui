/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

public class ToastModule extends EditorModule {
	private static final int SCREEN_PADDING = 20;
	private static final int MESSAGE_PADDING = 5;

	private Stage stage;
	private Array<ToastTable> toasts = new Array<>();
	private static final int UNTIL_CLOSED = -1;

	@Override
	public void init () {
		stage = Editor.instance.getStage();
	}

	public void show (String text) {
		show(text, UNTIL_CLOSED);
	}

	public void show (Table table) {
		show(table, UNTIL_CLOSED);
	}

	public void show (String text, int timeSec) {
		VisTable table = new VisTable();
		table.add(text).expand().fill();
		show(table, timeSec);
	}

	public void show (Table table, int timeSec) {
		ToastTable msgTable = new ToastTable(table, timeSec);
		toasts.add(msgTable);
		stage.addActor(msgTable);
		updateToastsPositions();
	}

	@Override
	public void resize () {
		updateToastsPositions();
	}

	private void updateToastsPositions () {
		float y = stage.getHeight() - SCREEN_PADDING;

		for (ToastTable table : toasts) {
			table.setPosition(stage.getWidth() - table.getWidth() - SCREEN_PADDING, y - table.getHeight());
			y = y - table.getHeight() - MESSAGE_PADDING;
		}

	}

	private class ToastTable extends Table {
		public ToastTable (Table table, int timeSec) {
			setBackground(VisUI.getSkin().getDrawable("tooltip-bg"));

			VisImageButton closeButton = new VisImageButton("close");
			closeButton.addListener(new VisChangeListener((event, actor) -> fadeOut()));

			add(table).pad(3).fill().expand();
			add(closeButton).top();

			if (timeSec > 0) {
				Timer.schedule(new Task() {
					@Override
					public void run () {
						fadeOut();
					}
				}, timeSec);
			}

			fadeIn();
			pack();
		}

		private void fadeOut () {
			addAction(Actions.sequence(Actions.fadeOut(VisWindow.FADE_TIME, Interpolation.fade), new Action() {
				@Override
				public boolean act (float delta) {
					toasts.removeValue(ToastTable.this, true);
					updateToastsPositions();
					return true;
				}
			}, Actions.removeActor()));
		}

		private Table fadeIn () {
			setColor(1, 1, 1, 0);
			addAction(Actions.fadeIn(VisWindow.FADE_TIME, Interpolation.fade));
			return this;
		}
	}
}


