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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Provides UI widget with status bar that is displayed at the bottom of VisEditor screen
 * @author Kotcrab
 */
public class StatusBarModule extends EditorModule {
	public VisTable table;
	public VisLabel statusLabel;
	public VisLabel infoLabel;

	public Timer timer;
	private Task resetTask = new Task() {
		@Override
		public void run () {
			statusLabel.setText("");
			statusLabel.setColor(Color.WHITE);
		}
	};

	public StatusBarModule () {
		timer = new Timer();

		statusLabel = new VisLabel("");
		infoLabel = new VisLabel("");

		table = new VisTable();
		table.setBackground(VisUI.getSkin().getDrawable("button"));
		table.add(statusLabel);
		table.add().expand().fill();
		table.add(infoLabel);
	}

	public void setText (String text) {
		setText(text, 3);
	}

	public void setText (String newText, int timeSeconds) {
		setText(newText, Color.WHITE, timeSeconds);
	}

	public void setText (String newText, Color color, int timeSeconds) {
		statusLabel.setText(newText);
		statusLabel.setColor(color == null ? Color.WHITE : color);
		timer.clear();
		timer.scheduleTask(resetTask, timeSeconds);
	}

	public Table getTable () {
		return table;
	}

	public void setInfoLabelText (String text) {
		infoLabel.setText(text);
	}
}
