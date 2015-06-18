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

package com.kotcrab.vis.editor.event.bus;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.ProcessingQueue;

import java.util.ArrayList;

/**
 * Handles distributing {@link Event}s for all registered listeners. All events are handled on OpenGL thread.
 * @author Kotcrab
 * @see App#eventBus
 */
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
		Gdx.app.postRunnable(() -> postEvent(event));
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
