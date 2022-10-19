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
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.CHECK_NON_EMPTY
import com.intellij.openapi.ui.validation.validationTextErrorIf
import com.intellij.ui.UIBundle
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ValidationInfoBuilder
import com.stackspot.model.Input
import org.apache.commons.lang3.StringUtils
import javax.swing.JCheckBox
import javax.swing.JComponent

private const val ENTER_A_NUMBER = "please.enter.a.number"
private const val AT_LEAST_ONE_MUST_BE_SELECTED = "At least one must be selected"
private const val INPUT_INVALID_REGEX = "Input is invalid for regex:"

class PluginInputsPanel(
    private val inputs: List<Input>,
    project: Project? = null,
    val variablesMap: MutableMap<String, Any> = mutableMapOf(),
    private var checkboxMap: MutableMap<String, Cell<JCheckBox>> = mutableMapOf(),
) : DialogWrapper(project, true) {

    init {
        title = "Plugin Inputs"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = panel {
            inputs.forEach { input ->
                draw(input, this)
            }
        }

        val first = checkboxMap.values.first()
        first.validationOnApply {
            val isSelected = checkboxMap.values.none { it.component.isSelected }
            if (isSelected) ValidationInfo(AT_LEAST_ONE_MUST_BE_SELECTED) else null
        }

        return dialogPanel
    }


    private fun draw(input: Input, panel: Panel): Row {

        return when (input.type) {
            "bool" -> panel.row {
                checkBox(input.label)
                    .bindSelected(functionBoolean(input)) { variablesMap[input.name] = it }
                    .comment(input.help)
            }

            "int" -> panel.row(input.label) {
                val field = textField()
                    .bindText(functionString(input)) { variablesMap[input.name] = it }
                    .comment(input.help)
                validatePattern(field, input)
                field.validationOnInput { validateIfInputIsNumber(it, input) }
                field.columns(COLUMNS_TINY)
            }

            "multiselect" -> panel.row(input.label) {
                input.items?.forEach { item ->
                    val key = "${input.name}_$item"
                    checkboxMap[key] = checkBox(item)
                        .bindSelected(functionBoolean(input, item, key)) { variablesMap[key] = it }
                        .comment(input.help)
                }
            }

            else -> panel.row(input.label) {
                val multipleComboBox = input.items?.let { items ->
                    comboBox(items)
                        .bindItem(functionString(input)) { variablesMap[input.name] = it ?: StringUtils.EMPTY }
                        .comment(input.help)
                }

                if (multipleComboBox == null) {
                    val field = textField()
                        .bindText((functionString(input))) { variablesMap[input.name] = it }
                        .comment(input.help)
                    validatePattern(field, input)
                }
            }
        }
    }

    private fun ValidationInfoBuilder.validateIfInputIsNumber(it: JBTextField, input: Input
    ): ValidationInfo? {
        val value = it.text
        val regex = "^[0-9]*\$".toRegex()
        return when {
            value == null && input.required -> error(UIBundle.message(ENTER_A_NUMBER))
            value != null && !regex.matches(value) -> error(UIBundle.message(ENTER_A_NUMBER))
            else -> null
        }
    }

    private fun validatePattern(field: Cell<JBTextField>, input: Input) {
        val pattern = input.pattern?.toRegex()
        val checkPattern = pattern?.let {
            validationTextErrorIf("$INPUT_INVALID_REGEX $pattern") {
                !pattern.matches(it)
            }
        }

        checkPattern?.let { field.textValidation(it) }
        if (input.required) field.textValidation(CHECK_NON_EMPTY)
    }

    private fun functionBoolean(input: Input, item: String, key: String): () -> Boolean = {
        val isEnabled = input.containsDefaultValue(item)
        variablesMap[key] = isEnabled
        isEnabled
    }

    private fun functionBoolean(input: Input): () -> Boolean = {
        val defaultValue = input.getDefaultBoolean()
        variablesMap[input.name] = defaultValue
        defaultValue
    }

    private fun functionString(input: Input): () -> String = {
        val defaultValue = input.getDefaultString()
        variablesMap[input.name] = defaultValue
        defaultValue
    }
}