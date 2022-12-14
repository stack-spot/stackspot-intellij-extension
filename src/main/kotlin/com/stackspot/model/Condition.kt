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


data class Condition(val variable: String, val operator: String, var value: String) {

    companion object {
        val OPERATIONS = mapOf(
            "==" to ::eq,
            "!=" to ::neq
        )

        private fun eq(variableValue: String, value: String, input: Input): Boolean =
            input.convert(variableValue) == input.convert(value)

        private fun neq(variableValue: String, value: String, input: Input): Boolean =
            input.convert(variableValue) != input.convert(value)
    }

    fun evaluate(variables: Map<String, String>, input: Input): Boolean {
        val variableValue = variables[variable]
        val operation = OPERATIONS[operator]
        return if (operation != null) {
            operation(variableValue ?: "", value, input)
        } else {
            false
        }
    }

}