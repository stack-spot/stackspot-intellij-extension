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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class History(
    @JsonProperty("stack_type") val stackType: String,
    val stack: String?,
    @JsonProperty("applied_templates") val appliedTemplates: List<AppliedTemplate>,
    @JsonProperty("global_inputs") val globalInputs: Map<String, Any>?,
) {

    @JsonIgnore
    fun getTemplateType(): TemplateType {
        return if (stackType == TemplateType.ENV.pluginType) {
            TemplateType.ENV
        } else {
            TemplateType.APP
        }
    }

    @JsonIgnore
    fun getCompatiblePlugins(): Map<Stack, List<Plugin>> {
        val compatiblePlugins = LinkedHashMap<Stack, List<Plugin>>()
        val type = getTemplateType()
        ImportedStacks().list().filter {
            it.filterPluginsByType(type).isNotEmpty()
        }.sortedBy { s ->
            if (s.name == stack) {
                "aa"
            } else {
                s.name
            }
        }.forEach {
            compatiblePlugins[it] = it.filterPluginsByType(type).sortedBy { p -> p.name }.toList()
        }
        return compatiblePlugins
    }
}