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

import com.fasterxml.jackson.annotation.JsonProperty
import com.stackspot.jackson.parsePluginYaml
import com.stackspot.jackson.parseStackfile
import com.stackspot.jackson.parseTemplateYaml
import com.stackspot.model.cli.CliPlugin
import com.stackspot.model.cli.CliStackfile
import com.stackspot.model.cli.CliTemplate
import java.io.File
import java.util.*

data class Stack(
    val name: String,
    val description: String,
    val displayName: String? = null,
    @JsonProperty("display-name") val displayNameKebab: String? = null,
    val useCases: List<StackUseCase>? = null,
    @JsonProperty("use-cases") val useCasesKebab: List<StackUseCase>? = null,
) {
    lateinit var location: File
    lateinit var pluginsMap: Map<String, List<CliPlugin>>
    lateinit var templatesMap: Map<String, List<CliTemplate>>
    lateinit var stackfilesMap: Map<String, List<CliStackfile>>

    fun filterPluginsByType(type: TemplateType): List<Plugin> {
        val pluginsList = pluginsMap.getOrDefault(name, listOf())
        return pluginsList
            .filter { isTemplateOfType(it, type.pluginType) }
            .map { it.path.parsePluginYaml(this) }
    }

    fun getTemplateByName(componentName: String): Template? {
        val pathsList = templatesMap.getOrDefault(this.name, listOf())
        return pathsList
            .filter { it.name == componentName  }
            .map { it.path.parseTemplateYaml(this) }
            .firstOrNull()
    }

    fun getPluginByName(componentName: String?): Plugin {
        val pluginsList = pluginsMap.getOrDefault(name, listOf())
        return pluginsList
            .filter{ it.name == componentName }
            .map { it.path.parsePluginYaml(this) }
            .first()
    }

    fun listStackfiles(
        filterByStack: Boolean = true
    ): List<Stackfile> {
        var pathsList = stackfilesMap.values.flatten()

        if (filterByStack) {
            pathsList = stackfilesMap.getOrDefault(name, listOf())
        }

        return pathsList.map { it.path.parseStackfile() }
    }

    fun listPlugins(): List<Plugin> {
        return filterPluginsByType(TemplateType.APP)
    }

    private fun isTemplateOfType(template: CliTemplate, templateType: String): Boolean {
        return template.types.contains(templateType)
    }

    override fun toString(): String {
        return displayNameKebab ?: (displayName ?: name)
    }

    override fun equals(other: Any?): Boolean {
        return other is Stack && other.name == name
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }
}