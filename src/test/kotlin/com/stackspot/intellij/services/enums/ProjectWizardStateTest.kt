package com.stackspot.intellij.services.enums

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class ProjectWizardStateTest {

    @ParameterizedTest
    @MethodSource("rightMessageArs")
    fun `should enum have the right message`(enum: ProjectWizardState, expected: String?) {
        enum.message shouldBe expected
    }

    private fun rightMessageArs(): Stream<Arguments> =
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