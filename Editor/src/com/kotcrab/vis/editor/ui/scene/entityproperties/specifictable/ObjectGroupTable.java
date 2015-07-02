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

package com.kotcrab.vis.editor.ui.scene.entityproperties.specifictable;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.ObjectGroup;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.ui.widget.Tooltip;

/**
 * Specific table for {@link ObjectGroup}
 * @author Kotcrab
 */
public class ObjectGroupTable extends SpecificObjectTable {
	private IndeterminateCheckbox preserveCheck;

	@Override
	protected void init () {
		preserveCheck = new IndeterminateCheckbox("Preserve on runtime");
		new Tooltip(preserveCheck, "Controls whether to preserve this group on runtime.\nIf enabled it will be possible to get this group by ID");

		preserveCheck.addListener(properties.getSharedCheckBoxChangeListener());

		padTop(0);
		padLeft(3);
		left();
		add(preserveCheck);
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		return entity instanceof ObjectGroup;
	}

	@Override
	public void updateUIValues () {
		Array<EditorObject> entities = properties.getEntities();

		EntityUtils.setCommonCheckBoxState(entities, preserveCheck, entity -> ((ObjectGroup) entity).isPreserveOnRuntime());
	}

	@Override
	public void setValuesToEntities () {
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			ObjectGroup obj = (ObjectGroup) entity;

			if (preserveCheck.isIndeterminate() == false) obj.setPreserveForRuntime(preserveCheck.isChecked());
		}
	}
}
