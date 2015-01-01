/**
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

package pl.kotcrab.vis.editor.event;

import java.util.ArrayList;

import pl.kotcrab.vis.editor.util.Log;
import pl.kotcrab.vis.editor.util.ProcessingQueue;

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
