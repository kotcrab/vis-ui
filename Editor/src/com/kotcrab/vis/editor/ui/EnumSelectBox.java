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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.OrderedMap;
import com.kotcrab.vis.runtime.util.PrettyEnum;
import com.kotcrab.vis.ui.widget.VisSelectBox;

/**
 * Select box. For enums. Extends standard VisUI {@link VisSelectBox}
 * @author Kotcrab
 */
public class EnumSelectBox<T extends Enum<T> & PrettyEnum> extends VisSelectBox<String> {
	private EnumListListener<T> listener;

	private OrderedMap<String, T> enumMap = new OrderedMap<>();

	public EnumSelectBox (Class<T> enumClass) {
		try {
			for (T value : enumClass.getEnumConstants()) {
				if (enumClass.getField(value.toString()).isAnnotationPresent(Deprecated.class)) continue;
				enumMap.put(value.toPrettyString(), value);
			}
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}

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
