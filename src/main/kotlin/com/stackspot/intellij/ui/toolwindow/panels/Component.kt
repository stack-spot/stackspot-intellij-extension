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
import com.intellij.openapi.ui.validation.validationTextErrorIf
import com.intellij.ui.UIBundle
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.util.MathUtil
import com.stackspot.model.Input
import com.stackspot.model.component.Helper
import org.apache.commons.lang3.StringUtils
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

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

class IntComponent : ComponentType {
    override fun create(helper: Helper): Row {

        return helper.panel.row(helper.input.label) {
            val field = textField()
                .bindText(getterString(helper)) { helper.addValues(it) }
                .comment(helper.input.help)
            validatePattern(field, helper.input)
            validateIfInputIsNumber(field)
            field.columns(10)
            field.component.addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent?) {
                    val text = field.component.text
                    if (text.length >= 7 && text.toIntOrNull() != null) {
                        var value = text.toIntOrNull()
                        value = value?.let { MathUtil.clamp(it, 0, 9999999) }
                        field.component.text = ""
                        field.component.text = value.toString()
                        e?.consume()
                    }
                }
            })
            helper.components.add(field)
        }
    }

    private fun validateIfInputIsNumber(field: Cell<JBTextField>) {

        val regex = "^[0-9]*\$".toRegex()

        val checkPattern = validationTextErrorIf(UIBundle.message(ENTER_A_NUMBER)) {
            !regex.matches(it) && field.component.isVisible
        }

        checkPattern.let { field.textValidation(it) }
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
            validatePattern(field, helper.input)
            helper.components.add(field)
        }
    }
}

private const val INPUT_INVALID_REGEX = "Input is invalid for regex:"

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