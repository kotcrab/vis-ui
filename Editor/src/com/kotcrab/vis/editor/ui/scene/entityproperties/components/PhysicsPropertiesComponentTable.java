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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components;

import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.component.PhysicsProperties;

/** @author Kotcrab */
public class PhysicsPropertiesComponentTable extends AutoComponentTable<PhysicsProperties> {
	private IndeterminateCheckbox adjustOriginCheck;

	public PhysicsPropertiesComponentTable (ModuleInjector injector) {
		super(injector, PhysicsProperties.class, true);
	}

	@Override
	protected void init () {
		super.init();
		adjustOriginCheck = getUIByFieldId("adjustOrigin", IndeterminateCheckbox.class);
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();
		adjustOriginIfNeeded();
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();
		adjustOriginIfNeeded();
	}

	private void adjustOriginIfNeeded () {

		if (adjustOriginCheck.isIndeterminate() == false) {
			boolean adjustOrigin = adjustOriginCheck.isChecked();
			if (adjustOrigin) {

				Holder<Boolean> uiUpdatedNeeded = Holder.of(false);

				EntityUtils.stream(properties.getSelectedEntities(), (proxy, entity) -> {
					if (proxy.isOriginSupported() && (proxy.getOriginX() != 0 || proxy.getOriginY() != 0)) {
						uiUpdatedNeeded.value = true;
						proxy.setOrigin(0, 0);
					}
				});

				if (uiUpdatedNeeded.value) properties.selectedEntitiesBasicValuesChanged();
			}
		}
	}
}
