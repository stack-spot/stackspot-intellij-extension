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

import com.stackspot.intellij.services.StackSpotToolWindowService
import com.stackspot.intellij.ui.Icons
import com.stackspot.model.AppliedTemplate

class ApplicationDetailsTreeNode(private val service: StackSpotToolWindowService) :
    StackSpotTreeNode("Application Details", Icons.APPLICATION_DETAILS) {

    init {
        addApplicationType()
        addTemplate()
        addAppliedPlugins()
        addGlobalInputs()
    }

    private fun addApplicationType() {
        val history = service.history
        if (history != null) {
            add(StackSpotTreeNode("Type: ${history.stackType}"))
        }
    }

    private fun addTemplate() {
        val template = service.template
        if (template != null && template.templateDataPath != "") {
            val templateNode = StackSpotTreeNode("Template: ${template.toString(service.importedStacks)}")
            addInputValues(template.inputs, templateNode)
            add(templateNode)
        }
    }

    private fun addInputValues(
        inputs: Map<String, Any>?,
        templateNode: StackSpotTreeNode,
        title: String = "Input Values"
    ) {
        if (inputs != null && inputs.isNotEmpty()) {
            templateNode.add(InputValuesTreeNode(inputs, title))
        }
    }

    private fun addAppliedPlugins() {
        val plugins = service.appliedPlugins
        if (plugins.isNotEmpty()) {
            val appliedPluginsNode = StackSpotTreeNode("Applied Plugins")
            addPluginsToAppliedPlugins(appliedPluginsNode, plugins)
            add(appliedPluginsNode)
        }
    }

    private fun addPluginsToAppliedPlugins(appliedPluginsNode: StackSpotTreeNode, plugins: List<AppliedTemplate>) {
        plugins.forEach {
            val pluginNode = StackSpotTreeNode(it.toString(service.importedStacks))
            addInputValues(it.inputs, pluginNode)
            appliedPluginsNode.add(pluginNode)
        }
    }

    private fun addGlobalInputs() {
        val history = service.history
        if (history != null) {
            addInputValues(history.globalInputs, this, "Global Inputs")
        }
    }
}