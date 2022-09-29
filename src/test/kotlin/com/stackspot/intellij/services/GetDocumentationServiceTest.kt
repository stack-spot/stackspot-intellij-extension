package com.stackspot.intellij.services

import com.stackspot.intellij.commands.git.GitBranch
import com.stackspot.intellij.commands.git.GitConfig
import com.stackspot.model.Stack
import com.stackspot.yaml.YamlResourceUtils
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import java.io.File
import kotlin.io.path.Path

internal class GetDocumentationServiceTest {

    private val gitConfigCmd: GitConfig = mockk()
    private val gitBranchCmd: GitBranch = mockk()

//    @Nested
//    inner class SuccessCases {
//
//        @Test
//        fun `should `() {
//            every { gitConfigCmd.run() } just runs
//            every { gitBranchCmd.run() } just runs
//            val stack = YamlResourceUtils.readYaml("yaml/stacks/default-stack/stack.yaml", Stack::class.java)
//            stack?.location = File(javaClass.getResource("yaml/stacks/default-stack/stack.yaml")?.file ?: "")
//
//            val service = GetDocumentationService(gitConfigCmd, gitBranchCmd)
//            val docUrl = service.getDocumentationUrl(stack)
//
//            docUrl shouldBe ""
//            verify {
//                gitConfigCmd.run()
//                gitBranchCmd.run()
//            }
//        }
//
//    }

    @Nested
    inner class FailureCases {

        @ParameterizedTest
        @NullSource
        fun `should return empty string when stack is null`(stack: Stack?) {
            val service = GetDocumentationService()
            val docUrl = service.getDocumentationUrl(stack)
            docUrl shouldBe ""
        }

    }

}