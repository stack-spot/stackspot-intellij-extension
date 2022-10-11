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

package com.stackspot.model

import com.stackspot.intellij.commands.stk.CommandInfoList
import com.stackspot.jackson.JacksonExtensions
import com.stackspot.jackson.parseYaml
import com.stackspot.model.UtilModelMock.createCliStack
import com.stackspot.model.UtilModelMock.createFullCliMock
import com.stackspot.model.cli.CliPlugin
import com.stackspot.model.cli.CliStack
import com.stackspot.model.cli.CliStackfile
import com.stackspot.model.cli.CliTemplate
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.stream.Stream


class ImportedStacksTest {

    private val stackInfoList: CommandInfoList = mockk(relaxUnitFun = true)
    private val stackfileInfoList: CommandInfoList = mockk(relaxUnitFun = true)
    private val templateInfoList: CommandInfoList = mockk(relaxUnitFun = true)
    private val pluginInfoList: CommandInfoList = mockk(relaxUnitFun = true)

    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    @ParameterizedTest
    @MethodSource("initializationArgs")
    fun `it must initialize correctly with diversified parameters `(
        stackPathList: List<CliStack>,
        stackfilePathMap: Map<String, List<CliStackfile>>,
        templatePathMap: Map<String, List<CliTemplate>>,
        pluginPathMap: Map<String, List<CliPlugin>>
    ) {
        coEvery { stackInfoList.runAsync().await().stdout } returns stackPathList.parseToStringJson()
        coEvery { stackfileInfoList.runAsync().await().stdout } returns stackfilePathMap.parseToStringJson()
        coEvery { templateInfoList.runAsync().await().stdout } returns templatePathMap.parseToStringJson()
        coEvery { pluginInfoList.runAsync().await().stdout } returns pluginPathMap.parseToStringJson()

        ImportedStacks.getInstance(
            stackInfoList = stackInfoList,
            stackfileInfoList = stackfileInfoList,
            templateInfoList = templateInfoList,
            pluginInfoList = pluginInfoList,
            newInstance = true
        )

        coVerify(exactly = 1) { stackInfoList.runAsync().await() }
        coVerify(exactly = 1) { stackfileInfoList.runAsync().await() }
        coVerify(exactly = 1) { templateInfoList.runAsync().await() }
        coVerify(exactly = 1) { pluginInfoList.runAsync().await() }
    }

    private fun initializationArgs(): Stream<Arguments> {
        val cliMock = createFullCliMock()

        val stackfileEmptyMap = mapOf<String, List<CliStackfile>>()
        val stackEmptyList = listOf<CliStack>()
        val pluginEmptyMap = mapOf<String, List<CliPlugin>>()
        val templateEmptyMap = mapOf<String, List<CliTemplate>>()

        return Stream.of(
            Arguments.of(cliMock.stack, cliMock.stackfile, cliMock.template, cliMock.plugin),
            Arguments.of(cliMock.stack, stackfileEmptyMap, templateEmptyMap, pluginEmptyMap),
            Arguments.of(stackEmptyList, cliMock.stackfile, templateEmptyMap, pluginEmptyMap),
            Arguments.of(stackEmptyList, stackfileEmptyMap, cliMock.template, pluginEmptyMap),
            Arguments.of(stackEmptyList, stackfileEmptyMap, templateEmptyMap, cliMock.plugin)
        )
    }

    @Test
    fun `when there isn't exist stacks, it must convert correctly`() {
        val stdout = """
                    > Initializing stk cli...
                    - stacks folder created!
                    - stk cli successfully initialized!
                    {}
                """

        coEvery { stackInfoList.runAsync().await().stdout } returns stdout
        coEvery { stackfileInfoList.runAsync().await().stdout } returns stdout
        coEvery { templateInfoList.runAsync().await().stdout } returns stdout
        coEvery { pluginInfoList.runAsync().await().stdout } returns stdout

        val instance = ImportedStacks.getInstance(
            stackInfoList = stackInfoList,
            stackfileInfoList = stackfileInfoList,
            templateInfoList = templateInfoList,
            pluginInfoList = pluginInfoList,
            newInstance = true
        )

        instance.shouldNotBeNull()
    }

    @Test
    fun `when to call a stacks search, it must return the stacks`() {
        stubbing()
        mockJackson()

        val instance = ImportedStacks.getInstance(
            stackInfoList = stackInfoList,
            stackfileInfoList = stackfileInfoList,
            templateInfoList = templateInfoList,
            pluginInfoList = pluginInfoList,
            newInstance = true
        )
        val stackList = instance.list()

        val stack = Stack(name = "Stack 1", description = "Stack description")
        val anotherStack = Stack(name = "Stack 2", description = "Stack 2 description")
        stackList.shouldNotBeNull()
        stackList[0] shouldBe stack
        stackList[1] shouldBe anotherStack
    }

