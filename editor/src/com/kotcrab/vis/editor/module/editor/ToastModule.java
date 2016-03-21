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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Module for displaying toast messages at upper right corner of VisEditor screen. Toasts can be closed by users or they
 * can automatically disappear after a period of time.
 * @author Kotcrab
 */
public class ToastModule extends EditorModule {
	private static final int SCREEN_PADDING = 20;
	private static final int MESSAGE_PADDING = 5;

	private Stage stage;

	private Array<ToastTable> toasts = new Array<>();
	private static final int UNTIL_CLOSED = -1;

	@Override
	public void resize () {
		updateToastsPositions();
	}

	public void show (String text) {
		show(text, UNTIL_CLOSED);
	}

	public void show (Table table) {
		show(table, UNTIL_CLOSED);
	}

	public void show (ToastTable toastTable) {
		show(toastTable, UNTIL_CLOSED);
	}

	public void show (String text, int timeSec) {
		VisTable table = new VisTable();
		table.add(text).expand().fill();
		show(table, timeSec);
	}

	public void show (Table table, int timeSec) {
		show(new ToastTable(table, timeSec));
	}

	public void show (ToastTable toastTable, int timeSec) {
		toastTable.setToastModule(this);
		toasts.add(toastTable);
		stage.addActor(toastTable);
		updateToastsPositions();
	}

	private void remove (ToastTable toastTable) {
		toasts.removeValue(toastTable, true);
		updateToastsPositions();
	}

	private void updateToastsPositions () {
		float y = stage.getHeight() - SCREEN_PADDING;

		for (ToastTable table : toasts) {
			table.setPosition(stage.getWidth() - table.getWidth() - SCREEN_PADDING, y - table.getHeight());
			y = y - table.getHeight() - MESSAGE_PADDING;
		}
	}

	public static class ToastTable extends VisTable {
		private ToastModule toastModule;
		protected final Table content;

		public ToastTable () {
			this(new VisTable(), UNTIL_CLOSED);
		}

		public ToastTable (Table content, int timeSec) {
			this.content = content;
			setBackground(VisUI.getSkin().getDrawable("tooltip-bg"));

			VisImageButton closeButton = new VisImageButton("close");
			closeButton.addListener(new VisChangeListener((event, actor) -> close()));

			add(content).pad(3).fill().expand();
			add(closeButton).top();

			if (timeSec > 0) {
				Timer.schedule(new Task() {
					@Override
					public void run () {
						fadeOut();
					}
				}, timeSec);
			}

			pack();
			fadeIn();
		}

		/** Called when close button was pressed by default call {@link #fadeOut()} */
		protected void close () {
			fadeOut();
		}

		protected void fadeOut () {
			addAction(Actions.sequence(Actions.fadeOut(VisWindow.FADE_TIME, Interpolation.fade), new Action() {
				@Override
				public boolean act (float delta) {
					toastModule.remove(ToastTable.this);
					return true;
				}
			}, Actions.removeActor()));
		}

		protected Table fadeIn () {
			setColor(1, 1, 1, 0);
			addAction(Actions.fadeIn(VisWindow.FADE_TIME, Interpolation.fade));
			return this;
		}

		void setToastModule (ToastModule toastModule) {
			this.toastModule = toastModule;
		}
	}
}


