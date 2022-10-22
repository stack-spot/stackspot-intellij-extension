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

data class Condition(val variable: String, val operator: String, var value: Any) {

    companion object {
        val OPERATIONS = mapOf(
            "==" to ::eq,
            "!=" to ::neq,
            ">" to ::gt,
            "<" to ::lt,
            ">=" to ::gte,
            "<=" to ::lte,
            "containsAny" to ::containsAny,
            "containsAll" to ::containsAll,
            "containsOnly" to ::containsOnly
        )

        private fun eq(variableValue: Any, value: Any, input: Input): Boolean =
            variableValue as String == (value as String)

        private fun neq(variableValue: Any, value: Any, input: Input): Boolean =
            variableValue as String != (value as String)

        private fun gt(variableValue: Any, value: Any, input: Input): Boolean =
            (variableValue as String).toInt() > (value as String).toInt()

        private fun lt(variableValue: Any, value: Any, input: Input): Boolean =
            (variableValue as String).toInt() < (value as String).toInt()

        private fun gte(variableValue: Any, value: Any, input: Input): Boolean =
            (variableValue as String).toInt() >= (value as String).toInt()

        private fun lte(variableValue: Any, value: Any, input: Input): Boolean =
            (variableValue as String).toInt() <= (value as String).toInt()

        private fun containsAny(variableValue: Any, value: Any, input: Input): Boolean {
            variableValue as Set<*>
            return when (value) {
                is String -> variableValue.contains(value)
                else -> (value as ArrayList<*>).any { s -> variableValue.contains(s) }
            }
        }

        private fun containsAll(variableValue: Any, value: Any, input: Input): Boolean {
            variableValue as Set<*>
            return when (value) {
                is String -> variableValue.contains(value)
                else -> variableValue.containsAll(value as ArrayList<*>)
            }
        }

        private fun containsOnly(variableValue: Any, value: Any, input: Input): Boolean {
            variableValue as Set<*>
            return when (value) {
                is String -> {
                    val valueList = mutableSetOf<String>()
                    valueList.add(value)
                    variableValue == valueList
                }

                else -> variableValue == value
            }
        }
    }

    fun evaluate(variableValue: Any?, input: Input): Boolean {
        if (variableValue == null || variableValue == "") return false
        val operation = OPERATIONS[operator]
        return if (operation != null) {
            operation(variableValue, this.value, input)
        } else {
            false
        }
    }
}