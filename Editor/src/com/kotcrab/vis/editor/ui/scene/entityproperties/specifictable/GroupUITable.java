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

import com.kotcrab.vis.editor.module.scene.entitymanipulator.GroupSelectionFragment;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.SelectionFragment;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.gdx.ArrayUtils;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** @author Kotcrab */
public class GroupUITable extends SpecificUITable {
	private VisLabel idLabel;
	private VisValidatableTextField idField;

	public GroupUITable () {
		super(true);
	}

	@Override
	protected void init () {
		VisTable intIdTable = new VisTable(true);

		intIdTable.add(new VisLabel("Group Integer ID: "));
		intIdTable.add(idLabel = new VisLabel()).expandX().fillX();

		VisTable stringIdTable = new VisTable(true);

		stringIdTable.add(new VisLabel("Group ID"));
		stringIdTable.add(idField = new VisValidatableTextField()).expandX().fillX();
		idField.setProgrammaticChangeEvents(false);
		idField.addListener(properties.getSharedChangeListener());
		idField.addListener(properties.getSharedFocusListener());
		idField.addListener(properties.getSharedInputListener());

		add(intIdTable).expandX().fillX().row();
		add(stringIdTable).expandX().fillX();
	}

	@Override
	public boolean isSupported (EntityProxy entity) {
		ImmutableArray<SelectionFragment> selection = properties.getSelection().getFragmentedSelection();
		if (selection.size() != 1) return false;
		return ArrayUtils.has(selection, GroupSelectionFragment.class);
	}

	@Override
	public void updateUIValues () {
		GroupSelectionFragment groupProxy = (GroupSelectionFragment) properties.getSelection().getFragmentedSelection().get(0);
		int id = groupProxy.getGroupId();
		idLabel.setText(String.valueOf(id));
		idField.setText(properties.getSceneModuleContainer().getScene().getGroupStringId(id));
	}

	@Override
	public void setValuesToEntities () {
		GroupSelectionFragment groupProxy = (GroupSelectionFragment) properties.getSelection().getFragmentedSelection().get(0);
		int id = groupProxy.getGroupId();

		properties.getSceneModuleContainer().getScene().setGroupStringId(id, idField.getText());
	}
}
