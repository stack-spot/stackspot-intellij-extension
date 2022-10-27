/*
 * Copyright 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackspot.intellij.ui.toolwindow

import com.stackspot.model.Plugin
import com.stackspot.model.Stack
import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode

open class StackSpotTreeNode(
    userObject: Any? = null,
    val icon: Icon? = null,
    val stack: Stack? = null,
    val plugin: Plugin? = null,
    private val pluginsNotApplied: List<String>? = null
) : DefaultMutableTreeNode(userObject) {

    fun hasPluginDependency(): Boolean {
        return pluginsNotApplied?.isNotEmpty() ?: false
    }

    fun pluginsNotAppliedToString(isHtml: Boolean  = false): StringBuilder {
        val newLine = if (isHtml) "<br>" else "\n"
        val stringBuilder = StringBuilder()
        pluginsNotApplied?.forEach { pa ->
            stringBuilder.append("- $pa $newLine")
        }
        return stringBuilder
    }
}