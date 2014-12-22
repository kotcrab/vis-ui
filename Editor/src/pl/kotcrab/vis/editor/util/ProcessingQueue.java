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

package pl.kotcrab.vis.editor.util;

import java.util.concurrent.ArrayBlockingQueue;

/** Blocking queue that allows processing objects of any type, one by one. Element are processed in different thread. This queue
 * orders elements FIFO. Uses {@link ArrayBlockingQueue}. If not started in daemon mode and no longer needed
 * {@link ProcessingQueue#stop()} must be called to shutdown processing thread.
 * 
 * @author Pawel Pastuszak
 *
 * @param <E> type of objects that will be processed */
public abstract class ProcessingQueue<E> {
	private boolean running = true;

	private int capacity = 256;
	private boolean daemon = false;
	private String threadName = null;

	private Thread processingThread;

	private ArrayBlockingQueue<E> queue;

	/** Creates {@link ProcessingQueue}.
	 * 
	 * @param threadName name of processing thread that will be created
	 * @param capacity queue capacity, if queue is full, {@link ProcessingQueue#processLater()} will block until there is space in
	 *           queue */
	public ProcessingQueue (String threadName, int capacity) {
		this.threadName = threadName;
		this.capacity = capacity;
		start();
	}

	/** Creates {@link ProcessingQueue} with fixed 256 objects capacity. If queue is full, {@link ProcessingQueue#processLater()}
	 * will block until there is space in queue
	 * 
	 * @param threadName name of processing thread that will be created */
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
		queue = new ArrayBlockingQueue<E>(capacity);

		processingThread = new Thread(new Runnable() {
			@Override
			public void run () {
				while (running) {
					try {
						processQueueElement(queue.take());
					} catch (InterruptedException e) {
						Log.interruptedEx(e);
					} catch (Exception e) {
						Log.exception(e);
					}
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

	/** Add element to queue. If queue is full, execution will be blocked until there is empty space in queue.
	 * @param element to be added to queue */
	public void processLater (E element) {
		try {
			queue.put(element);
		} catch (InterruptedException e) { // if queue was stopped
			Log.interruptedEx(e);
		}
	}

	/** Called by processing thread when queue element should be processed.
	 * @param element to be processed */
	protected abstract void processQueueElement (E element);
}
