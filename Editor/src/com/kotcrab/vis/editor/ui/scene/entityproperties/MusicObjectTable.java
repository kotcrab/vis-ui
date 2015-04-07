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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.MusicObject;
import com.kotcrab.vis.editor.ui.IndeterminateCheckbox;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.ui.widget.VisLabel;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.setCheckBoxState;

class MusicObjectTable extends SpecificObjectTable {
	private VisLabel label;
	private IndeterminateCheckbox loopingCheck;
	private IndeterminateCheckbox playOnStartCheck;

	public MusicObjectTable (EntityProperties properties) {
		super(properties, true);
		loopingCheck = new IndeterminateCheckbox("Loop");
		playOnStartCheck = new IndeterminateCheckbox("Play on start");

		loopingCheck.addListener(properties.getSharedCheckBoxChangeListener());
		playOnStartCheck.addListener(properties.getSharedCheckBoxChangeListener());

		label = new VisLabel();
		label.setColor(Color.GRAY);

		left();
		defaults().left();
		add(TableBuilder.build(new VisLabel("Music:"), label)).row();
		add(TableBuilder.build(loopingCheck, playOnStartCheck));
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		return entity instanceof MusicObject;
	}

	@Override
	public void updateUIValues () {
		Array<EditorObject> entities = properties.getEntities();

		label.setText(Utils.getCommonString(entities, "<?>", entity -> ((MusicObject) entity).getAssetPath()));
		setCheckBoxState(entities, loopingCheck, entity -> ((MusicObject) entity).isLooping());
		setCheckBoxState(entities, playOnStartCheck, entity -> ((MusicObject) entity).isPlayOnStart());
	}

	@Override
	public void setValuesToEntities () {
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			MusicObject obj = (MusicObject) entity;

			if (loopingCheck.isIndeterminate() == false) obj.setLooping(loopingCheck.isChecked());
			if (playOnStartCheck.isIndeterminate() == false) obj.setPlayOnStart(playOnStartCheck.isChecked());
		}
	}
}
