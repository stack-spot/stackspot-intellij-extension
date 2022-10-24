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

package com.stackspot.model.component

import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.stackspot.model.Input
import javax.swing.JCheckBox
import javax.swing.JComponent

data class Helper(
    val variablesMap: MutableMap<String, Any> = mutableMapOf(),
    val checkBoxList: MutableSet<Cell<JCheckBox>> = mutableSetOf(),
    val componentMap: MutableMap<String, Cell<JComponent>> = mutableMapOf(),
    val checkBoxValuesMap: MutableMap<String, MutableSet<String>> =  mutableMapOf()
) {
    lateinit var input: Input
    lateinit var panel: Panel
}


