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

package com.kotcrab.vis.editor.util

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.value.ConstantIfVisibleValue
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.VisTable

import com.badlogic.gdx.utils.Array as GdxArray

/** @author Kotcrab */

fun Table.widgetRow(vararg actors: Actor, managePreviousActorSpacing: Boolean = false): Cell<VisTable> {
    val table = VisTable(true)
    actors.forEach { table.add(it) }
    val cell = add(table)
    cell.height(PrefHeightIfVisibleValue.INSTANCE).spaceRight(ConstantIfVisibleValue(VisUI.getSizes().spacingRight))
    row()

    if (managePreviousActorSpacing == false) return cell

    val newTableIdx = children.indexOf(table, true)
    if (newTableIdx > 0) {
        getCell(this.children.get(newTableIdx - 1)).spaceBottom(ConstantIfVisibleValue(table, VisUI.getSizes().spacingBottom))
    }

    return cell
}

fun Actor.addTooltip(text: String): Tooltip {
    return Tooltip.Builder(text).target(this).width(300f).build()
}

inline fun Actor.addChangeListener(crossinline callback: (ChangeListener.ChangeEvent, Actor) -> Unit): ChangeListener {
    val listener = object : ChangeListener() {
        override fun changed(event: ChangeEvent, actor: Actor) = callback(event, actor)
    }
    this.addListener(listener)
    return listener
}

inline fun Actor.changed(crossinline callback: () -> Unit) {
    this.addListener(object : ChangeListener() {
        override fun changed(event: ChangeEvent, actor: Actor) = callback()
    })
}
