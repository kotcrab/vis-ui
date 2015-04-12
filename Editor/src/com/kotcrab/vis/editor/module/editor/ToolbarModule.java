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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.MenuEvent;
import com.kotcrab.vis.editor.event.MenuEventType;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;

public class ToolbarModule extends EditorModule implements EventListener {
	public VisTable table;

	public ToolbarModule () {
		table = new VisTable(false);
		table.setBackground(VisUI.getSkin().getDrawable("button"));
		table.add(createButton(Icons.SAVE, "Save", MenuEventType.FILE_SAVE));
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

	private VisImageButton createButton (Icons icon, String text, MenuEventType eventType) {
		VisImageButton button = new VisImageButton(Assets.getIcon(icon), text);
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
