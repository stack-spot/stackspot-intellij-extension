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

package com.stackspot.model

import com.stackspot.jackson.JacksonExtensions
import com.stackspot.model.cli.CliPlugin
import com.stackspot.model.cli.CliStack
import com.stackspot.model.cli.CliStackfile
import com.stackspot.model.cli.CliTemplate

object UtilModelMock {

    fun createFullCliMock(
        stackName: String = "stack 2",
        stackPath: String = "stack_2.yaml",
        stackfileName: String = "stackfile 2",
        templateName: String = "template 2",
        pluginName: String = "plugin 2"
    ): Quadruple<List<CliStack>, Map<String, List<CliStackfile>>,
            Map<String, List<CliTemplate>>, Map<String, List<CliPlugin>>> {

        val stackPathList = mutableListOf<CliStack>()
        stackPathList.addAll(listOf(createCliStack(), createCliStack(name = stackName, path = stackPath)))

        val stackfilePathMap = mutableMapOf<String, List<CliStackfile>>()
        stackfilePathMap["stack 1"] =
            listOf(createCliStackfile(), createCliStackfile(name = stackfileName, path = "stackfile_2.yaml"))

        val templatePathMap = mutableMapOf<String, List<CliTemplate>>()
        templatePathMap["stack 1"] =
            listOf(createCliTemplate(), createCliTemplate(name = templateName, path = "template_2.yaml"))

        val pluginPathMap = mutableMapOf<String, List<CliPlugin>>()
        pluginPathMap["stack 1"] = listOf(createCliPlugin(), createCliPlugin(name = pluginName, path = "plugin_2"))

        return Quadruple(stackPathList, stackfilePathMap, templatePathMap, pluginPathMap)

    }

    fun createCliStack(
        description: String = "Stack description",
        name: String = "Stack name",
        path: String = "stack.yaml"
    ) = CliStack(description, name, path)

    fun createCliStackfile(
        description: String = "Stackfile description",
        name: String = "Stackfile name",
        path: String = "stackfile.yaml"
    ) = CliStackfile(description, name, path)

    fun createCliTemplate(
        description: String = "Template description",
        name: String = "Template name",
        path: String = "template.yaml",
        types: List<String> = listOf("app")
    ) = CliTemplate(description, name, path, types)

    fun createCliPlugin(
        description: String = "Plugin description",
        name: String = "Plugin name",
        path: String = "plugin.yaml",
        types: List<String> = listOf("app")
    ) = CliPlugin(description, name, path, types)

    fun createStackfile(
        type: String = "Type",
        description: String = "Plugin 1 description",
        template: String = "Template",
        inputs: Map<String, Any>? = null,
        plugins: List<StackfilePlugin>? = listOf(createStackfilePlugin())
    ): Stackfile {
        val stackfile = Stackfile(type, description, template, inputs, plugins)
        stackfile.name = "Stackfile name"
        return stackfile
    }

    private fun createStackfilePlugin(
        plugin: String = "PLugin",
        inputs: Map<String, Any>? = null
    ) = StackfilePlugin(plugin, inputs)

    fun createPlugin(
        name: String = "Plugin 1",
        description: String = "Plugin 1 description",
        types: List<String> = listOf("app"),
        inputs: List<Input> = listOf(createInput()),
        displayName: String = "Display name",
        displayNameKebab: String = "Display name kebab"
    ) = Plugin(name, description, types, inputs, displayName, displayNameKebab)

    fun createTemplate(
        name: String = "Template 1",
        description: String = "Template 1 description",
        types: List<String> = listOf("app"),
        inputs: List<Input> = listOf(createInput()),
        displayName: String = "Display name",
        displayNameKebab: String = "Display name kebab"
    ) = Plugin(name, description, types, inputs, displayName, displayNameKebab)

    private fun createInput(
        type: String = "type",
        label: String = "label",
        name: String = "name",
        default: Any? = null,
        condition: Condition? = createCondition()
    ) = Input(type, label, name, default, condition)

    private fun createCondition(
        variable: String = "Variable",
        operator: String = "Operator",
        value: String = "Value"
    ) = Condition(variable, operator, value)
}

data class Quadruple<A, B, C, D>(
    val stack: A,
    val stackfile: B,
    val template: C,
    val plugin: D
)