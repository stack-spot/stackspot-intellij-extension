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
import javax.swing.JComponent

class PluginInputsPanel(private val inputs: List<Input>, project: Project? = null) : DialogWrapper(project, true) {

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
                    .bindSelected({input.default as Boolean}, {})
            }
            "int" -> panel.row(input.label) {
                intTextField()
                    .text(input.default.toString())
                    .comment(input.help)

            }
            "multiselect" -> panel.row(input.label) {
                input.items?.forEach { checkBox(it) }
            }
            else -> panel.row(input.label) {
                input.items?.let { comboBox(it).comment(input.help) }
                    ?: textField()
                        .text(input.default.toString())
                        .comment(input.help)
            }
        }
    }
}