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

package com.kotcrab.vis.usl.test;

import com.kotcrab.vis.usl.USL;
import org.junit.Ignore;
import org.junit.Test;

public class RemoteTest {
	@Ignore
	@Test
	public void testRemote () throws Exception {
		USL.parse(null, "include <visui-0.7.7>");
		USL.parse(null, "include <visui-0.8.0>");
		USL.parse(null, "include <visui-0.8.1>");
	}
}
