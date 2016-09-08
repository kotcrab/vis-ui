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

package com.kotcrab.vis.editor.util.gdx

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3

/**
 * Camera controller that supports zooming around point.
 * @author Kotcrab
 */
class CameraZoomController(private val camera: OrthographicCamera, private val unprojectVec: Vector3) {
    private companion object {
        val MIN_ZOOM = 0.3f
        val MAX_ZOOM = 15f
    }

    /**
     * Zooms camera around given point.
     * @param x screen x
     * @param y screen y
     * @param zoomIn if true then zoomIn will be performed
     */
    fun zoomAroundPoint(x: Float, y: Float, zoomIn: Boolean): Boolean {
        camera.unproject(unprojectVec.set(x, y, 0f))
        val cursorX = unprojectVec.x
        val cursorY = unprojectVec.y

        val (zoomPossible, newZoom) = calcNewZoom(zoomIn)
        if (zoomPossible == false) return false

        // some complicated calculations, basically we want to zoom in/out where mouse pointer is
        camera.position.x = cursorX + newZoom / camera.zoom * (camera.position.x - cursorX)
        camera.position.y = cursorY + newZoom / camera.zoom * (camera.position.y - cursorY)
        camera.zoom = newZoom
        return true
    }

    /** Zooms camera in. */
    fun zoomIn(): Boolean = zoom(true)

    /** Zooms camera out. */
    fun zoomOut(): Boolean = zoom(false)

    private fun zoom(zoomIn: Boolean): Boolean {
        val (zoomPossible, newZoom) = calcNewZoom(zoomIn)
        if (zoomPossible == false) return false
        camera.zoom = newZoom
        return true
    }

    private fun calcNewZoom(zoomIn: Boolean): Pair<Boolean, Float> {
        val newZoom: Float
        if (zoomIn) {
            if (camera.zoom <= MIN_ZOOM) return Pair(false, camera.zoom)
            newZoom = camera.zoom - 0.1f * camera.zoom * 2f
        } else {
            if (camera.zoom >= MAX_ZOOM) return Pair(false, camera.zoom)
            newZoom = camera.zoom + 0.1f * camera.zoom * 2f
        }
        return Pair(true, newZoom)
    }
}
