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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.ui.UIUtil
import com.stackspot.intellij.messaging.StackUpdatesNotifier
import com.stackspot.intellij.services.StackSpotToolWindowService
import javax.swing.JComponent


class StackSpotToolWindow(private val project: Project) {
    private val service: StackSpotToolWindowService = project.getService(StackSpotToolWindowService::class.java)

    init {
        service.project = project
        service.reloadProjectData()
    }

    private val projectTree = StackSpotProjectTree(service)
    private val catalogTree = StackSpotCatalogTree(service)
    private lateinit var yourProjectGroup: Row
    private val treePanel = panel {
        yourProjectGroup = group(" Your Project", false) {
            row {
                cell(projectTree).horizontalAlign(HorizontalAlign.FILL)
            }
        }.visible(false)
        group(" StackSpot Catalog", false) {
            row {
                cell(catalogTree).horizontalAlign(HorizontalAlign.FILL)
            }
        }.topGap(TopGap.NONE)
    }

    init {
        treePanel.background = UIUtil.getTreeBackground()
        projectTree.scrollPane = JBScrollPane(treePanel)
        catalogTree.scrollPane = projectTree.scrollPane
        observeStackYamlChanges()
        observeStackUpdatesEvents()
        configureSelectionListeners()
        configureYourProjectGroupVisibility()
    }

    private fun notifyChange() {
        projectTree.notifyChange()
        catalogTree.notifyChange()
        configureYourProjectGroupVisibility()
    }

    private fun configureYourProjectGroupVisibility() {
        ApplicationManager.getApplication().invokeLater {
            yourProjectGroup.visible(service.history != null)
        }
    }

    private fun configureSelectionListeners() {
        catalogTree.addTreeSelectionListener { e ->
            val node = (e?.source as AbstractStackSpotTree).lastSelectedPathComponent as StackSpotTreeNode?
            if (node != null) {
                projectTree.clearSelection()
            }
        }
        projectTree.addTreeSelectionListener { e ->
            val node = (e?.source as AbstractStackSpotTree).lastSelectedPathComponent as StackSpotTreeNode?
            if (node != null) {
                catalogTree.clearSelection()
            }
        }
    }

    private fun observeStackUpdatesEvents() {
        val application = ApplicationManager.getApplication()
        application.messageBus.connect().subscribe(
            StackUpdatesNotifier.TOPIC,
            object : StackUpdatesNotifier {
                override fun stacksUpdated() {
                    notifyChange()
                }

                override fun stackYamlUpdated() {
                    notifyChange()
                }
            })
    }

    private fun observeStackYamlChanges() {
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: MutableList<out VFileEvent>) {
                    events.forEach {
                        val file = it.file
                        if (file != null && file.name == "stk.yaml") {
                            notifyChange()
                        }
                    }
                }
            })
    }

    fun getContent(): JComponent {
        return projectTree.scrollPane
    }
}



