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

package com.kotcrab.vis.editor.ui.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.module.Module;
import com.kotcrab.vis.editor.module.editor.SettableModule;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;

import java.util.Comparator;

/**
 * VisEditor settings dialog
 * @author Kotcrab
 */
public class SettingsDialog extends VisWindow {
	private static final String settingsInvalidError = "Settings are invalid and cannot be applied";

	private NodeComparator nodeComparator = new NodeComparator();

	private ObjectMap<SettableModule, Node> modulesMap = new ObjectMap<>();

	private VisTree tree;
	private Table containerTable;
	private Table buttonTable;

	private Node lastNode;

	public SettingsDialog () {
		super("Settings");
		addCloseButton();
		closeOnEscape();
		setResizable(true);
		setModal(true);

		createButtonsTable();

		containerTable = new VisTable();
		tree = new VisTree();
		tree.getSelection().setMultiple(false);

		containerTable.add(new VisLabel("Select item to view settings for it")).expandX().space(3);

		VisSplitPane splitPane = new VisSplitPane(tree, containerTable, false);
		splitPane.setSplitAmount(0.3f);

		add(splitPane).expand().fill();
		row();
		add(buttonTable).expandX().fillX().padBottom(3);

		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Node node = tree.getSelection().first();

				if (node != null && lastNode != node) {
					lastNode = node;
					containerTable.clear();
					containerTable.add(modulesMap.findKey(node, true).getSettingsTable()).expand().fill().padLeft(10).padTop(3);
				}
			}
		});

		addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					if (applySettingsIfPossible()) {
						close();
						return true;
					}
				}

				return false;
			}
		});

		setSize(500, 400);
		centerWindow();
	}

	private boolean isCurrentSettingsValid () {
		for (SettableModule module : modulesMap.keys())
			if (module.settingsChanged() == false) return false;

		return true;
	}

	private boolean applySettingsIfPossible () {
		if (isCurrentSettingsValid() == false) {
			Dialogs.showErrorDialog(getStage(), settingsInvalidError);
			return false;
		}

		applySettings();
		return true;
	}

	private void applySettings () {
		for (SettableModule module : modulesMap.keys())
			module.settingsApply();
	}

	private void createButtonsTable () {
		buttonTable = new VisTable(true);
		buttonTable.right();

		VisTextButton okButton = new VisTextButton("OK");
		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton applyButton = new VisTextButton("Apply");

		buttonTable.add(okButton);
		buttonTable.add(cancelButton);
		buttonTable.add(applyButton);

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (applySettingsIfPossible())
					close();
			}
		});

		applyButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				applySettingsIfPossible();
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				close();
			}
		});
	}

	public void add (final SettableModule module) {
		Node node = new Node(new SettingsCategoryLabel(module.getSettingsName(), module.getListPriority()));

		modulesMap.put(module, node);
		tree.add(node);
		tree.getNodes().sort(nodeComparator);
	}

	public void remove (SettableModule module) {
		tree.remove(modulesMap.get(module));
		tree.getNodes().sort(nodeComparator);
		modulesMap.remove(module);
	}

	public void addAll (Array<? extends Module> modules) {
		for (Module module : modules) {
			if (module instanceof SettableModule)
				add((SettableModule) module);
		}
	}

	public void removeAll (Array<ProjectModule> modules) {
		for (Module module : modules) {
			if (module instanceof SettableModule)
				remove((SettableModule) module);
		}
	}

	private class SettingsCategoryLabel extends VisLabel {
		private int priority;

		public SettingsCategoryLabel (CharSequence text, int priority) {
			super(text);
			this.priority = priority;
		}

		public int getPriority () {
			return priority;
		}
	}

	private class NodeComparator implements Comparator<Node> {
		@Override
		public int compare (Node n1, Node n2) {
			SettingsCategoryLabel l1 = (SettingsCategoryLabel) n1.getActor();
			SettingsCategoryLabel l2 = (SettingsCategoryLabel) n2.getActor();
			String t1 = l1.getText().toString();
			String t2 = l2.getText().toString();

			int priorityResult = (int) Math.signum(l1.getPriority() - l2.getPriority()) * -1;

			if (priorityResult != 0)
				return priorityResult;
			else
				return t1.compareToIgnoreCase(t2);
		}
	}
}
