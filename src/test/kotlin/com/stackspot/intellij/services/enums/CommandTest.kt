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

package com.stackspot.intellij.services.enums

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class CommandTest {


    @ParameterizedTest
    @MethodSource("messageArgs")
    fun `it must return the command correctly`(enum: Command, expected: String) {
        enum.value shouldBe expected
    }

    private fun messageArgs(): Stream<Arguments> =
        Stream.of(
            Arguments.of(Command.STACK, "stack"),
            Arguments.of(Command.STACKFILE, "stackfile"),
            Arguments.of(Command.TEMPLATE, "template"),
            Arguments.of(Command.PLUGIN, "plugin"),
        )
}