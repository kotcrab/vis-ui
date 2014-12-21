/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.editor.event;

import java.util.ArrayList;

import pl.kotcrab.vis.editor.Log;
import pl.kotcrab.vis.editor.ProcessingQueue;

public class EventBus {
	private ArrayList<EventListener> listeners;
	private ProcessingQueue<Event> queue;

	public EventBus () {
		listeners = new ArrayList<EventListener>();

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
		try {
			for (EventListener listener : listeners)
				if (listener.onEvent(event)) break;
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
