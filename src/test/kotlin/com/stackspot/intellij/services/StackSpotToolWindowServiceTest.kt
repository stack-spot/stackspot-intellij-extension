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

import com.stackspot.intellij.commands.stk.CommandInfoList
import com.stackspot.model.AppliedTemplate
import com.stackspot.model.ImportedStacks
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class StackSpotToolWindowServiceTest {

    private val stackInfoList: CommandInfoList = mockk(relaxed = true)
    private val stackfileInfoList: CommandInfoList = mockk(relaxed = true)
    private val templateInfoList: CommandInfoList = mockk(relaxed = true)
    private val pluginInfoList: CommandInfoList = mockk(relaxed = true)

    @BeforeEach
    fun init() {
        stubbing()
        ImportedStacks.getInstance(stackInfoList, stackfileInfoList, templateInfoList, pluginInfoList)
        mockkObject(ImportedStacks)
    }

    private fun stubbing() {
        coEvery { stackInfoList.runAsync().await().stdout } returns "[]"
        coEvery { stackfileInfoList.runAsync().await().stdout } returns "{}"
        coEvery { templateInfoList.runAsync().await().stdout } returns "{}"
        coEvery { pluginInfoList.runAsync().await().stdout } returns "{}"
    }

    @ParameterizedTest
    @MethodSource("pluginOrTemplateArgs")
    fun `when to submit a list, it must return a list with plugins or templates not applied`(
        list: List<String>,
        expected: List<String>
    ) {
        val toolWindowService = StackSpotToolWindowService()
        toolWindowService.appliedPlugins =
            listOf(
                createAppliedTemplate("stack/plugin-1"),
                createAppliedTemplate("stack/plugin-2"),
                createAppliedTemplate("stack/plugin-3"),
            )
        toolWindowService.template = createAppliedTemplate(templateDataPath = "stack/template")

        val pluginsNotApplied = toolWindowService.pluginsOrTemplatesNotApplied(list)

        pluginsNotApplied shouldBe expected
    }

    private fun createAppliedTemplate(templateDataPath: String) = AppliedTemplate(
        templateDataPath = templateDataPath,
        inputs = mapOf()
    )

    private fun pluginOrTemplateArgs(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(listOf("stack/plugin-1", "stack/plugin-4"),  listOf("stack/plugin-4")),
            Arguments.of(listOf("stack/plugin-1", "stack/template-2"),  listOf("stack/template-2")),
            Arguments.of(listOf<String>(),  listOf<String>())
        )
    }
}