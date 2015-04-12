package com.kotcrab.vis.plugin.runtime.spine;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.spine.Skeleton;
import com.kotcrab.vis.runtime.entity.Entity;

public class SpineEntity extends Entity{
	private String atlasPath;

	protected Skeleton skeleton;

	public SpineEntity (String id, String atlasPath, String skeletonPath, Skeleton skeleton) {
		super(id);
		this.atlasPath = atlasPath;
		setAssetPath(skeletonPath);
	}

	public String getAtlasPath () {
		return atlasPath;
	}

	public void setAtlasPath (String atlasPath) {
		this.atlasPath = atlasPath;
	}

	public float getX () {
		return skeleton.getX();
	}

	public void setX (float x) {
		skeleton.setX(x);
	}

	public float getY () {
		return skeleton.getY();
	}

	public void setY (float y) {
		skeleton.setY(y);
	}

	public void setPosition (float x, float y) {
		skeleton.setPosition(x, y);
	}

	public void setFlip (boolean flipX, boolean flipY) {
		skeleton.setFlip(flipX, flipY);
	}

	public void setFlipY (boolean flipY) {
		skeleton.setFlipY(flipY);
	}

	public boolean isFlipY () {
		return skeleton.getFlipY();
	}

	public void setFlipX (boolean flipX) {
		skeleton.setFlipX(flipX);
	}

	public boolean isFlipX () {
		return skeleton.getFlipX();
	}

	public void setColor (Color color) {
		skeleton.setColor(color);
	}

	public Color getColor () {
		return skeleton.getColor();
	}
}
