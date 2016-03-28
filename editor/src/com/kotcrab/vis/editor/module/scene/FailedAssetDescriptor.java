package com.kotcrab.vis.editor.module.scene;

import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/** @author Kotcrab */
public class FailedAssetDescriptor {
	public final VisAssetDescriptor asset;
	public final Throwable throwable;

	public FailedAssetDescriptor (VisAssetDescriptor asset, Throwable throwable) {
		this.asset = asset;
		this.throwable = throwable;
	}
}
