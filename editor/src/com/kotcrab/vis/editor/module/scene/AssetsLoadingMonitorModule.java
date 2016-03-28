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
