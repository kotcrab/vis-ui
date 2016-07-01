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

/**
 * An inverse kinematics objects which defines a constraint for a {@link IKResolver}.
 * @author Trixt0r
 */
public class IKObject extends Point {

	int chainLength, iterations;

	/**
	 * Creates a new IKObject with the given constraints.
	 * @param x x coordinate constraint
	 * @param y y coordinate constraint
	 * @param length the chain length constraint.
	 * @param iterations the number of iterations.
	 */
	public IKObject (float x, float y, int length, int iterations) {
		super(x, y);
		this.setLength(length);
		this.setIterations(iterations);
	}

	/**
	 * Sets the chain length of this ik object.
	 * The chain length indicates how many parent bones should get affected, when a {@link IKResolver} resolves the constraints.
	 * @param chainLength the chain length
	 * @return this ik object for chained operations
	 * @throws SpriterException if the chain length is smaller than 0
	 */
	public IKObject setLength (int chainLength) {
		if (chainLength < 0) throw new SpriterException("The chain has to be at least 0!");
		this.chainLength = chainLength;
		return this;
	}

	/**
	 * Sets the number of iterations.
	 * The more iterations a {@link IKResolver} is asked to do, the more precise the result will be.
	 * @param iterations number of iterations
	 * @return this ik object for chained operations
	 * @throws SpriterException if the number of iterations is smaller than 0
	 */
	public IKObject setIterations (int iterations) {
		if (iterations < 0) throw new SpriterException("The number of iterations has to be at least 1!");
		this.iterations = iterations;
		return this;
	}

	/**
	 * Returns the current set chain length.
	 * @return the chain length
	 */
	public int getChainLength () {
		return this.chainLength;
	}

	/**
	 * Returns the current set number of iterations.
	 * @return the number of iterations
	 */
	public int getIterations () {
		return this.iterations;
	}

}
