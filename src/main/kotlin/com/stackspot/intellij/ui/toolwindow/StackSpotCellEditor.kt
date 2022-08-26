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

import java.awt.Component
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JTree
import javax.swing.tree.DefaultTreeCellEditor

class StackSpotCellEditor(renderer: StackSpotCellRenderer) : DefaultTreeCellEditor(renderer.tree, renderer) {

    private val editRenderer = StackSpotCellRenderer(renderer.tree)
    var selectedUserObject: Any? = null

    override fun isCellEditable(anEvent: EventObject?): Boolean {
        val event = anEvent as MouseEvent?
        val source = anEvent?.source as JTree?
        if (event != null && source != null) {
            val path = source.getPathForLocation(event.x, event.y)
            val node = path.lastPathComponent as StackSpotTreeNode
            return node.userObject is ImportedStack || node.plugin != null
        }
        return false
    }

    override fun getTreeCellEditorComponent(
        tree: JTree?,
        value: Any?,
        isSelected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int
    ): Component {
        selectedUserObject = (value as StackSpotTreeNode).userObject
        return editRenderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true)
    }
}