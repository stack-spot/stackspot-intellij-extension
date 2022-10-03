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

package com.stackspot.intellij.services

import com.stackspot.intellij.commands.BackgroundCommandRunner
import com.stackspot.intellij.commands.git.GitConfig
import com.stackspot.intellij.services.enums.ProjectWizardState
import com.stackspot.model.ImportedStacks
import com.stackspot.model.Stack
import com.stackspot.model.Stackfile
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.awaitility.kotlin.await
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

internal class CreateProjectServiceTest {

    private val importedStacks: ImportedStacks = mockk()
    private val gitConfigCmd: GitConfig = mockk(relaxUnitFun = true)

    @BeforeEach
    fun init() {
        clearAllMocks()
    }

    @Nested
    inner class FailureCases {
        @Test
        fun `service state should be STACKFILES_EMPTY`() {
            every { importedStacks.hasStackFiles() } returns false
            val service = CreateProjectService(importedStacks, isInstalled = true)
            service.state shouldBe ProjectWizardState.STACKFILES_EMPTY
            verify { importedStacks.hasStackFiles() }
            confirmVerified(importedStacks)
        }

        @Test
        fun `service state should be NOT_INSTALLED`() {
            val service = CreateProjectService(isInstalled = false)
            service.state shouldBe ProjectWizardState.NOT_INSTALLED
        }

        @Test
        fun `service state should be GIT_CONFIG_NOT_OK`() {
            every { importedStacks.hasStackFiles() } returns true
            every { (gitConfigCmd.runner as BackgroundCommandRunner).stdout } returns ""
            val service = CreateProjectService(importedStacks, gitConfigCmd = gitConfigCmd, isInstalled = true)
            service.state shouldBe ProjectWizardState.GIT_CONFIG_NOT_OK
            verify { importedStacks.hasStackFiles() }
            verify { gitConfigCmd.run() }
            confirmVerified(importedStacks)
        }

        @ParameterizedTest
        @MethodSource("stackfileIsSelectNullArgs")
        fun `should check if stackfile isn't selected`(
            stack: Stack?,
            stackfile: Stackfile?,
            expected: Boolean
        ) {
            val service = CreateProjectService().saveInfo(stack, stackfile)
            Assertions.assertEquals(service.isStackfileSelected(), expected)
        }

        private fun stackfileIsSelectNullArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(null, null, false),
                Arguments.of(createStack(), null, false),
                Arguments.of(null, createStackfile(), false)
            )
    }

    @Nested
    @Disabled
    inner class SuccessCases {
        @Test
        fun `should clear service attributes`() {
            val service = CreateProjectService().saveInfo(createStack(), createStackfile())
            service.clearInfo()

            service.asClue {
                it.stack shouldBe null
                it.stackfile shouldBe null
            }
        }

        @Test
        fun `service state should be OK`() {
            every { importedStacks.hasStackFiles() } returns true
            val service = CreateProjectService(importedStacks, isInstalled = true)
            service.state shouldBe ProjectWizardState.OK
            verify { importedStacks.hasStackFiles() }
            confirmVerified(importedStacks)
        }

        @Test
        fun `should add git config`() {
            every { gitConfigCmd.run() } just runs
            val service = CreateProjectService(gitConfigCmd = gitConfigCmd, isInstalled = true)
            service.addGitConfig("aaa", "b@b.com")
            await.atMost(500, TimeUnit.MILLISECONDS)
                .untilAsserted {
                    verify(exactly = 2) { gitConfigCmd.run() }
                }
        }

        @ParameterizedTest
        @MethodSource("saveInfoArgs")
        fun `should saveInfo when args aren't null`(stack: Stack?, stackfile: Stackfile?) {
            val service = CreateProjectService().saveInfo(stack, stackfile)
            service.asClue {
                it.stack shouldBe stack
                it.stackfile shouldBe stackfile
            }
        }

        @ParameterizedTest
        @MethodSource("saveInfoNullArgs")
        fun `should saveInfo when args are null`(stack: Stack?, stackfile: Stackfile?) {
            val service = CreateProjectService().saveInfo(stack, stackfile)
            service.asClue {
                it.stack shouldBe stack
                it.stackfile shouldBe stackfile
            }
        }

        @ParameterizedTest
        @MethodSource("stackfileIsSelectedArgs")
        fun `should check if stackfile is selected`(
            stack: Stack?,
            stackfile: Stackfile?,
            expected: Boolean
        ) {
            val service = CreateProjectService().saveInfo(stack, stackfile)
            Assertions.assertEquals(service.isStackfileSelected(), expected)
        }

        private fun stackfileIsSelectedArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(createStack(), createStackfile(), true)
            )

        private fun saveInfoArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    null, null
                )
            )

        private fun saveInfoNullArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    null, null
                )
            )
    }

    private fun createStack(name: String = "stack-for-test", description: String = "stack test description") =
        Stack(name, description)

    private fun createStackfile(
        type: String = "app",
        description: String = "stackfile test description",
        template: String = "test-template"
    ) = Stackfile(type, description, template)
}