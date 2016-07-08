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

package com.kotcrab.vis.runtime.assets;

/**
 * Classes implementing this interface references some asset. All asset descriptor must be immutable.
 * @author Kotcrab
 */
public interface VisAssetDescriptor {
	/**
	 * Used to compare two {@link VisAssetDescriptor}. VisEditor uses this during asset refactoring to find asset descriptors
	 * that needs updating, for this reason this may ignore some properties ie. regionName in {@link AtlasRegionAsset}. This
	 * method should only check that descriptor being compared points to the same file as this descriptor. However if you
	 * are writing custom AssetTransactionGenerator (see VisEditor source) you may like to include other
	 * properties as well.
	 * @param asset other asset used for comparison
	 * @return true is assets are same, false otherwise
	 */
	boolean compare (VisAssetDescriptor asset);
}
