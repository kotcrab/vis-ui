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

package com.kotcrab.vis.editor.ui.scene;

import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache.EntityProxyCacheListener;
import com.kotcrab.vis.editor.module.scene.system.GroupProxyProviderSystem;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.proxy.GroupEntityProxy;
import com.kotcrab.vis.editor.util.scene2d.EventStopper;
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

	private Array<EntityProxy> selectedEntities;
	private EntityProxyCache proxyCache;
	private GroupProxyProviderSystem groupProxyProvider;

	private IntArray expandedNodes = new IntArray();
	private VisTree tree;

	public SceneOutline (SceneModuleContainer sceneMC, Array<EntityProxy> selectedEntities) {
		super(true);
		sceneMC.injectModules(this);

		this.selectedEntities = selectedEntities;

		proxyCache.addListener(this);

		tree = new VisTree();
		tree.getSelection().setMultiple(true);
		tree.getSelection().setRequired(false);
		tree.getSelection().setProgrammaticChangeEvents(false);

		tree.addListener(new ClickListener() {
			OutlineNode selection;

			@Override
			public void clicked (InputEvent event, float x, float y) {
				//tree will deselect item after double click so we on first click store selection
				if (getTapCount() == 1 && tree.getSelection().size() == 1) {
					selection = (OutlineNode) tree.getSelection().getLastSelected();
				}

				if (getTapCount() == 2 && selection != null) {
					sceneMC.getSceneTab().centerAround(selection.proxy);
					selection = null;
				}
			}
		});

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		addListener(new EventStopper());

		add(new VisLabel("Outline", Align.center)).expandX().fillX().top().spaceBottom(0).row();

		VisScrollPane scrollPane = new VisScrollPane(tree);
		scrollPane.setFadeScrollBars(false);

		add(scrollPane).expand().fill();
		pack();

		cacheChanged(); //do first update
	}

	public void selectedEntitiesChanged () {
		tree.getSelection().clear();
		for (EntityProxy proxy : selectedEntities) {
			highlightProxy(tree.getNodes(), proxy);
		}
	}

	private boolean highlightProxy (Array<Node> nodes, EntityProxy proxy) {
		for (Node n : nodes) {
			OutlineNode node = (OutlineNode) n;
			if (node.proxy.compareProxyByID(proxy)) {
				tree.getSelection().add(n);
				return true;
			}

			if (n.getChildren().size > 0) {
				boolean wasExpanded = n.isExpanded();
				n.setExpanded(true);
				if (highlightProxy(n.getChildren(), proxy)) return true;
				if (wasExpanded == false) n.setExpanded(false);
			}
		}

		return false;
	}

	private void buildGroupNodeState (Array<Node> nodes) {
		for (Node n : nodes) {
			OutlineNode node = (OutlineNode) n;

			if (node.getChildren().size > 0) {
				GroupEntityProxy groupProxy = (GroupEntityProxy) node.proxy;

				if (node.isExpanded()) {
					expandedNodes.add(groupProxy.getGroupId());
				}

				buildGroupNodeState(node.getChildren());
			}
		}
	}

	public void rebuildOutline () {
		buildGroupNodeState(tree.getNodes());

		tree.clearChildren();

		Values<EntityProxy> proxies = proxyCache.getCache().values();

		Array<Entity> ignoreEntities = new Array<>();

		for (EntityProxy proxy : proxies) {
			if (ignoreEntities.contains(proxy.getEntities().get(0), true)) {
				continue;
			}

			int gid = proxy.getLastGroupId();
			if (gid != -1) {
				GroupEntityProxy groupProxy = groupProxyProvider.getGroupEntityProxy(gid);
				ignoreEntities.addAll(groupProxy.getEntities());
				buildTreeRecursively(groupProxy, null);
				continue;
			}

			tree.add(new OutlineNode(proxy));
		}

		expandedNodes.clear();
	}

	@Override
	public void cacheChanged () {
		rebuildOutline();
	}

	private void buildTreeRecursively (GroupEntityProxy groupProxy, Node parent) {
		OutlineNode groupRoot = new OutlineNode(groupProxy);
		if (parent == null) {
			tree.add(groupRoot);
		} else {
			parent.add(groupRoot);
		}

		if (expandedNodes.contains(groupProxy.getGroupId())) {
			groupRoot.setExpanded(true);
		}

		Array<Entity> ignoreEntities = new Array<>();

		for (Entity entity : groupProxy.getEntities()) {
			EntityProxy proxy = proxyCache.get(entity);

			if (ignoreEntities.contains(proxy.getEntities().get(0), true)) {
				continue;
			}

			int gidBefore = proxy.getGroupIdBefore(groupProxy.getGroupId());
			if (gidBefore != -1) {
				GroupEntityProxy nestedGroupProxy = groupProxyProvider.getGroupEntityProxy(gidBefore);
				ignoreEntities.addAll(nestedGroupProxy.getEntities());
				buildTreeRecursively(nestedGroupProxy, groupRoot);
				continue;
			}

			groupRoot.add(new OutlineNode(proxy));
		}
	}

	private static class OutlineNode extends Node {
		private EntityProxy proxy;

		public OutlineNode (EntityProxy proxy) {
			super(new VisLabel(proxy.getEntityName(), "small"));
			this.proxy = proxy;
		}
	}
}
