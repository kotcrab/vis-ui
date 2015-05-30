/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

public interface EditorObject extends Disposable {
	String getId ();

	void setId (String id);

	float getX ();

	void setX (float x);

	float getY ();

	void setY (float y);

	void setPosition (float x, float y);

	float getWidth ();

	float getHeight ();

	Rectangle getBoundingRectangle ();

	void render (Batch batch);

	default boolean isResizeSupported () {
		return false;
	}

	default void setSize (float width, float height) {

	}

	default boolean isOriginSupported () {
		return false;
	}

	default float getOriginX () {
		return 0;
	}

	default float getOriginY () {
		return 0;
	}

	default void setOrigin (float x, float y) {

	}

	default boolean isScaleSupported () {
		return false;
	}

	default float getScaleX () {
		return 0;
	}

	default float getScaleY () {
		return 0;
	}

	default void setScale (float x, float y) {

	}

	default boolean isTintSupported () {
		return false;
	}

	default Color getColor () {
		return null;
	}

	default void setColor (Color color) {

	}

	default boolean isRotationSupported () {
		return false;
	}

	default float getRotation () {
		return 0;
	}

	default void setRotation (float rotation) {

	}

	default boolean isFlipSupported () {
		return false;
	}

	default boolean isFlipX () {
		return false;
	}

	default boolean isFlipY () {
		return false;
	}

	default void setFlip (boolean x, boolean y) {

	}

	default String toPrettyString () {
		if (getId() == null)
			return getClass().getSimpleName() + " X: " + (int) getX() + " Y: " + (int) getY();
		else
			return getClass().getSimpleName() + " ID: " + getId() + " X: " + (int) getX() + " Y: " + (int) getY();
	}

	@Override
	default void dispose () {

	}

	VisAssetDescriptor getAssetDescriptor ();

	void setAssetDescriptor (VisAssetDescriptor assetDescriptor);

	default void checkAssetDescriptor (VisAssetDescriptor assetDescriptor) {
		if (isAssetsDescriptorSupported(assetDescriptor) == false)
			throw new UnsupportedAssetDescriptorException(assetDescriptor);
	}

	boolean isAssetsDescriptorSupported (VisAssetDescriptor assetDescriptor);
}
