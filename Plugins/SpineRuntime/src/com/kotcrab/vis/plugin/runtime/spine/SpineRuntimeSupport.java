package com.kotcrab.vis.plugin.runtime.spine;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.api.plugin.RuntimeEntitySupport;

public class SpineRuntimeSupport implements RuntimeEntitySupport<SpineData, SpineEntity>
{
	@Override
	public Class<SpineEntity> getEntityClass () {
		return null;
	}

	@Override
	public void resolveDependencies (Array<AssetDescriptor> deps, SpineData entityData) {

	}

	@Override
	public SpineEntity getInstanceFromData (AssetManager manager, SpineData data) {
		return null;
	}
}
