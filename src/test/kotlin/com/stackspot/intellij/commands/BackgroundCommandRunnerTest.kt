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

package com.stackspot.intellij.commands

import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class BackgroundCommandRunnerTest {

    @ParameterizedTest
    @MethodSource("runCommandArgs")
    fun `should run command`(commandLine: List<String>) {
        //Arrange
        mockkStatic(ExecUtil::class)
        every { ExecUtil.execAndGetOutput(any()) } returns ProcessOutput("OK", "", 0, false, false)

        //Act
        val backgroundCommandRunner = BackgroundCommandRunner("")
        backgroundCommandRunner.run(commandLine)

        //Assert
        backgroundCommandRunner.stdout shouldNotBe ""
        backgroundCommandRunner.stderr shouldBe ""
        backgroundCommandRunner.exitCode shouldBe 0
        backgroundCommandRunner.timeout shouldBe false
        backgroundCommandRunner.cancelled shouldBe false

        verify { ExecUtil.execAndGetOutput(any()) }
    }

    @ParameterizedTest
    @MethodSource("runCommandArgs")
    fun `should run command with listener`(commandLine: List<String>) {
        //Arrange
        val listener: CommandRunner.CommandEndedListener = mockk()
        mockkStatic(ExecUtil::class)
        every { ExecUtil.execAndGetOutput(any()) } returns ProcessOutput("OK", "", 0, false, false)
        every { listener.notifyEnded() } just Runs

        //Act
        val backgroundCommandRunner = BackgroundCommandRunner("")
        backgroundCommandRunner.run(commandLine, listener)

        //Assert
        backgroundCommandRunner.stdout shouldNotBe ""
        backgroundCommandRunner.stderr shouldBe ""
        backgroundCommandRunner.exitCode shouldBe 0
        backgroundCommandRunner.timeout shouldBe false
        backgroundCommandRunner.cancelled shouldBe false

        verify { ExecUtil.execAndGetOutput(any()) }
        verify { listener.notifyEnded() }

        confirmVerified(listener)
    }

    @ParameterizedTest
    @MethodSource("runCommandArgs")
    fun `should run command error`(commandLine: List<String>) {
        //Arrange
        mockkStatic(ExecUtil::class)
        every { ExecUtil.execAndGetOutput(any()) } returns ProcessOutput("", "ERROR", 1, false, false)

        //Act
        val backgroundCommandRunner = BackgroundCommandRunner("")
        backgroundCommandRunner.run(commandLine)

        //Assert
        backgroundCommandRunner.stdout shouldBe ""
        backgroundCommandRunner.stderr shouldNotBe ""
        backgroundCommandRunner.exitCode shouldBe 1
        backgroundCommandRunner.timeout shouldBe false
        backgroundCommandRunner.cancelled shouldBe false

        verify { ExecUtil.execAndGetOutput(any()) }
    }

    private fun runCommandArgs(): Stream<Arguments> =
        Stream.of(
            Arguments.of(listOf("git", "config", "--get", "user.name"))
        )

}