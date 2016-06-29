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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.runtime.util.autotable.EnumNameProvider;

/** @author Kotcrab */
public class AutoTableEnumSelectBox<E extends Enum<E>> extends EnumSelectBox<E> {
	public static final String INDETERMINATE = "<?>";

	public AutoTableEnumSelectBox (Class<E> enumClass, EnumNameProvider<E> nameProvider) {
		super(enumClass, nameProvider);
		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				setItems(getEnumMap().orderedKeys()); //removes INDETERMINATE if set
			}
		});
	}

	public void setIndeterminate (boolean indeterminate) {
		if (indeterminate) {
			getItems().add(INDETERMINATE);
			setItems(getItems().toArray());
			setSelected(INDETERMINATE);
		} else {
			setItems(getEnumMap().orderedKeys());
		}
	}

	public boolean isIndeterminate () {
		return getSelected().equals(INDETERMINATE);
	}
}
