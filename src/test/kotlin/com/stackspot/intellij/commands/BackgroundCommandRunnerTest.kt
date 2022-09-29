package com.stackspot.intellij.commands

import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class BackgroundCommandRunnerTest {

    @ParameterizedTest
    @MethodSource("args")
    fun bla(commandLine: List<String>, listener: CommandRunner.CommandEndedListener?) {
        //Arrange
        mockkStatic(ExecUtil::class)
        every { ExecUtil.execAndGetOutput(any()) } returns ProcessOutput("OK", "", 0, false, false)

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
    }

    private fun args(): Stream<Arguments> =
        Stream.of(
            Arguments.of(listOf("git", "config", "--get", "user.name"), null)
        )

}