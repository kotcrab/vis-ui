/*
 * Copyright 2014-2016 See AUTHORS file.
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

package com.brashmonkey.spriter;

import com.brashmonkey.spriter.Mainline.Key.BoneRef;
import com.brashmonkey.spriter.Timeline.Key.Bone;

/**
 * An inverse kinematics resolver implementation.
 * An instance of this class uses the CCD (Cyclic Coordinate Descent) algorithm to resolve the constraints.
 * @author Trixt0r
 * @see <a href="https://sites.google.com/site/auraliusproject/ccd-algorithm">ccd-algorithm</a>
 * and <a href="http://www.ryanjuckett.com/programming/cyclic-coordinate-descent-in-2d/">cyclic-coordinate-descent-in-2d</a> .
 */
public class CCDResolver extends IKResolver {

	public CCDResolver (Player player) {
		super(player);
	}

	@Override
	public void resolve (float x, float y, int chainLength, BoneRef effectorRef) {
		//player.unmapObjects(null);
		Timeline timeline = player.animation.getTimeline(effectorRef.timeline);
		Timeline.Key key = player.tweenedKeys[effectorRef.timeline];
		Timeline.Key unmappedKey = player.unmappedTweenedKeys[effectorRef.timeline];
		Bone effector = key.object();
		Bone unmappedffector = unmappedKey.object();
		float width = (timeline.objectInfo != null) ? timeline.objectInfo.size.width : 200;
		width *= unmappedffector.scale.x;
		float xx = unmappedffector.position.x + (float) Math.cos(Math.toRadians(unmappedffector.angle)) * width,
				yy = unmappedffector.position.y + (float) Math.sin(Math.toRadians(unmappedffector.angle)) * width;
		if (Calculator.distanceBetween(xx, yy, x, y) <= this.tolerance)
			return;

		effector.angle = Calculator.angleBetween(unmappedffector.position.x, unmappedffector.position.y, x, y);
		if (Math.signum(player.root.scale.x) == -1) effector.angle += 180f;
		BoneRef parentRef = effectorRef.parent;
		Bone parent = null, unmappedParent = null;
		if (parentRef != null) {
			parent = player.tweenedKeys[parentRef.timeline].object();
			unmappedParent = player.unmappedTweenedKeys[parentRef.timeline].object();
			effector.angle -= unmappedParent.angle;
		}
		player.unmapObjects(null);
		for (int i = 0; i < chainLength && parentRef != null; i++) {
			if (Calculator.distanceBetween(xx, yy, x, y) <= this.tolerance)
				return;
			parent.angle += Calculator.angleDifference(Calculator.angleBetween(unmappedParent.position.x, unmappedParent.position.y, x, y),
					Calculator.angleBetween(unmappedParent.position.x, unmappedParent.position.y, xx, yy));
			parentRef = parentRef.parent;
			if (parentRef != null && i < chainLength - 1) {
				parent = player.tweenedKeys[parentRef.timeline].object();
				unmappedParent = player.unmappedTweenedKeys[parentRef.timeline].object();
				parent.angle -= unmappedParent.angle;
			} else parent = null;
			player.unmapObjects(null);
			xx = unmappedffector.position.x + (float) Math.cos(Math.toRadians(unmappedffector.angle)) * width;
			yy = unmappedffector.position.y + (float) Math.sin(Math.toRadians(unmappedffector.angle)) * width;
		}
	}

}
