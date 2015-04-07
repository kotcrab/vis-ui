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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.OrderedMap;
import com.kotcrab.vis.runtime.util.PrettyEnum;
import com.kotcrab.vis.ui.widget.VisSelectBox;

public class EnumSelectBox<T extends Enum<T> & PrettyEnum> extends VisSelectBox<String> {
	private EnumListListener<T> listener;

	private OrderedMap<String, T> enumMap = new OrderedMap<>();

	public EnumSelectBox (Class<T> enumClass) {
		for (T value : enumClass.getEnumConstants()) enumMap.put(value.toPrettyString(), value);

		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (listener != null)
					listener.changed(enumMap.get(getSelected()));
			}
		});

		setItems(enumMap.orderedKeys());
	}

	public void setSelectedEnum (T newEnum) {
		setSelected(enumMap.findKey(newEnum, true));
	}

	public T getSelectedEnum () {
		return enumMap.get(getSelected());
	}

	public void setListener (EnumListListener<T> listener) {
		this.listener = listener;
	}

	public interface EnumListListener<T> {
		void changed (T result);
	}
}
