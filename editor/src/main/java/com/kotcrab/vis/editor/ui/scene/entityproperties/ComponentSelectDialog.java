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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.GroupSelectionFragment;
import com.kotcrab.vis.editor.plugin.api.UserAddableComponentProvider;
import com.kotcrab.vis.editor.util.gdx.ArrayUtils;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

/** @author Kotcrab */
public class ComponentSelectDialog extends VisTable { //TODO search field when we will have more components
	private ExtensionStorageModule extensionStorage;

	private EntityProperties properties;
	private ComponentSelectDialogListener listener;

	private Array<Class<? extends Component>> componentClasses = new Array<>();

	private VisTextButtonStyle buttonStyle;

	private VisTable scrollPaneTable;

	private InputListener inputListener;

	public ComponentSelectDialog (SceneModuleContainer sceneMC, EntityProperties properties, ComponentSelectDialogListener listener) {
		super(false);
		this.properties = properties;
		this.listener = listener;
		sceneMC.injectModules(this);

		setBackground(VisUI.getSkin().getDrawable("tooltip-bg"));

		reloadComponents();

		buttonStyle = new VisTextButtonStyle(VisUI.getSkin().get(VisTextButtonStyle.class));

		scrollPaneTable = new VisTable(false);
		scrollPaneTable.top();

		VisScrollPane scrollPane = new VisScrollPane(scrollPaneTable);
		scrollPane.setScrollingDisabled(false, true);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);

		VisImageButton closeButton = new VisImageButton("close");

		add(new VisLabel("Select component"));
		add(closeButton).right().row();

		addSeparator().colspan(2);
		add(scrollPane).colspan(2).expand().fill().padLeft(3).padRight(3);
		setSize(220, 200);

		closeButton.addListener(new VisChangeListener((event, actor) -> remove()));

		inputListener = new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (contains(event.getStageX(), event.getStageY()) == false) {
					remove();
					return true;
				}

				return false;
			}
		};
	}

	private boolean contains (float x, float y) {
		return getX() <= x && getX() + getWidth() >= x && getY() <= y && getY() + getHeight() >= y;
	}

	public Array<Class<? extends Component>> getComponentClasses () {
		return componentClasses;
	}
	
	public void reloadComponents() {
		componentClasses.clear();
		for (UserAddableComponentProvider provider : extensionStorage.getUserAddableComponentProviders()) {
			componentClasses.add(provider.provide());
		}
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
		if (stage != null) {
			properties.addListener(inputListener);
			stage.addListener(inputListener);
		}
	}

	@Override
	public boolean remove () {
		if (getStage() != null) {
			properties.removeListener(inputListener);
			getStage().removeListener(inputListener);
		}
		return super.remove();
	}

	public boolean build () {
		scrollPaneTable.clearChildren();

		if (ArrayUtils.has(properties.getFragmentedSelection(), GroupSelectionFragment.class))
			return false;

		boolean atLeastOneComponentAdded = false;

		for (Class<? extends Component> clazz : componentClasses) {
			if (EntityUtils.isComponentCommon(clazz, properties.getSelectedEntities()) == false) {
				VisTextButton button = new VisTextButton(clazz.getSimpleName(), buttonStyle);
				button.setFocusBorderEnabled(false);
				scrollPaneTable.add(button).expandX().fillX().row();

				button.addListener(new VisChangeListener((event, actor) -> {
					listener.selected(clazz);
					remove();
				}));

				atLeastOneComponentAdded = true;
			}
		}

		invalidateHierarchy();

		return atLeastOneComponentAdded;
	}

	interface ComponentSelectDialogListener {
		void selected (Class<? extends Component> clazz);
	}
}
