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

package com.stackspot.intellij.service

import com.stackspot.intellij.commands.git.GitBranch
import com.stackspot.intellij.commands.git.GitConfig
import com.stackspot.intellij.services.GetDocumentationService
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

    //TODO(Save it to complete later - Needs to add stack file location)
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