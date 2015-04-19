package com.kotcrab.vis.plugin.spine.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.spine.*;
import com.kotcrab.vis.runtime.entity.Entity;

public class SpineEntity extends Entity {
	protected String atlasPath;

	protected transient Skeleton skeleton;
	protected transient AnimationState state;
	private transient SkeletonRenderer renderer; //TODO shared renderer

	public SpineEntity (String id, String atlasPath, String skeletonPath, SkeletonData skeletonData) {
		super(id);
		this.atlasPath = atlasPath;
		setAssetPath(skeletonPath);

		init(skeletonData);
	}

	public SpineEntity (SpineEntity original) {
		super(original.getId());
		this.atlasPath = original.atlasPath;
		setAssetPath(original.getAssetPath());
		init(original.getSkeleton().getData());
	}

	public void onDeserialize (SkeletonData skeletonData) {
		init(skeletonData);
	}

	protected void init (SkeletonData skeletonData) {
		skeleton = new Skeleton(skeletonData);

		AnimationStateData stateData = new AnimationStateData(skeletonData);
		state = new AnimationState(stateData);

		renderer = new SkeletonRenderer();
	}

	@Override
	public void render (Batch batch) {
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
		skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
		renderer.draw(batch, skeleton); // Draw the skeleton images.
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

	public Skeleton getSkeleton () {
		return skeleton;
	}
}
