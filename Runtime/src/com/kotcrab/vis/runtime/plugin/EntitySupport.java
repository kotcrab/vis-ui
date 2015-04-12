package com.kotcrab.vis.runtime.plugin;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.entity.Entity;

public interface EntitySupport<ED extends EntityData, E extends Entity> {
	void setLoaders (AssetManager manager);

	Class<E> getEntityClass ();

	void resolveDependencies (Array<AssetDescriptor> deps, ED entityData);

	E getInstanceFromData(AssetManager manager, ED data);
}
