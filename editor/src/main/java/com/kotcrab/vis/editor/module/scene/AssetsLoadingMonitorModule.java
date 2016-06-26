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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/** @author Kotcrab */
public class AssetsLoadingMonitorModule extends SceneModule {
	private Array<FailedAssetDescriptor> failedDescriptors = new Array<>();

	public void addFailedResource (VisAssetDescriptor descriptor, Throwable throwable) {
		failedDescriptors.add(new FailedAssetDescriptor(descriptor, throwable));
	}

	public Array<FailedAssetDescriptor> getFailedDescriptors () {
		return failedDescriptors;
	}
}
