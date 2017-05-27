/*
 * Copyright 2014-2017 See AUTHORS file.
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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Implementation of {@link ItemAdapter} that caches created views. Provides two methods that are called when new view
 * should be created and when old view should be updated (see {@link #createView(Object)} and {@link #updateView(Actor, Object)}).
 * Internal cache is not cleared automatically and obsolete entries must be removed manually.
 * @author Kotcrab
 * @since 1.0.0
 */
public abstract class CachedItemAdapter<ItemT, ViewT extends Actor> implements ItemAdapter<ItemT> {
	private ObjectMap<ItemT, ViewT> views = new ObjectMap<ItemT, ViewT>();

	@Override
	public final ViewT getView (ItemT item) {
		ViewT view = views.get(item);

		if (view == null) {
			view = createView(item);
			if (view == null) throw new IllegalStateException("Returned view view can't be null");
			views.put(item, view);
		} else {
			updateView(view, item);
		}

		return view;
	}

	/** @return internal views cache map */
	protected ObjectMap<ItemT, ViewT> getViews () {
		return views;
	}

	protected abstract ViewT createView (ItemT item);

	protected abstract void updateView (ViewT view, ItemT item);
}
