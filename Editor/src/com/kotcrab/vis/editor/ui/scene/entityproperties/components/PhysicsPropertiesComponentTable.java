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
import com.kotcrab.vis.editor.util.gdx.ArrayUtils;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.component.PhysicsPropertiesComponent;

/** @author Kotcrab */
public class PhysicsPropertiesComponentTable extends AutoComponentTable<PhysicsPropertiesComponent> {
	private IndeterminateCheckbox adjustOriginCheck;

	public PhysicsPropertiesComponentTable (ModuleInjector injector) {
		super(injector, PhysicsPropertiesComponent.class, true);
	}

	@Override
	protected void init () {
		super.init();
		adjustOriginCheck = getUiByField("adjustOrigin", IndeterminateCheckbox.class);
	}

	@Override
	public void componentAddedToEntities () {
		super.componentAddedToEntities();

		EntityUtils.stream(properties.getProxies(), (proxy, entity) -> {
			if (proxy.hasComponent(PhysicsPropertiesComponent.class)) {
				if (entity.getComponent(PhysicsPropertiesComponent.class).adjustOrigin)
					proxy.setOrigin(0, 0);
			}
		});
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();

		if (adjustOriginCheck.isIndeterminate() == false) {
			boolean adjustOrigin = adjustOriginCheck.isChecked();
			if (adjustOrigin) {

				Holder<Boolean> uiUpdatedNeeded = new Holder<>(false);

				ArrayUtils.stream(properties.getProxies(), proxy -> {
					if (proxy.getOriginX() != 0 || proxy.getOriginY() != 0) {
						uiUpdatedNeeded.value = true;
						proxy.setOrigin(0, 0);
					}
				});

				if (uiUpdatedNeeded.value) properties.selectedEntitiesBasicValuesChanged();
			}
		}
	}
}
