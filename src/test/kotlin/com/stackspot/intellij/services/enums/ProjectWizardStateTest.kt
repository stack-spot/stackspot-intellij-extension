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

internal class ProjectWizardStateTest {

    @ParameterizedTest
    @MethodSource("rightMessageArgs")
    fun `should enum have the right message`(enum: ProjectWizardState, expected: String?) {
        enum.message shouldBe expected
    }

    private fun rightMessageArgs(): Stream<Arguments> =
        Stream.of(
            Arguments.of(ProjectWizardState.NOT_INSTALLED, "Please install STK CLI before continue."),
            Arguments.of(ProjectWizardState.STACKFILES_EMPTY, "Please import a stack with stackfiles before continue."),
            Arguments.of(
                ProjectWizardState.GIT_CONFIG_NOT_OK,
                "Please insert git config username and e-mail before continue."
            ),
            Arguments.of(ProjectWizardState.OK, null)
        )

}