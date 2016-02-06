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

package com.kotcrab.vis.ui.util.adapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter.SelectionPolicy.SelectionDisabled;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.ListAdapterListener;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Basic {@link ListAdapter} implementation using {@link CachedItemAdapter}. Supports item selection. Classes
 * extending this should store provided list and provide delegates for all common methods that change array state.
 * Those delegates should call {@link #itemAdded(Object)} or {@link #itemRemoved(Object)} in order to properly update
 * view cache. When changes to array are to big to be handled by those two methods {@link #itemsChanged()} should be
 * called.
 * <p>
 * When view does not existed in cache and must be created {@link #createView(Object)} is called. When item view exists
 * in cache {@link #updateView(Actor, Object)} will be called.
 * <p>
 * Enabling item selection requires calling {@link #setSelectionPolicy(SelectionPolicy)} (see {@link SelectionPolicy}
 * internal classes for built-in policies implementations) and overriding {@link #selectView(Actor)} and {@link #deselectView(Actor)}.
 * @author Kotcrab
 * @see ArrayAdapter
 * @see ArrayListAdapter
 * @since 1.0.0
 */
public abstract class AbstractListAdapter<ItemT, ViewT extends Actor> extends CachedItemAdapter<ItemT, ViewT>
		implements ListAdapter<ItemT> {
	protected ListView<ItemT> view;
	protected ListAdapterListener viewListener;

	private SelectionPolicy<ItemT, ViewT> selectionPolicy = new SelectionDisabled<ItemT, ViewT>();
	private ListSelection<ItemT, ViewT> selection = new ListSelection<ItemT, ViewT>(this);

	private ObjectMap<ViewT, ItemT> viewMap = new ObjectMap<ViewT, ItemT>();
	private ItemClickListener<ItemT> clickListener;

	@Override
	public void fillTable (VisTable itemsTable) {
		viewMap.clear();
		for (final ItemT item : iterable()) {
			final ViewT view = getView(item);
			viewMap.put(view, item);

			boolean listenerMissing = true;
			for (EventListener listener : view.getListeners()) {
				if (ListClickListener.class.isInstance(listener)) {
					listenerMissing = false;
					break;
				}
			}
			if (listenerMissing) {
				view.setTouchable(Touchable.enabled);
				view.addListener(new ListClickListener(view, item));
			}

			itemsTable.add(view).growX();
			itemsTable.row();
		}
	}

	@Override
	public void setListView (ListView<ItemT> view, ListAdapterListener viewListener) {
		this.view = view;
		this.viewListener = viewListener;
	}

	@Override
	public void setItemClickListener (ItemClickListener<ItemT> listener) {
		clickListener = listener;
	}

	protected void itemAdded (ItemT item) {
		viewListener.invalidateDataSet();
	}

	protected void itemRemoved (ItemT item) {
		getViews().remove(item);
		viewListener.invalidateDataSet();
	}

	public void itemsChanged () {
		getViews().clear();
		viewListener.invalidateDataSet();
	}

	@Override
	protected void updateView (ViewT view, ItemT item) {

	}

	public SelectionPolicy<ItemT, ViewT> getSelectionPolicy () {
		return selectionPolicy;
	}

	/** @return selected items */
	public ListSelection<ItemT, ViewT> getSelection () {
		return selection;
	}

	public void setSelectionPolicy (SelectionPolicy<ItemT, ViewT> selectionPolicy) {
		if (selectionPolicy == null) throw new IllegalArgumentException("selectionPolicy can't be null");
		this.selectionPolicy = selectionPolicy;
	}

	protected void selectView (ViewT view) {
		if (selectionPolicy instanceof SelectionDisabled) return;
		throw new UnsupportedOperationException("selectView must be implemented when `selectionPolicy` is different than SelectionDisabled");
	}

	protected void deselectView (ViewT view) {
		if (selectionPolicy instanceof SelectionDisabled) return;
		throw new UnsupportedOperationException("deselectView must be implemented when `selectionPolicy` is different than SelectionDisabled");
	}

	private class ListClickListener extends ClickListener {
		private ViewT view;
		private ItemT item;

		public ListClickListener (ViewT view, ItemT item) {
			this.view = view;
			this.item = item;
		}

		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if (selection.getArray().contains(item, true) == false) {
				selection.select(item, view);
			} else {
				selection.deselect(item, view);
			}
			return true;
		}

		@Override
		public void clicked (InputEvent event, float x, float y) {
			clickListener.clicked(item);
		}
	}

	public interface SelectionPolicy<ItemT, ViewT extends Actor> {
		boolean accept (ListSelection<ItemT, ViewT> selection, ItemT newItem);

		public static class SelectionDisabled<ItemT, ViewT extends Actor> implements SelectionPolicy<ItemT, ViewT> {
			@Override
			public boolean accept (ListSelection<ItemT, ViewT> selection, ItemT newItem) {
				return false;
			}
		}

		public static class SingleSelection<ItemT, ViewT extends Actor> implements SelectionPolicy<ItemT, ViewT> {
			@Override
			public boolean accept (ListSelection<ItemT, ViewT> selection, ItemT newItem) {
				selection.deselectAll();
				return true;
			}
		}

		public static class MultipleSelection<ItemT, ViewT extends Actor> implements SelectionPolicy<ItemT, ViewT> {
			@Override
			public boolean accept (ListSelection<ItemT, ViewT> selection, ItemT newItem) {
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) {
					selection.deselectAll();
				}
				return true;
			}
		}
	}

	public static class ListSelection<ItemT, ViewT extends Actor> {
		private AbstractListAdapter<ItemT, ViewT> listAdapter;

		private Array<ItemT> selection = new Array<ItemT>();

		private ListSelection (AbstractListAdapter<ItemT, ViewT> listAdapter) {
			this.listAdapter = listAdapter;
		}

		public boolean select (ItemT item) {
			return select(item, listAdapter.getViews().get(item));
		}

		boolean select (ItemT item, ViewT view) {
			if (listAdapter.selectionPolicy.accept(this, item) == false) return false;
			if (selection.contains(item, true) == false) {
				listAdapter.selectView(view);
				selection.add(item);
				return true;
			}

			return false;
		}

		public boolean deselect (ItemT item) {
			return deselect(item, listAdapter.getViews().get(item));
		}

		boolean deselect (ItemT item, ViewT view) {
			if (selection.contains(item, true) == false) return false;
			listAdapter.deselectView(view);
			selection.removeValue(item, true);
			return true;
		}

		public void deselectAll () {
			Array<ItemT> items = new Array<ItemT>(selection);
			for (ItemT item : items) {
				deselect(item);
			}
		}

		/** @return internal array, MUST NOT be modified directly */
		public Array<ItemT> getArray () {
			return selection;
		}
	}
}
