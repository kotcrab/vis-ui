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

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.IndeterminateCheckbox;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.runtime.component.MusicComponent;

import static com.kotcrab.vis.editor.util.vis.EntityUtils.setCommonCheckBoxState;

/**
 * Specific table for MusicComponent
 */
public class MusicUITable extends SpecificUITable {
	private IndeterminateCheckbox loopingCheck;
	private IndeterminateCheckbox playOnStartCheck;

	@Override
	protected void init () {
		loopingCheck = new IndeterminateCheckbox("Loop");
		playOnStartCheck = new IndeterminateCheckbox("Play on start");

		loopingCheck.addListener(properties.getSharedCheckBoxChangeListener());
		playOnStartCheck.addListener(properties.getSharedCheckBoxChangeListener());

		left();
		defaults().left();
		add(TableBuilder.build(loopingCheck, playOnStartCheck)).colspan(2);
	}

	@Override
	public boolean isSupported (EntityProxy entity) {
		return entity.hasComponent(MusicComponent.class);
	}

	@Override
	public void updateUIValues () {
		Array<EntityProxy> proxies = properties.getProxies();

		setCommonCheckBoxState(proxies, loopingCheck, (Entity entity) -> entity.getComponent(MusicComponent.class).isLooping());
		setCommonCheckBoxState(proxies, playOnStartCheck, (Entity entity) -> entity.getComponent(MusicComponent.class).isPlayOnStart());
	}

	@Override
	public void setValuesToEntities () {
		for (EntityProxy proxy : properties.getProxies()) {
			for (Entity entity : proxy.getEntities()) {
				MusicComponent music = entity.getComponent(MusicComponent.class);

				if (loopingCheck.isIndeterminate() == false) music.setLooping(loopingCheck.isChecked());
				if (playOnStartCheck.isIndeterminate() == false) music.setPlayOnStart(playOnStartCheck.isChecked());
			}
		}
	}
}
