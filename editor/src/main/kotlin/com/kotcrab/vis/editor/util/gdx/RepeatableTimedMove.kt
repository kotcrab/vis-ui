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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.badlogic.gdx.utils.Timer
import com.kotcrab.vis.editor.module.ContentTable

class RepeatableTimedMove(private val stage: Stage,
                          private val pixelsPerUnit: Float,
                          private val shouldCancel: () -> Boolean,
                          private val doMove: (Float, Float) -> Unit) : Timer.Task() {
    var onCancel: () -> Unit = {}

    private val keyRepeatTime = 0.05f
    private var deltaX = 0f
    private var deltaY = 0f

    fun update() {
        if (shouldCancel()) {
            cancel()
        }
        updateDelta()

        if (deltaX == 0f && deltaY == 0f) {
            cancel()
            return
        }
        if (isScheduled) return
        if (stage.keyboardFocus != null && stage.keyboardFocus !is ContentTable) return
        run()
        Timer.schedule(this, keyRepeatTime, keyRepeatTime)
    }

    private fun updateDelta() {
        var delta = 20f
        if (UIUtils.shift()) delta *= 10f
        if (UIUtils.ctrl()) delta *= 10f

        delta /= pixelsPerUnit

        val up = Gdx.input.isKeyPressed(Input.Keys.UP)
        val down = Gdx.input.isKeyPressed(Input.Keys.DOWN)
        val left = Gdx.input.isKeyPressed(Input.Keys.LEFT)
        val right = Gdx.input.isKeyPressed(Input.Keys.RIGHT)

        if ((up && down) || (!up && !down)) {
            deltaY = 0f
        } else if (up) {
            deltaY = delta
        } else if (down) {
            deltaY = -delta
        }

        if ((left && right) || (!left && !right)) {
            deltaX = 0f
        } else if (right) {
            deltaX = delta
        } else if (left) {
            deltaX = -delta
        }
    }

    override fun run() {
        if (shouldCancel()) return
        doMove(deltaX, deltaY)
    }

    override fun cancel() {
        super.cancel()
        deltaX = 0f
        deltaY = 0f
        onCancel()
    }
}
