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
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Timer
import com.kotcrab.vis.editor.module.ContentTable

class RepeatableTimedKey(private val stage: Stage, private val key: Int, private val doAction: () -> Any) : Timer.Task() {
    private val keyRepeatTime = 0.05f

    fun update() {
        if (Gdx.input.isKeyPressed(key) == false) {
            cancel()
            return
        }
        if (isScheduled) return
        if (stage.keyboardFocus != null && stage.keyboardFocus !is ContentTable) return
        run()
        Timer.schedule(this, keyRepeatTime, keyRepeatTime)
    }

    override fun run() {
        doAction()
    }
}
