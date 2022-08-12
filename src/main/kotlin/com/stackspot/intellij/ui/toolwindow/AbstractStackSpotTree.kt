/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.getTreePath
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.render.RenderingUtil
import com.intellij.util.ui.UIUtil
import com.stackspot.intellij.services.StackSpotToolWindowService
import java.awt.Color
import java.util.function.Supplier
import javax.swing.JTree
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

abstract class AbstractStackSpotTree(val service: StackSpotToolWindowService) : JTree(StackSpotTreeNode()),
    TreeExpansionListener {

    protected val root = model.root as StackSpotTreeNode
    private val expandedPaths = LinkedHashSet<TreePath>()
    lateinit var scrollPane: JBScrollPane

    init {
        val renderer = StackSpotCellRenderer(this)
        setCellRenderer(renderer)
        setCellEditor(StackSpotCellEditor(renderer))
        reload()
        addTreeExpansionListener(this)
        isRootVisible = false
        isEditable = true
        isLargeModel = true
        setRowHeight(30)
        putClientProperty(RenderingUtil.CUSTOM_SELECTION_BACKGROUND, object : Supplier<Color> {
            override fun get(): Color {
                return UIUtil.getTreeSelectionBackground(true)
            }
        })
    }

    private fun reload() {
        root.removeAllChildren()
        addNodes()
        expandPath(TreePath(root.path))
        val treeModel = model as DefaultTreeModel
        treeModel.reload()
        expandNodes()
    }

    abstract fun addNodes()

    private fun expandNodes() {
        val stackSpotCellEditor = cellEditor as StackSpotCellEditor
        val myExpandedPaths = LinkedHashSet<TreePath>(expandedPaths)
        myExpandedPaths.forEach {
            val userObject = (it.lastPathComponent as StackSpotTreeNode).userObject
            val path = treeModel.getTreePath(userObject)
            if (path == null) {
                expandedPaths.remove(it)
            } else if (path != it) {
                expandedPaths.remove(it)
                expandedPaths.add(path)
            }
            expandPath(path)
        }
        val userObject = stackSpotCellEditor.selectedUserObject
        if (userObject != null) {
            val path = treeModel.getTreePath(userObject)
            if (path != null) {
                ApplicationManager.getApplication().invokeLater {
                    scrollPathToVisible(path)
                    startEditingAtPath(path)
                }
            }
        }

    }

    private fun resetProjectInService() {
        service.reloadProjectData()
    }

    fun notifyChange() {
        ApplicationManager.getApplication().invokeLater {
            resetProjectInService()
            reload()
        }
    }

    override fun treeExpanded(event: TreeExpansionEvent?) {
        if (event != null) {
            expandedPaths.add(event.path)
        }
    }

    override fun treeCollapsed(event: TreeExpansionEvent?) {
        if (event != null) {
            expandedPaths.remove(event.path)
        }
    }
}