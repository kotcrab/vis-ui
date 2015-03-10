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

package com.kotcrab.vis.editor.util;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Blocking queue that allows processing objects of any type, one by one. Element are processed in different thread. This queue
 * orders elements FIFO. Uses {@link ArrayBlockingQueue}. If not started in daemon mode and no longer needed
 * {@link ProcessingQueue#stop()} must be called to shutdown processing thread.
 * @param <E> type of objects that will be processed
 * @author Pawel Pastuszak
 */
public abstract class ProcessingQueue<E> {
	private boolean running = true;

	private int capacity = 256;
	private boolean daemon = false;
	private String threadName = null;

	private Thread processingThread;

	private ArrayBlockingQueue<E> queue;

	/**
	 * Creates {@link ProcessingQueue}.
	 * @param threadName name of processing thread that will be created
	 * @param capacity   queue capacity, if queue is full, {@link ProcessingQueue#processLater(E element)} will block until there is space in
	 *                   queue
	 */
	public ProcessingQueue (String threadName, int capacity) {
		this.threadName = threadName;
		this.capacity = capacity;
		start();
	}

	/**
	 * Creates {@link ProcessingQueue} with fixed 256 objects capacity. If queue is full, {@link ProcessingQueue#processLater(E element)}
	 * will block until there is space in queue
	 * @param threadName name of processing thread that will be created
	 */
	public ProcessingQueue (String threadName) {
		this.threadName = threadName;
		start();
	}

	public ProcessingQueue (String threadName, boolean daemon) {
		this.threadName = threadName;
		this.daemon = daemon;
		start();
	}

	private void start () {
		queue = new ArrayBlockingQueue<>(capacity);

		processingThread = new Thread(() -> {
			while (running) {
				try {
					processQueueElement(queue.take());
				} catch (Exception e) {
					Log.exception(e);
				}
			}
		}, threadName);
		processingThread.setDaemon(daemon);
		processingThread.start();
	}

	/** Stop and clear queue. Interrupt and stops processing thread. After calling this method queue becomes unusable. */
	public void stop () {
		running = false;
		processingThread.interrupt();
		queue.clear();
	}

	/**
	 * Add element to queue. If queue is full, execution will be blocked until there is empty space in queue.
	 * @param element to be added to queue
	 */
	public void processLater (E element) {
		try {
			queue.put(element);
		} catch (InterruptedException e) { // if queue was stopped
			Log.exception(e);
		}
	}

	/**
	 * Called by processing thread when queue element should be processed.
	 * @param element to be processed
	 */
	protected abstract void processQueueElement (E element);
}
