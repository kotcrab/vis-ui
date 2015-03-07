/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.Particle;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.Field;

public class ParticleUtils {
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
