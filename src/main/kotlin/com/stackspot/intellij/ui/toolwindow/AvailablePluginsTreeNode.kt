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

import com.stackspot.intellij.services.StackSpotToolWindowService
import com.stackspot.intellij.ui.Icons
import com.stackspot.model.Plugin
import com.stackspot.model.Stack

class AvailablePluginsTreeNode(private val service: StackSpotToolWindowService) :
    StackSpotTreeNode("Available Plugins", Icons.AVAILABLE_PLUGINS) {
    init {
        addPlugins()
    }

    private fun addPlugins() {
        val availablePlugins = getAvailablePlugins()
        availablePlugins.forEach { entry ->
            val stack = entry.key
            val plugins = entry.value
            val stackNode = StackSpotTreeNode(stack, null, stack, null)
            add(stackNode)
            plugins.forEach { plugin ->
                val pluginNode = StackSpotTreeNode(plugin, null, stack, plugin)
                stackNode.add(pluginNode)
            }
        }
    }

    private fun getAvailablePlugins(): Map<Stack, List<Plugin>> {
        val history = service.history
        if (history != null) {
            return history.getCompatiblePlugins()
        }
        val availablePlugins = LinkedHashMap<Stack, List<Plugin>>()
        service.importedStacks.list().forEach { stack ->
            availablePlugins[stack] = stack.listPlugins()
        }
        return availablePlugins
    }
}