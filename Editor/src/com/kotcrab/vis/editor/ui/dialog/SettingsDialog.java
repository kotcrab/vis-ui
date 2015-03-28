/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
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
import com.kotcrab.vis.editor.module.BaseModule;
import com.kotcrab.vis.editor.module.editor.SettableModule;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.*;

import java.util.Comparator;

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
			DialogUtils.showErrorDialog(getStage(), settingsInvalidError);
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
		Node node = new Node(new VisLabel(module.getSettingsName()));

		modulesMap.put(module, node);
		tree.add(node);
		tree.getNodes().sort(nodeComparator);
	}

	public void remove (SettableModule module) {
		tree.getNodes().removeValue(modulesMap.get(module), true);
		tree.getNodes().sort(nodeComparator);
		modulesMap.remove(module);
	}

	public void addAll (Array<? extends BaseModule> modules) {
		for (BaseModule module : modules) {
			if (module instanceof SettableModule)
				add((SettableModule) module);
		}
	}

	public void removeAll (Array<ProjectModule> modules) {
		for (BaseModule module : modules) {
			if (module instanceof SettableModule)
				remove((SettableModule) module);
		}
	}

	private class NodeComparator implements Comparator<Node> {
		@Override
		public int compare (Node n1, Node n2) {
			VisLabel l1 = (VisLabel) n1.getActor();
			VisLabel l2 = (VisLabel) n2.getActor();
			return l1.getText().toString().compareToIgnoreCase(l2.getText().toString());
		}
	}
}
