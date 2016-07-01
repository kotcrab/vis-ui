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

package com.kotcrab.vis.plugin.spriter.ui;

import com.badlogic.gdx.utils.Array;
import com.brashmonkey.spriter.Entity;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.plugin.spriter.component.SpriterProperties;
import com.kotcrab.vis.plugin.spriter.runtime.component.VisSpriter;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;

/** @author Kotcrab */
public class VisSpriterComponentTable extends AutoComponentTable<SpriterProperties> {

	private VisSelectBox<String> animSelectBox;

	public VisSpriterComponentTable (ModuleInjector injector) {
		super(injector, SpriterProperties.class, false);
	}

	@Override
	protected void init () {
		super.init();

		animSelectBox = new VisSelectBox<>();
		animSelectBox.getSelection().setProgrammaticChangeEvents(false);
		animSelectBox.addListener(properties.getSharedSelectBoxChangeListener());

		VisTable table = new VisTable(true);
		table.add(new VisLabel("Animation"));
		table.add(animSelectBox);

		add(table);
	}

	@Override
	public void updateUIValues () {
		super.updateUIValues();

		ImmutableArray<EntityProxy> proxies = properties.getSelectedEntities();
		if (proxies.size() == 1) {
			VisSpriter spriter = EntityUtils.getFirstEntity(proxies).getComponent(VisSpriter.class);
			animSelectBox.setDisabled(false);

			Entity entity = spriter.getPlayer().getEntity();

			Array<String> animations = new Array<>(entity.animations());

			for (int i = 0; i < entity.animations(); i++) {
				animations.add(entity.getAnimation(i).name);
			}

			animSelectBox.setItems(animations);
		} else {
			animSelectBox.setDisabled(true);
			animSelectBox.setItems("<select one entity>");
		}
	}

	@Override
	public void setValuesToEntities () {
		super.setValuesToEntities();

		ImmutableArray<EntityProxy> proxies = properties.getSelectedEntities();
		if (proxies.size() == 1) {
			VisSpriter spriter = EntityUtils.getFirstEntityComponent(proxies, VisSpriter.class);
			SpriterProperties properties = EntityUtils.getFirstEntityComponent(proxies, SpriterProperties.class);
			Entity entity = spriter.getPlayer().getEntity();

			properties.animation = entity.getAnimation(animSelectBox.getSelected()).id;
		}

		EntityUtils.stream(proxies, VisSpriter.class, (proxy, spriterComponent) -> {
			SpriterProperties propertiesComponent = proxy.getComponent(SpriterProperties.class);
			spriterComponent.getPlayer().setScale(propertiesComponent.scale);
			spriterComponent.setPlayOnStart(propertiesComponent.playOnStart);
			spriterComponent.setDefaultAnimation(propertiesComponent.animation);
			spriterComponent.getPlayer().setAnimation(propertiesComponent.animation);
			spriterComponent.setAnimationPlaying(propertiesComponent.previewInEditor);
		});
	}
}
