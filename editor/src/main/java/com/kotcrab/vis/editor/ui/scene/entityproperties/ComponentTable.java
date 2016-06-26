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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.artemis.Component;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;
import com.kotcrab.vis.runtime.util.annotation.VisInternal;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Specific objects tables allow to add custom widgets for {@link EntityProperties} dialog. Consider using
 * {@link AutoComponentTable} for simple components.
 * @author Kotcrab
 */
public abstract class ComponentTable<T extends Component> extends VisTable {
	protected EntityProperties properties;

	public ComponentTable () {
		this(true);
	}

	public ComponentTable (boolean useVisDefaults) {
		super(useVisDefaults);
	}

	protected abstract void init ();

	@VisInternal
	public void setProperties (EntityProperties properties) {
		if (this.properties != null) throw new IllegalArgumentException("Properties already assigned!");
		this.properties = properties;
		init();
	}

	/** @return component class that this {@link ComponentTable} supports. */
	public abstract Class<T> getComponentClass ();

	/**
	 * Called when this table should lock fields that won't be editable by user. This is always called if component
	 * is present in entity. The purpose of this method is to lock fields even when multiple entities is selected in
	 * which case {@link #updateUIValues()} would never be called. Framework takes care of resetting lock state.
	 */
	public void lockFields (T component) {

	}

	/** @return true if component can be removed by user from editor, false otherwise. */
	public abstract boolean isRemovable ();

	/** Called when this table must update it's UI values using values from component */
	public abstract void updateUIValues ();

	/** Called when this table must set component values from current UI values */
	public abstract void setValuesToEntities ();
}
