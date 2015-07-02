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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.SoundObject;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.ui.widget.VisLabel;

/**
 * Specific table for {@link SoundObject}
 * @author Kotcrab
 */
public class SoundObjectTable extends SpecificObjectTable {
	private VisLabel label;

	@Override
	protected void init () {
		label = new VisLabel();
		label.setColor(Color.GRAY);

		left();
		defaults().left();
		add(TableBuilder.build(new VisLabel("Sound:"), label)).row();
	}

	@Override
	public boolean isSupported (EditorObject entity) {
		return entity instanceof SoundObject;
	}

	@Override
	public void updateUIValues () {
		Array<EditorObject> entities = properties.getEntities();

		label.setText(EntityUtils.getCommonString(entities, "<?>", entity -> ((PathAsset) entity.getAssetDescriptor()).getPath()));
	}

	@Override
	public void setValuesToEntities () {

	}
}
