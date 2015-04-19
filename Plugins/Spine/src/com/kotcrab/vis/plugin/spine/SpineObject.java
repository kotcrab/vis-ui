package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.*;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.plugin.spine.runtime.SpineEntity;

public class SpineObject extends SpineEntity implements EditorObject {
	private Rectangle bounds;

	public SpineObject (String atlasPath, String skeletonPath, SkeletonData skeletonData) {
		super(null, atlasPath, skeletonPath, skeletonData);
		bounds = new Rectangle();
	}

	public SpineObject (SpineObject original) {
		super(original);
		this.bounds = original.bounds;
	}

	@Override
	public boolean isFlipSupported () {
		return true;
	}

	@Override
	public float getWidth () {
		return bounds.width;
	}

	@Override
	public float getHeight () {
		return bounds.height;
	}

	@Override
	public Rectangle getBoundingRectangle () {
		computeBoundingRectangle();
		return bounds;
	}

	private void computeBoundingRectangle () {
		Array<Slot> slots = skeleton.getSlots();
		bounds = null;

		for (Slot slot : slots) {
			Attachment attachment = slot.getAttachment();
			if (attachment == null) continue;

			float[] vertices = null;
			if (attachment instanceof BoundingBoxAttachment)
				vertices = ((BoundingBoxAttachment) attachment).getVertices();
			if (attachment instanceof RegionAttachment)
				vertices = ((RegionAttachment) attachment).getWorldVertices();
			if (attachment instanceof SkinnedMeshAttachment)
				vertices = ((SkinnedMeshAttachment) attachment).getWorldVertices();
			if (attachment instanceof MeshAttachment)
				vertices = ((MeshAttachment) attachment).getWorldVertices();

			if (vertices == null) continue;

			float minX = vertices[0];
			float minY = vertices[1];
			float maxX = vertices[0];
			float maxY = vertices[1];

			for (int i = 5; i < vertices.length; i += 5) {
				minX = minX > vertices[i] ? vertices[i] : minX;
				minY = minY > vertices[i + 1] ? vertices[i + 1] : minY;
				maxX = maxX < vertices[i] ? vertices[i] : maxX;
				maxY = maxY < vertices[i + 1] ? vertices[i + 1] : maxY;
			}

			Rectangle partBounds = new Rectangle();
			partBounds.x = minX;
			partBounds.y = minY;
			partBounds.width = maxX - minX;
			partBounds.height = maxY - minY;

			if (bounds == null)
				bounds = partBounds;
			else
				bounds.merge(partBounds);
		}
	}
}
