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
import com.stackspot.model.Condition
import com.stackspot.model.Input
import org.apache.commons.lang3.StringUtils
import java.awt.event.ItemListener
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

private const val ENTER_A_NUMBER = "please.enter.a.number"
private const val AT_LEAST_ONE_MUST_BE_SELECTED = "At least one must be selected"
private const val INPUT_INVALID_REGEX = "Input is invalid for regex:"

class PluginInputsPanel(
    private val inputs: List<Input>,
    project: Project? = null,
    val variablesMap: MutableMap<String, Any> = mutableMapOf(),
    private val checkBoxMap: MutableMap<String, Cell<JCheckBox>> = mutableMapOf(),
    private val componentMap: MutableMap<String, Cell<JComponent>> = mutableMapOf(),
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

        val first = checkBoxMap.values.first()
        first.validationOnApply {
            val isSelected = checkBoxMap.values.none { it.component.isSelected }
            if (isSelected) ValidationInfo(AT_LEAST_ONE_MUST_BE_SELECTED) else null
        }

        return dialogPanel
    }


    private fun draw(input: Input, panel: Panel): Row {
        return when (input.type) {

            "bool" -> panel.row {
                val field = checkBox(input.label)
                    .bindSelected(functionBoolean(input)) { variablesMap[input.name] = it }
                    .comment(input.help)
                componentMap[input.name] = field
            }.verifyCondition(input)

            "int" -> panel.row(input.label) {
                val field = textField()
                    .bindText(functionString(input)) { variablesMap[input.name] = it }
                    .comment(input.help)
                validatePattern(field, input)
                field.validationOnInput { validateIfInputIsNumber(it, input) }
                field.columns(COLUMNS_TINY)
                componentMap[input.name] = field
            }.verifyCondition(input)

            "multiselect" -> panel.row(input.label) {
                input.items?.forEach { item ->
                    val key = "${input.name}_$item"
                    checkBoxMap[key] = checkBox(item)
                        .bindSelected(functionBoolean(input, item, key)) { variablesMap[key] = it }
                        .comment(input.help)
                }
                componentMap.putAll(checkBoxMap)
            }.verifyCondition(input)

            else -> panel.row(input.label) {
                val comboBox = input.items?.let { items ->
                    val field = comboBox(items)
                        .bindItem(functionString(input)) { variablesMap[input.name] = it ?: StringUtils.EMPTY }
                        .comment(input.help)
                    componentMap[input.name] = field
                }
                if (comboBox == null) {
                    val field = textField()
                        .bindText((functionString(input))) { variablesMap[input.name] = it }
                        .comment(input.help)
                    validatePattern(field, input)
                    componentMap[input.name] = field
                }
            }.verifyCondition(input)
        }
    }

    private fun Row.verifyCondition(input: Input): Row {
        input.condition?.let {
            val value = when (val component = componentMap[it.variable]?.component) {
                is JBTextField -> {
                    component.document.addDocumentListener(TextFieldListener(component, it, this, input))
                    component.text
                }

                is JCheckBox -> {
                    component.addItemListener(ItemListener { _ ->
                        val result = (componentMap[it.variable]?.component as JCheckBox).isSelected
                        val isVisible = it.evaluate(result, input)
                        this.visible(isVisible)
                    })
                    component.isSelected
                }

                is JComboBox<*> -> {
                    component.addItemListener(ItemListener { _ ->
                        val result = (componentMap[it.variable]?.component as JComboBox<*>).selectedItem
                        val isVisible = it.evaluate(result, input)
                        this.visible(isVisible)
                    })
                    component.selectedItem
                }

                else -> null
            }
            val result = it.evaluate(value, input)
            this.visible(result)
        }
        return this
    }

    private fun ValidationInfoBuilder.validateIfInputIsNumber(
        it: JBTextField, input: Input
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
                !pattern.matches(it) && field.component.isVisible
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

class TextFieldListener(
    private val textField: JBTextField,
    private val condition: Condition,
    private val row: Row,
    private val input: Input
) : DocumentListener {
    override fun insertUpdate(e: DocumentEvent?) {
        val text = textField.text
        val isVisible = condition.evaluate(text, input)
        row.enabled(isVisible)
        row.visible(isVisible)
    }

    override fun removeUpdate(e: DocumentEvent?) {
        val text = textField.text
        val isVisible = condition.evaluate(text, input)
        row.enabled(isVisible)
        row.visible(isVisible)
    }

    override fun changedUpdate(e: DocumentEvent?) {
        val text = textField.text
        val isVisible = condition.evaluate(text, input)
        row.enabled(isVisible)
        row.visible(isVisible)
    }
}
