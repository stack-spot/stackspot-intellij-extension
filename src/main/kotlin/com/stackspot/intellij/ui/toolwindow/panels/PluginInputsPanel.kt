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
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import com.stackspot.model.Plugin
import com.stackspot.model.component.Helper
import com.stackspot.model.component.MultiselectHelper
import javax.swing.JComponent

class PluginInputsPanel(
    project: Project? = null,
    private val plugin: Plugin,
    windowTitle: String,
    val variablesMap: MutableMap<String, Any> = mutableMapOf(),
    private val helpers: MutableList<Helper> = mutableListOf()
) : DialogWrapper(project, true) {

    init {
        title = windowTitle
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = panel {
            plugin.inputs?.forEach { input ->
                val helper = Helper()
                helper.input = input
                helper.panel = this
                draw(helper)
                helpers.add(helper)
            }
        }
        return dialogPanel
    }


    private fun draw(helper: Helper): Row {

        verifyDependency(helper)

        val strategyMap = mapOf(
            "bool" to BoolComponent(),
            "int" to IntComponent(),
            "text" to TextComponent(),
            "multiselect" to MultiselectComponent(),
            "list" to ListComponent(),
            "password" to PasswordComponent()
        )

        val strategy = strategyMap[helper.input.typeValue]

        if (strategy != null) {
            val validation = ValidationHandler()
            return strategy.create(helper).also { validation.conditionValidation(helper, it) }
        }

        Messages.showWarningDialog("Change <b>${helper.input.typeValue}</b> to a valid type", "Invalid input type")

        return helper.panel.row {}
    }

    private fun verifyDependency(helper: Helper) {
        val dependsOn = helper.input.condition?.let { c -> helpers.first { h -> c.variable == h.input.name } }
        val component = dependsOn?.components?.firstOrNull()
        helper.dependsOn.add(component)

        if (dependsOn?.input?.type == "multiselect") {
            helper.dependsOnMultiselect = MultiselectHelper(
                dependsOn.variableValues,
                dependsOn.checkBoxList,
                dependsOn.components,
                dependsOn.input
            )
        }
    }

    override fun showAndGet(): Boolean {
        check(isModal) { "The showAndGet() method is for modal dialogs only" }
        show()
        helpers.filter { helper -> helper.isActive }.forEach { h -> variablesMap[h.input.name] = h.variableValues }
        return isOK
    }
}
