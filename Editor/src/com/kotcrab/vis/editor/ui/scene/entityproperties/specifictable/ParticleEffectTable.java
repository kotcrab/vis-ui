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
import com.kotcrab.vis.editor.scene.ParticleEffectObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.ui.widget.Tooltip;

import static com.kotcrab.vis.editor.util.vis.EntityUtils.setCommonCheckBoxState;

/**
 * Specific table for {@link ParticleEffectObject}
 * @author Kotcrab
 */
public class ParticleEffectTable extends SpecificObjectTable {
	private IndeterminateCheckbox activeCheck;

	@Override
	protected void init () {
		activeCheck = new IndeterminateCheckbox("Active on start");
		new Tooltip(activeCheck, "Controls whether to automatically start this effect on runtime.\nIn editor, particle effect are always active");

		activeCheck.addListener(properties.getSharedCheckBoxChangeListener());

		padTop(0);
		padLeft(3);
		left();
		add(activeCheck);
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		return entity instanceof ParticleEffectObject;
	}

	@Override
	public void updateUIValues () {
		Array<EditorObject> entities = properties.getEntities();

		setCommonCheckBoxState(entities, activeCheck, entity -> ((ParticleEffectObject) entity).isActive());
	}

	@Override
	public void setValuesToEntities () {
		Array<EditorObject> entities = properties.getEntities();
		for (EditorObject entity : entities) {
			ParticleEffectObject obj = (ParticleEffectObject) entity;

			if (activeCheck.isIndeterminate() == false) obj.setActive(activeCheck.isChecked());
		}
	}
}
