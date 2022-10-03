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

package com.stackspot.services

import com.jayway.jsonpath.JsonPath
import com.stackspot.intellij.services.CreateProjectService
import com.stackspot.model.Stack
import com.stackspot.model.Stackfile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class CreateProjectServiceTest {

    @ParameterizedTest
    @MethodSource("saveInfoArgs")
    fun `should saveInfo when args are nullable and when they don't`(stack: Stack?, stackfile: Stackfile?) {
        val service = CreateProjectService().saveInfo(stack, stackfile)
        Assertions.assertEquals(stack, service.stack)
        Assertions.assertEquals(stackfile, service.stackfile)
    }

    @ParameterizedTest
    @MethodSource("stackfileIsSelectedArgs")
    fun `should check if stackfile is selected and when it don't`(
        stack: Stack?,
        stackfile: Stackfile?,
        expected: Boolean
    ) {
        val service = CreateProjectService().saveInfo(stack, stackfile)
        Assertions.assertEquals(service.isStackfileSelected(), expected)
    }

    @Test
    fun `test`() {
        val json = """
        {
            "store": {
            "book": [
            {
                "category": "reference",
                "author": "Nigel Rees",
                "title": "Sayings of the Century",
                "price": 8.95
            },
            {
                "category": "fiction",
                "author": "Evelyn Waugh",
                "title": "Sword of Honour",
                "price": 12.99
            },
            {
                "category": "fiction",
                "author": "Herman Melville",
                "title": "Moby Dick",
                "isbn": "0-553-21311-3",
                "price": 8.99
            },
            {
                "category": "fiction",
                "author": "J. R. R. Tolkien",
                "title": "The Lord of the Rings",
                "isbn": "0-395-19395-8",
                "price": 22.99
            }
            ],
            "bicycle": {
            "color": "red",
            "price": 19.95
        }
        },
            "expensive": 10
        }
        """

        val book = JsonPath.read<Map<String, Any>>(json, "$.store.book[0]")
        println(book)
    }

    private companion object {
        @JvmStatic
        fun stackfileIsSelectedArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    null, null, false
                ),
                Arguments.of(
                    createStack(),
                    createStackfile(),
                    true
                )
            )

        @JvmStatic
        fun saveInfoArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    null, null
                ),
                Arguments.of(
                    createStack(),
                    createStackfile()
                )
            )

        private fun createStack(name: String = "stack-for-test", description: String = "stack test description") =
            Stack(name, description)

        private fun createStackfile(
            type: String = "app",
            description: String = "stackfile test description",
            template: String = "test-tempalte"
        ) = Stackfile(type, description, template)
    }
}