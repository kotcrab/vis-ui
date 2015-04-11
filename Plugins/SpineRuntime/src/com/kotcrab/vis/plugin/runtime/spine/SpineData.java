package com.kotcrab.vis.plugin.runtime.spine;

import com.kotcrab.vis.runtime.api.data.EntityData;

public class SpineData extends EntityData<SpineEntity> {
	@Override
	public void saveFrom (SpineEntity entity) {
		id = entity.getId();
	}

	@Override
	public void loadTo (SpineEntity entity) {
		entity.setId(id);
	}
}