    private fun stubbing() {
        val cliMock = createFullCliMock()
        val anotherCliMock = createFullCliMock(
            stackName = "Stack 5",
            stackPath = "stack_5.yaml",
            stackfileName = "Stackfile 5",
            templateName = "Template 5",
            pluginName = "Plugin 5",
        )

        coEvery {
            stackInfoList.runAsync().await().stdout
        } returns cliMock.stack.parseToStringJson() andThen anotherCliMock.stack.parseToStringJson()
        coEvery {
            stackfileInfoList.runAsync().await().stdout
        } returns cliMock.stackfile.parseToStringJson() andThen anotherCliMock.stackfile.parseToStringJson()
        coEvery {
            templateInfoList.runAsync().await().stdout
        } returns cliMock.template.parseToStringJson() andThen anotherCliMock.template.parseToStringJson()
        coEvery {
            pluginInfoList.runAsync().await().stdout
        } returns cliMock.plugin.parseToStringJson() andThen anotherCliMock.plugin.parseToStringJson()
    }

    @ParameterizedTest
    @MethodSource("stackfileArgs")
    fun `when there is stackfile, it must return true and when there is none, return false`(
        stackfile: String,
        expected: Boolean
    ) {
        val cliMock = createFullCliMock()

        coEvery { stackInfoList.runAsync().await().stdout } returns listOf(createCliStack()).parseToStringJson()
        coEvery { stackfileInfoList.runAsync().await().stdout } returns stackfile
        coEvery { templateInfoList.runAsync().await().stdout } returns cliMock.template.parseToStringJson()
        coEvery { pluginInfoList.runAsync().await().stdout } returns cliMock.plugin.parseToStringJson()

        mockkStatic("com.stackspot.jackson.JacksonExtensionsKt")
        val stack = Stack(name = "stack 1", description = "Stack description")
        every { File("stack.yaml").parseYaml(Stack::class.java) } returns stack

        val stackfile = Stackfile(type = "App", description = "Stack description", template = "template/template")
        every { File("stackfile.yaml").parseYaml(Stackfile::class.java) } returns stackfile

        val anotherStackfile =
            Stackfile(type = "App", description = "Stack 2 description", template = "template/template")
        every { File("stackfile_2.yaml").parseYaml(Stackfile::class.java) } returns anotherStackfile

        val instance = ImportedStacks.getInstance(
            stackInfoList = stackInfoList,
            stackfileInfoList = stackfileInfoList,
            templateInfoList = templateInfoList,
            pluginInfoList = pluginInfoList,
            newInstance = true
        )
        val hasStackFiles = instance.hasStackFiles()

        hasStackFiles shouldBe expected
    }

    private fun stackfileArgs(): Stream<Arguments> {
        val createFullCliMock = createFullCliMock()
        val stackfileJson = createFullCliMock.stackfile.parseToStringJson()
        return Stream.of(
            Arguments.of(stackfileJson, true),
            Arguments.of("{}", false)
        )
    }

    @ParameterizedTest
    @MethodSource("argToFetchStackByName")
    fun `when search stack by name, it must return the stack or null, when there is none`(
        stackName: String,
        expected: Stack?
    ) {
        stubbing()
        mockJackson()

        val instance = ImportedStacks.getInstance(
            stackInfoList = stackInfoList,
            stackfileInfoList = stackfileInfoList,
            templateInfoList = templateInfoList,
            pluginInfoList = pluginInfoList,
            newInstance = true
        )
        val stack = instance.getByName(stackName)

        stack shouldBe expected
    }

    private fun argToFetchStackByName(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("Stack 10", null),
            Arguments.of("Stack 2", Stack(name = "Stack 2", description = "Stack 2 description"))
        )
    }

    @Test
    fun `when update the stacks, it must reload the path maps again`() {
        stubbing()
        mockJackson()

        val instance = ImportedStacks.getInstance(
            stackInfoList = stackInfoList,
            stackfileInfoList = stackfileInfoList,
            templateInfoList = templateInfoList,
            pluginInfoList = pluginInfoList,
            newInstance = true
        )

        instance.refresh()

        instance shouldBeSameInstanceAs ImportedStacks.getInstance()

        coVerify(exactly = 2) { stackInfoList.runAsync().await() }
        coVerify(exactly = 2) { stackfileInfoList.runAsync().await() }
        coVerify(exactly = 2) { templateInfoList.runAsync().await() }
        coVerify(exactly = 2) { pluginInfoList.runAsync().await() }

        val anotherCliMock = createFullCliMock(
            stackName = "Stack 5",
            stackPath = "stack_5",
            stackfileName = "Stackfile 5",
            templateName = "Template 5",
            pluginName = "Plugin 5",
        )

        val stack = instance.getByName("Stack 5")
        Assertions.assertEquals(
            stack?.stackfilesMap?.parseToStringJson(), anotherCliMock.stackfile.parseToStringJson()
        )
        Assertions.assertEquals(
            stack?.templatesMap?.parseToStringJson(), anotherCliMock.template.parseToStringJson()
        )
        Assertions.assertEquals(
            stack?.pluginsMap?.parseToStringJson(), anotherCliMock.plugin.parseToStringJson()
        )

    }

    private fun mockJackson() {
        mockkStatic("com.stackspot.jackson.JacksonExtensionsKt")
        mockStack("Stack 1", "stack.yaml")
        mockStack("Stack 2", "stack_2.yaml")
        mockStack("Stack 5", "stack_5.yaml")
    }

    private fun mockStack(name: String, pathName: String) {
        val stackFile = File(pathName)
        val mockStack = Stack(name = name, description = "$name description")
        every { stackFile.parseYaml(Stack::class.java) } returns mockStack
    }

    private fun Any.parseToStringJson(): String {
        return JacksonExtensions.objectMapperJson.writeValueAsString(this)
    }
}





