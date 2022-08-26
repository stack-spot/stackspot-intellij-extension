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

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

open class Template(
    open val name: String,
    open val description: String,
    open val types: List<String>,
    open val inputs: List<Input>? = null,
    open val displayName: String? = null,
    @JsonProperty("display-name") open val displayNameKebab: String? = null
) {

    var stack: Stack? = null

    override fun toString(): String {
        return displayNameKebab ?: (displayName ?: name)
    }

    fun hasInputs(): Boolean {
        val i = inputs
        if (i != null) {
            return i.isNotEmpty()
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is Template) {
            return other.stack?.name == stack?.name && other.name == name
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(stack?.name, name)
    }
}