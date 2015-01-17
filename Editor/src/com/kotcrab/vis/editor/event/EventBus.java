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

package com.kotcrab.vis.editor.event;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.editor.util.ProcessingQueue;

import java.util.ArrayList;

public class EventBus {
	private ArrayList<EventListener> listeners;
	private ProcessingQueue<Event> queue;

	public EventBus () {
		listeners = new ArrayList<>();

		queue = new ProcessingQueue<Event>("EventBus", true) {
			@Override
			protected void processQueueElement (Event event) {
				processEvent(event);
			}
		};
	}

	public void register (EventListener listener) {
		listeners.add(listener);
	}

	public boolean unregister (EventListener listener) {
		return listeners.remove(listener);
	}

	public void post (Event event) {
		queue.processLater(event);
	}

	public void stop () {
		queue.stop();
	}

	private void processEvent (final Event event) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				postEvent(event);
			}
		});
	}

	private void postEvent (Event event) {
		try {
			for (int i = 0; i < listeners.size(); i++)
				if (listeners.get(i).onEvent(event)) break;
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
