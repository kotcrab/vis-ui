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
import com.kotcrab.vis.editor.ui.EnumSelectBox.EnumListListener;
import com.kotcrab.vis.runtime.util.autotable.EnumNameProvider;
import com.kotcrab.vis.ui.widget.VisSelectBox;

/**
 * Similar to {@link EnumSelectBox} but uses name provider ({@link EnumNameProvider}
 * @author Kotcrab
 */
public class AutoTableEnumSelectBox<T extends Enum<T>> extends VisSelectBox<String> {
	public static final String INDETERMINATE = "<?>";

	private EnumListListener<T> listener;

	private OrderedMap<String, T> enumMap = new OrderedMap<>();

	public AutoTableEnumSelectBox (Class<T> enumClass, EnumNameProvider<T> nameProvider) {
		for (T value : enumClass.getEnumConstants()) enumMap.put(nameProvider.getPrettyName(value), value);

		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (listener != null)
					listener.changed(enumMap.get(getSelected()));

				setItems(enumMap.orderedKeys()); //removes INDETERMINATE if set
			}
		});

		setItems(enumMap.orderedKeys());
	}

	public void setIndeterminate (boolean indeterminate) {
		if (indeterminate) {
			getItems().add(INDETERMINATE);
			setItems(getItems().toArray());
			setSelected(INDETERMINATE);
		} else {
			setItems(enumMap.orderedKeys());
		}
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

	public boolean isIndeterminate () {
		return getSelected().equals(INDETERMINATE);
	}
}
