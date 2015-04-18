package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.plugin.spine.runtime.SpineEntity;

public class SpineObject extends SpineEntity implements EditorObject {
	private SkeletonBounds bounds;
	private Rectangle rect;

	public SpineObject (String id, String atlasPath, String skeletonPath, Skeleton skeleton) {
		super(id, atlasPath, skeletonPath, skeleton);
		bounds = new SkeletonBounds();
		rect = new Rectangle();
	}

	@Override
	public float getWidth () {
		return bounds.getWidth();
	}

	@Override
	public float getHeight () {
		return bounds.getHeight();
	}

	@Override
	public void render (Batch batch) {
		bounds.update(skeleton, true);
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return rect.set(getX(), getY(), getWidth(), getHeight());
	}
}
