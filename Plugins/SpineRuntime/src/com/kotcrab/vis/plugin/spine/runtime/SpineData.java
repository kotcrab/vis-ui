package com.kotcrab.vis.plugin.spine.runtime;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.runtime.data.EntityData;

public class SpineData extends EntityData<SpineEntity> {
	public String skeletonPath;
	public String atlasPath;
	public float x, y;
	public boolean flipX, flipY;
	public Color color;

	public float scale;

	@Override
	public void saveFrom (SpineEntity entity) {
		id = entity.getId();
		skeletonPath = entity.getAssetPath();
		atlasPath = entity.getAtlasPath();
		x = entity.getX();
		y = entity.getY();
		flipX = entity.isFlipX();
		flipY = entity.isFlipY();
	}

	@Override
	public void loadTo (SpineEntity entity) {
		entity.setId(id);
		entity.setAssetPath(skeletonPath);
		entity.setAtlasPath(atlasPath);
		entity.setPosition(x, y);
		entity.setFlip(flipX, flipY);
	}
}
