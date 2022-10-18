package com.stackspot.intellij.ui.toolwindow.panels

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

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import com.stackspot.model.Input
import org.apache.commons.lang3.StringUtils
import javax.swing.JComponent

class PluginInputsPanel(
    private val inputs: List<Input>,
    project: Project? = null,
    private var variablesMap: MutableMap<String, Any> = mutableMapOf()
) : DialogWrapper(project, true) {

    init {
        title = "Plugin Inputs"
        init()
    }


    override fun createCenterPanel(): JComponent {
        return panel {
            inputs.forEach { input ->
                draw(input, this)
            }
        }
    }

    private fun draw(input: Input, panel: Panel): Row {
        return when (input.type) {
            "bool" -> panel.row {
                checkBox(input.label)
                    .bindSelected({
                        val defaultValue = input.getDefaultBoolean()
                        variablesMap[input.name] = defaultValue
                        defaultValue
                    }, { variablesMap[input.name] = it })
            }

            "int" -> panel.row(input.label) {
                intTextField()
                    .bindText(
                        {
                            val defaultValue = input.getDefaultString()
                            variablesMap[input.name] = defaultValue
                            defaultValue
                        },
                        { variablesMap[input.name] = it })
                    .comment(input.help)

            }

            "multiselect" -> panel.row(input.label) {
                input.items?.forEach { item ->
                    val isEnabled = input.containsDefaultValue(item)
                    val key = "${input.name}_$item"
                    checkBox(item)
                        .bindSelected(
                            {
                                variablesMap[key] = isEnabled
                                isEnabled
                            },
                            { variablesMap[key] = it })
                }
            }

            else -> panel.row(input.label) {
                val defaultValue = input.getDefaultString()
                input.items?.let { items ->
                    comboBox(items)
                        .bindItem(
                            {
                                variablesMap[input.name] = defaultValue
                                defaultValue
                            },
                            { variablesMap[input.name] = it ?: StringUtils.EMPTY }
                        ).comment(input.help)
                } ?: textField()
                    .bindText(
                        {
                            variablesMap[input.name] = defaultValue
                            defaultValue
                        },
                        { variablesMap[input.name] = it }
                    ).comment(input.help)
            }
        }
    }
}