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

package com.kotcrab.vis.editor.util.gdx;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.Particle;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.Field;

public class ParticleUtils {

	//TODO add in settings: use additive mode for particles
	public static Rectangle calculateBoundingRectangle (ParticleEffect effect, Rectangle bounds, boolean additive) {
		Rectangle tempBounds = new Rectangle();

		if (additive == false) bounds.set(0, 0, 0, 0);
		for (int i = 0; i < effect.getEmitters().size; i++) {
			calculateBoundingRectangle(effect.getEmitters().get(i), tempBounds);

			if (isZero(tempBounds) == false) {
				if (isZero(bounds))
					bounds.set(tempBounds);
				else
					bounds.merge(tempBounds);
			}
		}

		return bounds;
	}

	private static boolean isZero (Rectangle rect) {
		return (rect.x == 0 && rect.y == 0 && rect.width == 0 && rect.height == 0);
	}

	/**
	 * Uses reflection (yep) to calculate current bounding rectangle for emitter. Should be uses instead of emitter.getBoundingBox() because
	 * it often returns BoundingBox with Infinity as values making it useless.
	 */
	public static Rectangle calculateBoundingRectangle (ParticleEmitter emitter, Rectangle bounds) {
		try {
			Field fieldParticles = emitter.getClass().getDeclaredField("particles");
			fieldParticles.setAccessible(true);

			Field fieldActive = emitter.getClass().getDeclaredField("active");
			fieldActive.setAccessible(true);

			Particle[] particles = (Particle[]) fieldParticles.get(emitter);
			boolean[] active = (boolean[]) fieldActive.get(emitter);

			if (particles.length == 0)
				return bounds.set(0, 0, 0, 0);

			int startIndex = 0;

			//find first active particle and set first bounding box
			for (; startIndex < particles.length; startIndex++) {
				if (active[startIndex]) {
					bounds.set(particles[startIndex].getBoundingRectangle());
					break;
				}

				return bounds.set(0, 0, 0, 0);
			}

			//merge other active particles
			for (int i = startIndex; i < particles.length; i++) {
				if (active[i]) {
					Particle particle = particles[i];
					bounds.merge(particle.getBoundingRectangle());
				}
			}

			return bounds;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
