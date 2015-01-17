/*
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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.MenuEvent;
import com.kotcrab.vis.editor.event.MenuEventType;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;

public class ToolbarModule extends EditorModule implements EventListener {
	public VisTable table;

	public ToolbarModule () {
		table = new VisTable(false);
		table.setBackground(VisUI.skin.getDrawable("button"));
		table.add(createButton("save", MenuEventType.FILE_SAVE));
		table.add().expand().fill();
	}

	@Override
	public void added () {
		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Override
	public boolean onEvent (Event e) {
		return false;
	}

	public Table getTable () {
		return table;
	}

	private VisImageButton createButton (String iconName, MenuEventType eventType) {
		VisImageButton button = new VisImageButton(Assets.getIcon(iconName));
		button.addListener(new ToolbarButtonChangeListener(eventType));
		return button;
	}

	private class ToolbarButtonChangeListener extends ChangeListener {
		private MenuEventType type;

		public ToolbarButtonChangeListener (MenuEventType eventType) {
			this.type = eventType;
		}

		@Override
		public void changed (ChangeEvent event, Actor actor) {
			App.eventBus.post(new MenuEvent(type));
		}
	}
}
