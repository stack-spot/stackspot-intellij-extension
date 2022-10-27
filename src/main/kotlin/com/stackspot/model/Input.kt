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

package com.stackspot.model

import com.intellij.util.containers.isNullOrEmpty
import org.apache.commons.lang3.StringUtils

data class Input(
    val type: String,
    val label: String,
    val name: String,
    val default: Any?,
    val condition: Condition?,
    val required: Boolean = false,
    val items: Set<String>? = null,
    val pattern: String? = null,
    val help: String? = null
) {

    val typeValue: String
        get() {
            return if (type == "text" && !items.isNullOrEmpty()) "list" else type
        }

    fun containsDefaultValue(value: String): Boolean {
        if (default == null) return false
        default as List<*>
        return default.contains(value)
    }

    fun getDefaultBoolean(): Boolean = default as? Boolean ?: false

    fun getDefaultString(): String = default?.toString() ?: StringUtils.EMPTY

}