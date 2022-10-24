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
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.UIBundle
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ValidationInfoBuilder
import com.stackspot.model.Condition
import com.stackspot.model.Input
import com.stackspot.model.component.Helper
import java.awt.event.ItemListener
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

private const val ENTER_A_NUMBER = "please.enter.a.number"
private const val AT_LEAST_ONE_MUST_BE_SELECTED = "At least one must be selected"

class CopyPluginInputsPanel(
    private val inputs: List<Input>,
    project: Project? = null,
    var variablesMap: MutableMap<String, Any> = mutableMapOf(),
    private val helper: Helper = Helper(),
) : DialogWrapper(project, true) {

    init {
//        TODO alterar nome da tela para o nome do plugin
        title = "Plugin Inputs"
        init()
    }

    override fun createCenterPanel(): JComponent {

        val dialogPanel = panel {
            inputs.forEach { input ->
                helper.input = input
                helper.panel = this
                draw(helper)
            }
        }

        val first = helper.checkBoxList.first()
        first.validationOnApply {
            val isSelected = helper.checkBoxList.none { it.component.isSelected }
            if (isSelected) ValidationInfo(AT_LEAST_ONE_MUST_BE_SELECTED) else null
        }
        this.variablesMap = helper.variablesMap

        return dialogPanel
    }


    private fun draw(helper: Helper): Row {

        val strategyMap = mapOf(
            "bool" to ::BoolComponent,
            "int" to ::IntComponent,
            "text" to ::TextComponent,
            "multiselect" to ::MultiselectComponent,
            "list" to ::ListComponent
        )

        val strategy = strategyMap[helper.input.typeValue]

        if (strategy == null) {
            Messages.showErrorDialog(
                "Type invalid",
                helper.input.typeValue
            )
        }

        return strategy?.invoke()?.create(helper)?.verifyCondition(helper.input.condition)!!
    }

    private fun Row.verifyCondition(condition: Condition?): Row {

        condition?.let { c ->
            val input = inputs.first { input -> input.name == c.variable }
            if (input.type == "multiselect") {
                input.items?.forEach { item ->
                    val key = "${c.variable}_${item}"
                    val component = helper.componentMap[key]?.component as JCheckBox
                    component.addItemListener { cb ->
                        val valueList = helper.variablesMap[input.name] as MutableSet<String>
                        val text = (cb.item as JBCheckBox).text
                        if (valueList.contains(text)) valueList.remove(text) else valueList.add(text)
                        val isActive = c.evaluate(valueList, input)
                        this.visible(isActive)
                        this.enabled(isActive)
                        helper.variablesMap[c.variable] = valueList
                    }
                }

                val filter = input.items?.filter { item ->
                    val component = helper.componentMap["${c.variable}_${item}"]?.component as JCheckBox
                    component.isSelected
                }?.toSet()

                val result = c.evaluate(filter, input)
                this.visible(result)
                return this
            }

            val value = when (val component = helper.componentMap[c.variable]?.component) {
                is JBTextField -> {
                    component.document.addDocumentListener(TextFieldListener(component, c, this, input))
                    component.text
                }

                is JCheckBox -> {
                    component.addItemListener(ItemListener {
                        val result = (helper.componentMap[c.variable]?.component as JCheckBox).isSelected
                        val isVisible = c.evaluate(result, input)
                        this.visible(isVisible)
                        this.enabled(isVisible)
                    })
                    component.isSelected
                }

                is JComboBox<*> -> {
                    component.addItemListener(ItemListener {
                        val result = (helper.componentMap[c.variable]?.component as JComboBox<*>).selectedItem
                        val isVisible = c.evaluate(result, input)
                        this.visible(isVisible)
                        this.enabled(isVisible)
                    })
                    component.selectedItem
                }

                else -> null
            }
            val result = c.evaluate(value, input)
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
