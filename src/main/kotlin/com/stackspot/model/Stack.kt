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
import com.stackspot.yaml.parsePluginYaml
import com.stackspot.yaml.parseStackfile
import com.stackspot.yaml.parseTemplateYaml
import java.io.File
import java.util.*

data class Stack(
    val name: String,
    val description: String,
    val displayName: String?,
    @JsonProperty("display-name") val displayNameKebab: String?,
    val useCases: List<StackUseCase>?,
    @JsonProperty("use-cases") val useCasesKebab: List<StackUseCase>?
) {
    lateinit var location: File

    fun filterTemplatesByType(type: TemplateType): List<Template> {
        return location.walk().filter {
            it.isDirectory && isTemplateOfType(it, type.templateType)
        }.mapNotNull {
            it.parseTemplateYaml(this)
        }.toList()
    }

    fun filterPluginsByType(type: TemplateType): List<Plugin> {
        return location.walk().filter {
            it.isDirectory && isTemplateOfType(it, type.pluginType)
        }.mapNotNull {
            it.parsePluginYaml(this)
        }.toList()
    }

    fun getTemplateByName(name: String): Template? {
        return location.walk().filter {
            it.isDirectory && name.equals(it.name, true)
        }.mapNotNull {
            it.parseTemplateYaml(this)
        }.firstOrNull()
    }

    fun getPluginByName(name: String): Plugin? {
        return location.walk().filter {
            it.isDirectory && name.equals(it.name, true)
        }.mapNotNull {
            it.parsePluginYaml(this)
        }.firstOrNull()
    }

    fun listCompatiblePluginsByStackType(stackType: String): List<Plugin> {
        val templateType = if (stackType == "env") {
            TemplateType.ENV
        } else {
            TemplateType.APP
        }
        return filterPluginsByType(templateType)
    }

    fun listStackfiles(): List<Stackfile> {
        return location.toPath().resolve("stackfiles").toFile()
            .walk()
            .filter { it.name.endsWith(".yaml") }
            .map { it.parseStackfile() }
            .toList()
    }

    fun listPlugins(): List<Plugin> {
        return location.walk().filter {
            it.isDirectory
        }.filter {
            val template = it.parseTemplateYaml(this)
            template != null && (template.types.contains(TemplateType.APP.pluginType) || template.types.contains(
                TemplateType.APP.pluginType
            ))
        }.mapNotNull {
            it.parsePluginYaml(this)
        }.toList()
    }

    private fun isTemplateOfType(dir: File, templateType: String): Boolean {
        val template = dir.parseTemplateYaml(this) ?: return false
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