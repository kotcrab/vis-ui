package com.kotcrab.vis.runtime.api.plugin;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.api.data.EntityData;
import com.kotcrab.vis.runtime.api.entity.Entity;

public interface RuntimeEntitySupport<ED extends EntityData, E extends Entity> {
	Class<E> getEntityClass ();

	void resolveDependencies (Array<AssetDescriptor> deps, ED entityData);

	E getInstanceFromData(AssetManager manager, ED data);
}
