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

package com.stackspot.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AppliedTemplate(
    @JsonProperty("template_data_path") val templateDataPath: String,
    val inputs: Map<String, Any>?,
) {
    fun toString(importedStacks: ImportedStacks): String {
        val stackTemplate = templateDataPath.split("/")
        val stackName = stackTemplate[0]
        val componentName = stackTemplate[1]
        val stack = importedStacks.getByName(stackName)
        if (stack != null) {
            return buildPrettyName(stack, componentName)
        }
        return templateDataPath
    }

    private fun buildPrettyName(stack: Stack, componentName: String): String {
        val template = stack.getTemplateByName(componentName)
        if (template != null) {
            return "$stack/$template"
        }
        val plugin = stack.getPluginByName(componentName)
        if (plugin != null) {
            return "$stack/$plugin"
        }
        return "$stack/${componentName}"
    }
}
