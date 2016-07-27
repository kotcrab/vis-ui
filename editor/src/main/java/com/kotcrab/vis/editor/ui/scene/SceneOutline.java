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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.GroupSelectionFragment;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.SelectionFragment;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.SingleSelectionFragment;
import com.kotcrab.vis.editor.module.scene.system.EntitiesCollector;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache.EntityProxyCacheListener;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTree;

/**
 * Scene outline with all scene entities.
 * @author Kotcrab
 */
public class SceneOutline extends VisTable implements EntityProxyCacheListener {
	private EntityManipulatorModule entityManipulator;

	private EntityProxyCache proxyCache;
	private EntitiesCollector entitiesCollector;

	private EditorScene scene;

	private VisTree tree;
	private IntArray expandedNodes = new IntArray();
	private boolean outlineRebuildScheduled;

	public SceneOutline (SceneModuleContainer sceneMC) {
		super(true);
		sceneMC.injectModules(this);
		scene = sceneMC.getScene();

		proxyCache.addListener(this);

		tree = new VisTree();
		tree.getSelection().setMultiple(true);
		tree.getSelection().setRequired(false);
		tree.getSelection().setProgrammaticChangeEvents(false);

		tree.addListener(new ClickListener() {
			Node selection;

			@Override
			public void clicked (InputEvent event, float x, float y) {
				//tree will deselect item after double click so we on first click store selection
				if (getTapCount() == 1 && tree.getSelection().size() == 1) {
					selection = tree.getSelection().getLastSelected();
				}

				if (getTapCount() == 2 && selection != null) {
					if (selection instanceof ProxyNode) {
						sceneMC.getSceneTab().centerAround(((ProxyNode) selection).proxy);
					}

					if (selection instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) selection;
						sceneMC.getSceneTab().centerAroundGroup(groupNode.layerId, groupNode.groupId);
					}

					selection = null;
				}
			}
		});

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);

		add(new VisLabel("Outline", Align.center)).expandX().fillX().top().spaceBottom(0).row();

		VisScrollPane scrollPane = new VisScrollPane(tree);
		scrollPane.setFadeScrollBars(false);

		add(scrollPane).expand().fill();
		pack();

		rebuildOutline(); //do first update
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (outlineRebuildScheduled) {
			outlineRebuildScheduled = false;
			rebuildOutline();
		}
	}

	public void selectedEntitiesChanged () {
		tree.getSelection().clear();
		for (SelectionFragment fragment : entityManipulator.getFragmentedSelection()) {
			highlightProxy(tree.getNodes(), fragment);
		}
	}

	public void rebuildOutline () {
		buildGroupNodeState(tree.getNodes());

		tree.clearChildren();

		Values<EntityProxy> proxies = proxyCache.getCache().values();
		Array<EntityProxy> ignoreProxies = new Array<>();

		for (EntityProxy proxy : proxies) {
			if (ignoreProxies.contains(proxy, true)) {
				continue;
			}

			int gid = proxy.getLastGroupId();
			if (gid != -1) {
				Array<EntityProxy> result = entitiesCollector.collect(proxy.getLayerID(), gid);
				ignoreProxies.addAll(result);
				buildTreeRecursively(result, gid, null);
			} else {
				tree.add(new ProxyNode(proxy));
			}
		}

		expandedNodes.clear();
	}

	private void buildTreeRecursively (Array<EntityProxy> groupProxies, int gid, Node parent) {
		GroupNode groupRoot = new GroupNode(groupProxies.first().getLayerID(), gid);
		if (parent == null)
			tree.add(groupRoot);
		else
			parent.add(groupRoot);

		if (expandedNodes.contains(gid)) {
			groupRoot.setExpanded(true);
		}

		Array<EntityProxy> ignoreProxies = new Array<>();

		for (EntityProxy proxy : groupProxies) {
			if (ignoreProxies.contains(proxy, true)) {
				continue;
			}

			int gidBefore = proxy.getGroupIdBefore(gid);
			if (gidBefore != -1) {
				Array<EntityProxy> result = entitiesCollector.collect(proxy.getLayerID(), gidBefore);
				ignoreProxies.addAll(result);
				buildTreeRecursively(result, gidBefore, groupRoot);
				continue;
			}

			groupRoot.add(new ProxyNode(proxy));
		}
	}

	private void buildGroupNodeState (Array<Node> nodes) {
		for (Node n : nodes) {

			if (n.getChildren().size > 0) {

				if (n instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) n;

					if (groupNode.isExpanded()) {
						expandedNodes.add(groupNode.groupId);
					}

					buildGroupNodeState(groupNode.getChildren());
				}
			}
		}
	}

	private boolean highlightProxy (Array<Node> nodes, SelectionFragment selFragment) {
		for (Node node : nodes) {
			if (node instanceof ProxyNode && selFragment instanceof SingleSelectionFragment) {
				ProxyNode proxyNode = (ProxyNode) node;
				SingleSelectionFragment fragment = (SingleSelectionFragment) selFragment;

				if (proxyNode.proxy.compareProxyByUUID(fragment.getProxy())) {
					tree.getSelection().add(node);
					return true;
				}
			}

			if (node instanceof GroupNode && selFragment instanceof GroupSelectionFragment) {
				GroupNode groupNode = (GroupNode) node;
				GroupSelectionFragment fragment = (GroupSelectionFragment) selFragment;

				if (groupNode.groupId == fragment.getGroupId()) {
					tree.getSelection().add(node);
					return true;
				}
			}

			if (node.getChildren().size > 0) {
				boolean wasExpanded = node.isExpanded();
				node.setExpanded(true);
				if (highlightProxy(node.getChildren(), selFragment)) return true;
				if (wasExpanded == false) node.setExpanded(false);
			}
		}

		return false;
	}

	@Override
	public void cacheChanged () {
		outlineRebuildScheduled = true;
	}

	private static class GroupNode extends Node {
		private final int layerId;
		private final int groupId;

		public GroupNode (int layerId, int groupId) {
			super(new VisLabel("Group (id: " + groupId + ")", "small"));
			this.layerId = layerId;
			this.groupId = groupId;
		}
	}

	private static class ProxyNode extends Node {
		private EntityProxy proxy;

		public ProxyNode (EntityProxy proxy) {
			super(new VisLabel(proxy.getEntityName() + (proxy.getId() != null ? " (" + proxy.getId() + ")" : ""), "small"));
			this.proxy = proxy;
		}
	}
}
