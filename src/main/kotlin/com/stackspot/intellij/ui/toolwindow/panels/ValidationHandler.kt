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

package com.stackspot.intellij.ui.toolwindow.panels

import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Row
import com.stackspot.model.Condition
import com.stackspot.model.component.Helper
import java.awt.event.ItemListener
import javax.swing.JComboBox
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ValidationHandler {

    private var handler: ComponentValidationHandler = DependencyValidation()

    init {
        handler
            .linkHandler(CheckBoxGroupValidation())
            .linkHandler(CheckBoxValidation())
            .linkHandler(ComboBoxValidation())
            .linkHandler(TextValidation())
            .linkHandler(MultiselectValidation())
    }

    fun conditionValidation(helper: Helper, row: Row): Row {
        return handler.check(helper, row) ?: row
    }
}

class CheckBoxValidation : ComponentValidationHandler() {

    override fun check(helper: Helper, row: Row): Row? {
        val component = helper.dependsOn.first()?.component
        val condition = helper.input.condition

        if (component is JBCheckBox && condition != null && helper.dependsOnMultiselect == null) {
            component.addItemListener(ItemListener {
                val isSelected = component.isSelected
                val isActive = condition.evaluate(isSelected)
                checkVisibility(helper, isActive, row)
            })
            val isActive = condition.evaluate(component.isSelected)
            checkVisibility(helper, isActive, row)
            return row
        }

        return checkNext(helper, row)
    }
}

private const val AT_LEAST_ONE_MUST_BE_SELECTED = "At least one must be selected"

class CheckBoxGroupValidation : ComponentValidationHandler() {

    override fun check(helper: Helper, row: Row): Row? {
        val checkBoxList = helper.checkBoxList

        if (checkBoxList.isNotEmpty()) {
            checkBoxList.first().validationOnApply {
                val isNotSelected = helper.checkBoxList.none { it.component.isSelected }
                if (isNotSelected) ValidationInfo(AT_LEAST_ONE_MUST_BE_SELECTED) else null
            }
        }

        return checkNext(helper, row)
    }
}

class DependencyValidation : ComponentValidationHandler() {

    override fun check(helper: Helper, row: Row): Row? {
        val component = (helper.dependsOn.firstOrNull()
            ?: helper.dependsOnMultiselect?.checkBoxList?.firstOrNull())?.component
        val condition = helper.input.condition

        if (component != null && condition != null) {
            val fields = helper.checkBoxList.ifEmpty { helper.components }.first()
            fields.validationOnApply {
                if (!component.isVisible && it.isVisible) {
                    ValidationInfo("This component has depends on the field ${condition.variable}")
                } else null
            }
        }

        return checkNext(helper, row)
    }
}

class ComboBoxValidation : ComponentValidationHandler() {

    override fun check(helper: Helper, row: Row): Row? {
        val component = helper.dependsOn.first()?.component
        val condition = helper.input.condition
        if (component is JComboBox<*> && condition != null) {
            component.addItemListener(ItemListener {
                val item = component.selectedItem
                val isActive = condition.evaluate(item)
                checkVisibility(helper, isActive, row)
            })
            val isActive = condition.evaluate(component.selectedItem)
            checkVisibility(helper, isActive, row)
            return row
        }

        return checkNext(helper, row)
    }
}

class TextValidation : ComponentValidationHandler() {

    override fun check(helper: Helper, row: Row): Row? {
        val component = helper.dependsOn.first()?.component
        val condition = helper.input.condition
        if (component is JTextField && condition != null) {
            component.document.addDocumentListener(TextFieldListener(component, condition, row, helper))
            val isActive = condition.evaluate(component.text)
            checkVisibility(helper, isActive, row)
            return row
        }
        return checkNext(helper, row)
    }
}

class MultiselectValidation : ComponentValidationHandler() {

    override fun check(helper: Helper, row: Row): Row? {
        val dependsOn = helper.dependsOnMultiselect
        val condition = helper.input.condition
        val enabled: MutableSet<String> = mutableSetOf()

        if (dependsOn != null && dependsOn.input.type == "multiselect" && condition != null) {
            dependsOn.checkBoxList.forEach { checkBox ->
                val component = checkBox.component
                component.addItemListener { cb ->
                    val valueList = dependsOn.variableValues
                    val text = (cb.item as JBCheckBox).text
                    if (valueList.contains(text)) valueList.remove(text) else valueList.add(text)
                    val isActive = condition.evaluate(valueList)
                    checkVisibility(helper, isActive, row)
                    dependsOn.variableValues.addAll(valueList)
                }
                if (component.isSelected) enabled.add(component.text)
            }

            val isActive = condition.evaluate(enabled)
            checkVisibility(helper, isActive, row)
            return row
        }

        return checkNext(helper, row)
    }
}

private fun checkVisibility(helper: Helper, isActive: Boolean, row: Row) {
    helper.isActive = isActive
    row.enabled(isActive)
    row.visible(isActive)
}


class TextFieldListener(
    private val textField: JTextField,
    private val condition: Condition,
    private val row: Row,
    private val helper: Helper
) : DocumentListener {
    override fun insertUpdate(e: DocumentEvent?) {
        val text = textField.text
        val isVisible = condition.evaluate(text)
        helper.isActive = isVisible
        row.enabled(isVisible)
        row.visible(isVisible)
    }

    override fun removeUpdate(e: DocumentEvent?) {
        insertUpdate(e)
    }

    override fun changedUpdate(e: DocumentEvent?) {
        insertUpdate(e)
    }
}