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
import com.intellij.ui.layout.ValidationInfoBuilder
import com.stackspot.model.Input
import com.stackspot.model.component.Helper
import org.apache.commons.lang3.StringUtils

interface ComponentType {
    fun create(helper: Helper): Row
}

class BoolComponent : ComponentType {
    override fun create(helper: Helper): Row {
        return helper.panel.row {
            val field = checkBox(helper.input.label)
                .bindSelected(
                    getterBoolean(
                        helper.input,
                        helper.variablesMap
                    )
                ) { helper.variablesMap[helper.input.name] = it }
                .comment(helper.input.help)
            helper.componentMap[helper.input.name] = field
        }
    }
}

private const val ENTER_A_NUMBER = "please.enter.a.number"

class IntComponent : ComponentType {
    override fun create(helper: Helper): Row {
        return helper.panel.row(helper.input.label) {
            val field = textField()
                .bindText(getterString(helper.input, helper.variablesMap)) {
                    helper.variablesMap[helper.input.name] = it
                }
                .comment(helper.input.help)
            validatePattern(field, helper.input)
            field.validationOnInput { validateIfInputIsNumber(it, helper.input) }
            field.columns(COLUMNS_TINY)
            helper.componentMap[helper.input.name] = field
        }
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

class MultiselectComponent : ComponentType {
    override fun create(helper: Helper): Row {
        helper.variablesMap[helper.input.name] = mutableSetOf<String>()
        val valueList = helper.variablesMap[helper.input.name] as MutableSet<String>

        return helper.panel.row(helper.input.label) {
            helper.input.items?.forEach { item ->
                val key = "${helper.input.name}_$item"
                val checkBox = checkBox(item)
                    .bindSelected(
                        getterBoolean(helper.input, item, valueList)
                    ) { if (it) valueList.add(item) else valueList.remove(item) }
                    .comment(helper.input.help)
                helper.checkBoxList.add(checkBox)
                helper.componentMap[key] = checkBox
            }
        }
    }
}

class TextComponent : ComponentType {
    override fun create(helper: Helper): Row {
        return helper.panel.row(helper.input.label) {
            val field = textField()
                .bindText(
                    (getterString(
                        helper.input,
                        helper.variablesMap
                    ))
                ) { helper.variablesMap[helper.input.name] = it }
                .comment(helper.input.help)
            validatePattern(field, helper.input)
            helper.componentMap[helper.input.name] = field
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
                    .bindItem(getterString(helper.input, helper.variablesMap)) {
                        helper.variablesMap[helper.input.name] = it ?: StringUtils.EMPTY
                    }
                    .comment(helper.input.help)
                helper.componentMap[helper.input.name] = field
            }
        }
    }
}

private fun getterBoolean(
    input: Input,
    item: String,
    multiselectVariable: MutableSet<String>? = mutableSetOf()
): () -> Boolean = {
    val isEnabled = input.containsDefaultValue(item)
    if (isEnabled) {
        multiselectVariable?.add(item)
    }
    isEnabled
}

private fun getterBoolean(input: Input, variablesMap: MutableMap<String, Any>): () -> Boolean = {
    val defaultValue = input.getDefaultBoolean()
    variablesMap[input.name] = defaultValue
    defaultValue
}

private fun getterString(input: Input, variablesMap: MutableMap<String, Any>): () -> String = {
    val defaultValue = input.getDefaultString()
    variablesMap[input.name] = defaultValue
    defaultValue
}
