/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor;

/** Represents edit action made in editor.
 * 
 * @author Pawel Pastuszak */
public class EditorAction {
	private ObjectRepresentation orep;
	private float x;
	private float y;
	private float scaleX;
	private float scaleY;
	private float originX;
	private float originY;
	private float width;
	private float height;
	private float rotation;

	public EditorAction (ObjectRepresentation orep) {
		this.orep = orep;

		x = orep.getX();
		y = orep.getY();
		scaleX = orep.getScaleX();
		scaleY = orep.getScaleX();
		originX = orep.getOriginX();
		originY = orep.getOriginY();
		width = orep.getWidth();
		height = orep.getHeight();
		rotation = orep.getRotation();
	}

	public void switchValues () {
		float xTemp = orep.getX();
		float yTemp = orep.getY();
		float scaleXTemp = orep.getScaleX();
		float scaleYTemp = orep.getScaleX();
		float originXTemp = orep.getOriginX();
		float originYTemp = orep.getOriginY();
		float widthTemp = orep.getWidth();
		float heightTemp = orep.getHeight();
		float rotationTemp = orep.getRotation();

		orep.setX(x);
		orep.setY(y);
		orep.setScale(scaleX, scaleY);
		orep.setSize(width, height);
		orep.setOrigin(originX, originY);
		orep.setRotation(rotation);

		x = xTemp;
		y = yTemp;
		scaleX = scaleXTemp;
		scaleY = scaleYTemp;
		originX = originXTemp;
		originY = originYTemp;
		width = widthTemp;
		height = heightTemp;
		rotation = rotationTemp;
	}
}
