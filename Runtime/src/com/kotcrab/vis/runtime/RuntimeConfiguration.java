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

package com.kotcrab.vis.runtime;

import com.artemis.Entity;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.GroupComponent;
import com.kotcrab.vis.runtime.system.VisGroupManager;

/**
 * Holds runtime configurations values
 * @author Kotcrab
 */
public class RuntimeConfiguration {
	/**
	 * Controls whether to store {@link AssetComponent} in {@link Entity} after inflating it. Set this to false if you
	 * need to access {@link AssetComponent} during runtime. Default is true.
	 */
	public boolean removeAssetsComponentAfterInflating = true;

	/**
	 * Controls whether to add {@link VisGroupManager} into Artemis. Set this to false if you don't need to retrieve
	 * groups from VisEditor by id or by string id. Default is true. Even if false {@link GroupComponent} (which
	 * stores all groups int ids) is not removed so it can be accessed if needed.
	 */
	public boolean useVisGroupManager = true;
}
