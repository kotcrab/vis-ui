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

package pl.kotcrab.vis.editor.module;

import pl.kotcrab.vis.editor.App;
import pl.kotcrab.vis.editor.Assets;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.event.Event;
import pl.kotcrab.vis.editor.event.EventListener;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisImageButton;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ToolbarModule extends EditorModule implements EventListener {
	public VisTable table;
	public VisImageButton saveButton;

	public ToolbarModule () {
		saveButton = new VisImageButton(Assets.getIcon("save"));

		table = new VisTable(false);
		table.setBackground(VisUI.skin.getDrawable("button"));
		table.add(saveButton);
		table.add().expand().fill();
	}

	public void addToStage (Table root) {
		root.add(table).fillX().expandX().row();
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

	@Override
	public boolean onEvent (Event e) {
		return false;
	}
}
