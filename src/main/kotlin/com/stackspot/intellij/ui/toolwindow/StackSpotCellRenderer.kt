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

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.ColorUtil
import com.intellij.util.ui.UIUtil
import com.stackspot.intellij.commands.CommandRunner
import com.stackspot.intellij.commands.listeners.NotifyStacksUpdatedCommandListener
import com.stackspot.intellij.commands.stk.ApplyPlugin
import com.stackspot.intellij.commands.stk.DeleteStack
import com.stackspot.intellij.commands.stk.UpdateStack
import com.stackspot.intellij.ui.Icons
import com.stackspot.intellij.ui.toolwindow.panels.PluginInputsPanel
import com.stackspot.model.Condition
import com.stackspot.model.Input
import java.awt.*
import javax.swing.*
import javax.swing.tree.DefaultTreeCellRenderer

private const val COULD_NOT_APPLY_PLUGIN = "Could not apply plugin"
private const val PLUGIN_HAS_DEPENDENCY = "This plugin has dependencies. First, apply these before proceeding:"

class StackSpotCellRenderer(val tree: AbstractStackSpotTree) : DefaultTreeCellRenderer() {

    private val panel = object : JPanel() {
        override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
            val scroll = tree.scrollPane.verticalScrollBar
            val scrollWidth = if (scroll.isVisible) {
                scroll.width
            } else {
                0
            }
            super.setBounds(x, y, tree.scrollPane.width - x - scrollWidth, height)
        }
    }

    init {
        panel.layout = BorderLayout(5, 0)
        panel.isOpaque = false
    }

    override fun getTreeCellRendererComponent(
        tree: JTree?,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        panel.removeAll()
        val stackSpotNode = value as StackSpotTreeNode
        createIcon(stackSpotNode, selected, hasFocus)
        createLabel(stackSpotNode, selected, hasFocus)
        createButtons(stackSpotNode, selected, hasFocus, tree)
        setTooltipText(stackSpotNode)
        return panel
    }

    private fun setTooltipText(stackSpotNode: StackSpotTreeNode) {
        panel.toolTipText = if (stackSpotNode.stack != null && stackSpotNode.plugin == null) {
            stackSpotNode.stack.description
        } else if (stackSpotNode.plugin != null) {
            stackSpotNode.plugin.description
        } else {
            null
        }
    }

    private fun createButtons(
        stackSpotNode: StackSpotTreeNode,
        selected: Boolean,
        hasFocus: Boolean,
        tree: JTree?
    ) {
        val buttonsPanel = JPanel()
        buttonsPanel.isOpaque = false
        if (stackSpotNode.plugin != null && hasFocus) {
            val applyPluginButton = createApplyPluginButton(stackSpotNode, tree, selected)
            buttonsPanel.add(applyPluginButton)
        } else if (stackSpotNode.userObject is ImportedStack && hasFocus) {
            val updateStackButton = createUpdateStackButton(stackSpotNode, tree, selected)
            val deleteStackButton = createDeleteStackButton(stackSpotNode, tree, selected)
            buttonsPanel.add(updateStackButton)
            buttonsPanel.add(deleteStackButton)
        }
        panel.add(buttonsPanel, BorderLayout.EAST)
    }

    private fun createApplyPluginButton(
        stackSpotNode: StackSpotTreeNode,
        tree: JTree?,
        selected: Boolean
    ): JButton {
        val button = createButton("Apply Plugin", getIcon(Icons.APPLY_PLUGIN, selected, true))
        button.addActionListener {
            val stackSpotTree = (tree as AbstractStackSpotTree)
            val project = stackSpotTree.service.project
            if (stackSpotNode.stack != null && stackSpotNode.plugin != null && project != null) {
                if (stackSpotNode.isItPluginDependent()) {
                    val requirements = stackSpotNode.pluginsNotAppliedToString()
                    Messages.showWarningDialog("$PLUGIN_HAS_DEPENDENCY\n$requirements", COULD_NOT_APPLY_PLUGIN)
                } else {
                    if (stackSpotNode.plugin.inputs != null) {
                        PluginInputsPanel(stackSpotNode.plugin.inputs).showAndGet()
                    }
                    ApplyPlugin(stackSpotNode.stack, stackSpotNode.plugin, project)
                        .run(object : CommandRunner.CommandEndedListener {
                            override fun notifyEnded() {
                                stackSpotTree.notifyChange()
                            }
                        })
                }
            }
        }
        return button
    }

    private fun createLabel(stackSpotNode: StackSpotTreeNode, selected: Boolean, focused: Boolean) {
        val label = JLabel(stackSpotNode.toString())
        label.foreground = getLabelForeground(selected, focused)
        panel.add(label, BorderLayout.CENTER)
    }

    private fun getLabelForeground(selected: Boolean, focused: Boolean): Color {
        if (UIUtil.isUnderIntelliJLaF()) {
            if (!selected && focused || selected && !focused) {
                return UIUtil.getTreeForeground(true, true)
            }
        }
        return UIUtil.getTreeForeground(selected, focused)
    }

    private fun createIcon(stackSpotNode: StackSpotTreeNode, selected: Boolean, focused: Boolean) {
        if (stackSpotNode.icon != null) {
            val icon = getIcon(stackSpotNode.icon, selected, focused)
            val jLabel = JLabel(icon)
            if (stackSpotNode.plugin != null && stackSpotNode.isItPluginDependent()) {
                jLabel.toolTipText =
                    "This plugin has dependencies. First, apply these before proceeding: <br>" +
                            "${stackSpotNode.pluginsNotAppliedToString(isHtml = true)}"
            }
            panel.add(jLabel, BorderLayout.WEST)
        }
    }

    private fun getIcon(myIcon: Icon, selected: Boolean, focused: Boolean): Icon {
        val color = getLabelForeground(selected, focused)
        val icon = if (!ColorUtil.isDark(color)) {
            IconLoader.getDarkIcon(myIcon, true)
        } else {
            myIcon
        }
        return icon
    }

    private fun createUpdateStackButton(
        stackSpotNode: StackSpotTreeNode,
        tree: JTree?,
        selected: Boolean
    ): JButton {
        val updateStackButton = createButton("Update Stack", getIcon(Icons.UPDATE_STACK, selected, true))
        updateStackButton.addActionListener {
            val project = (tree as AbstractStackSpotTree).service.project
            if (stackSpotNode.stack != null && project != null) {
                UpdateStack(
                    stackSpotNode.stack,
                    project,
                ).run(NotifyStacksUpdatedCommandListener())
            }
        }
        return updateStackButton
    }

    private fun createDeleteStackButton(
        stackSpotNode: StackSpotTreeNode,
        tree: JTree?,
        selected: Boolean
    ): JButton {
        val deleteStackButton = createButton("Delete Stack", getIcon(Icons.TRASH, selected, true))
        val stack = stackSpotNode.stack
        val project = (tree as AbstractStackSpotTree).service.project
        deleteStackButton.addActionListener {
            if (stack != null && project != null) {
                DeleteStack(
                    stack,
                    project,
                ).run(NotifyStacksUpdatedCommandListener())
            }
        }
        return deleteStackButton
    }

    private fun createButton(tooltipText: String, icon: Icon): JButton {
        val button = JButton(icon)
        button.isBorderPainted = false
        button.preferredSize = Dimension(20, 20)
        button.toolTipText = tooltipText
        button.isContentAreaFilled = false
        return button
    }
}