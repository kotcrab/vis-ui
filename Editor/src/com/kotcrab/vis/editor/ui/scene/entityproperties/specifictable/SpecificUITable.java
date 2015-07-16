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

import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Specific objects tables allow to add custom widgets for {@link EntityProperties} dialog.
 * @author Kotcrab
 */
public abstract class SpecificUITable extends VisTable {
	protected EntityProperties properties;

	public SpecificUITable () {
		this(true);
	}

	public SpecificUITable (boolean useVisDefaults) {
		super(useVisDefaults);
	}

	protected abstract void init ();

	public void setProperties (EntityProperties properties) {
		if (this.properties != null) throw new IllegalArgumentException("Properties already assigned!");
		this.properties = properties;
		init();
	}

	public abstract boolean isSupported (EntityProxy entity);

	public abstract void updateUIValues ();

	public abstract void setValuesToEntities ();
}
