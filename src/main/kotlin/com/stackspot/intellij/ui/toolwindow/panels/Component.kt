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
import com.intellij.openapi.ui.validation.CHECK_NON_EMPTY
import com.intellij.ui.UIBundle
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.util.MathUtil
import com.stackspot.model.component.Helper
import org.apache.commons.lang3.StringUtils
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JPasswordField
import javax.swing.JTextField

interface ComponentType {
    fun create(helper: Helper): Row
}

private const val IT_MUST_BE_SELECTED = "It must be selected"

class BoolComponent : ComponentType {
    override fun create(helper: Helper): Row {
        return helper.panel.row {
            val field = checkBox(helper.input.label)
                .bindSelected(getterBoolean(helper)) { helper.addValues(it) }
                .comment(helper.input.help)
                .validationOnApply {
                    if (helper.input.required && !it.isSelected) ValidationInfo(IT_MUST_BE_SELECTED) else null
                }
            helper.components.add(field)
        }
    }
}

private const val ENTER_A_NUMBER = "please.enter.a.number"

private const val COLUMNS_SIZE = 10
private const val MAX_TEXT_LENGTH = 7

class IntComponent : ComponentType {
    override fun create(helper: Helper): Row {

        return helper.panel.row(helper.input.label) {
            val field = textField()
                .bindText(getterString(helper)) { helper.addValues(it) }
                .comment(helper.input.help)
            validateRequired(helper, field)
            validatePattern(helper, field)
            validateIfInputIsNumber(field)
            field.columns(COLUMNS_SIZE)
            validateNumericFieldSize(field)
            helper.components.add(field)
        }
    }

    private fun validateNumericFieldSize(field: Cell<JBTextField>) {
        field.component.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                val text = field.component.text
                if (text.length >= MAX_TEXT_LENGTH && text.toIntOrNull() != null) {
                    var value = text.toIntOrNull()
                    value = value?.let { MathUtil.clamp(it, 0, 9999999) }
                    field.component.text = ""
                    field.component.text = value.toString()
                    e?.consume()
                }
            }
        })
    }

    private fun validateIfInputIsNumber(field: Cell<JBTextField>) {
        val regex = "^[0-9]*\$".toRegex()
        field.validation {
            val text: String = field.component.text
            if (!regex.matches(text) && field.component.isVisible) {
                ValidationInfo(UIBundle.message(ENTER_A_NUMBER), field.component)
            } else null
        }
    }
}

class MultiselectComponent : ComponentType {
    override fun create(helper: Helper): Row {
        return helper.panel.row(helper.input.label) {
            helper.input.items?.forEach { item ->
                val values = helper.variableValues
                val checkBox = checkBox(item)
                    .bindSelected(getterBoolean(helper, item)) { if (it) values.add(item) else values.remove(item) }
                    .comment(helper.input.help)
                helper.checkBoxList.add(checkBox)
            }
        }
    }
}

class TextComponent : ComponentType {
    override fun create(helper: Helper): Row {
        return helper.panel.row(helper.input.label) {
            val field = textField()
                .bindText(getterString(helper)) { helper.addValues(it) }
                .comment(helper.input.help)
            validatePattern(helper, field)
            helper.components.add(field)
        }
    }
}

class ListComponent : ComponentType {
    override fun create(helper: Helper): Row {
        return helper.panel.row(helper.input.label) {
            helper.input.items?.let { items ->
                val field = comboBox(items)
                    .bindItem(getterString(helper)) { helper.addValues(it ?: StringUtils.EMPTY) }
                    .comment(helper.input.help)
                helper.components.add(field)
            }
        }

    }
}

class PasswordComponent : ComponentType {
    override fun create(helper: Helper): Row {
        val passwordField = JPasswordField(COLUMNS_SHORT)
        return helper.panel.row(helper.input.label) {
            val field = cell(passwordField)
                .bindText(getterString(helper)) { helper.addValues(it) }
                .comment(helper.input.help)
            validatePattern(helper, field)
            helper.components.add(field)
        }
    }
}

private const val INPUT_INVALID_REGEX = "Input is invalid for regex:"

private fun validateRequired(helper: Helper, field: Cell<JBTextField>) {
    if (helper.input.required) field.textValidation(CHECK_NON_EMPTY)
}

private fun validatePattern(helper: Helper, field: Cell<JTextField>) {
    val pattern = helper.input.pattern?.toRegex()
    pattern?.let {
        field.validation {
            val text: String = field.component.text
            if (!pattern.matches(text) && field.component.isVisible) {
                ValidationInfo("$INPUT_INVALID_REGEX $pattern", field.component)
            } else null
        }
    }
}

private fun getterBoolean(
    helper: Helper,
    item: String
): () -> Boolean = {
    val isEnabled = helper.input.containsDefaultValue(item)
    if (isEnabled) {
        helper.variableValues.add(item)
    }
    isEnabled
}

private fun getterBoolean(helper: Helper): () -> Boolean = {
    val defaultValue = helper.input.getDefaultBoolean()
    helper.variableValues.add(defaultValue)
    defaultValue
}

private fun getterString(helper: Helper): () -> String = {
    val defaultValue = helper.input.getDefaultString()
    helper.variableValues.add(defaultValue)
    defaultValue
}