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

package com.kotcrab.vis.ui.widget.file.internal;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/** @author Kotcrab */
public class ServiceThreadFactory implements ThreadFactory {
	private final AtomicLong count = new AtomicLong(0);
	private final String threadPrefix;

	public ServiceThreadFactory (String threadPrefix) {
		super();
		this.threadPrefix = threadPrefix + "-";
	}

	@Override
	public Thread newThread (Runnable runnable) {
		Thread thread = Executors.defaultThreadFactory().newThread(runnable);
		thread.setName(threadPrefix + count.getAndIncrement());
		thread.setDaemon(true);
		return thread;
	}
}
