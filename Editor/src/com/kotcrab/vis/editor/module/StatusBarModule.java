/**
 * Copyright 2014-2015 Pawel Pastuszak
 * 
 * This file is part of VisEditor.
 * 
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module;

import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.StatusBarEvent;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

public class StatusBarModule extends EditorModule implements EventListener {
	public VisTable table;
	public VisLabel statusLabel;

	public Timer timer;

	public StatusBarModule () {
		timer = new Timer();

		statusLabel = new VisLabel("Ready");

		table = new VisTable();
		table.setBackground(VisUI.skin.getDrawable("button"));
		table.add(statusLabel);
		table.add().expand().fill();
	}

	public void addToStage (Table root) {
		root.add(table).fillX().expandX().row();
	}

	public void setText (String newText, int timeSeconds) {
		statusLabel.setText(newText);
		timer.clear();
		timer.scheduleTask(resetTask, timeSeconds);
	}

	@Override
	public void added () {
		App.eventBus.register(this);
		addToStage(Editor.instance.getRoot());
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	private Task resetTask = new Task() {
		@Override
		public void run () {
			statusLabel.setText("Ready");
		}
	};

	@Override
	public boolean onEvent (Event e) {
		if (e instanceof StatusBarEvent) {
			StatusBarEvent event = (StatusBarEvent)e;
			setText(event.text, event.timeSeconds);
			return true;
		}

		return false;
	}
}
