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

import com.stackspot.jackson.JacksonExtensions
import com.stackspot.jackson.parsePluginYaml
import com.stackspot.jackson.parseStackfile
import com.stackspot.jackson.parseTemplateYaml
import com.stackspot.model.UtilModelMock.createCliPlugin
import com.stackspot.model.UtilModelMock.createCliStackfile
import com.stackspot.model.UtilModelMock.createCliTemplate
import com.stackspot.model.UtilModelMock.createPlugin
import com.stackspot.model.UtilModelMock.createStackfile
import com.stackspot.model.UtilModelMock.createTemplate
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class StackTest {

    @BeforeEach
    fun init() {
        clearAllMocks()
        mockkStatic("com.stackspot.jackson.JacksonExtensionsKt")
    }

    @Test
    fun `when searching plugin with type, should return a list of plugins`() {
        val stack = Stack(name = "stack 1", description = "Stack 1 description")
        val pluginsPathMap = mapOf("stack 1" to listOf(createCliPlugin(), createCliPlugin()))
        stack.pluginsMap = pluginsPathMap

        val plugin = createPlugin()
        every { "plugin.yaml".parsePluginYaml(stack) } returns plugin andThen plugin

        val filterPluginsByType = stack.listPlugins()

        filterPluginsByType.parseToStringJson() shouldBe listOf(plugin, plugin).parseToStringJson()
    }

    @ParameterizedTest
    @MethodSource("templateArgs")
    fun `when searching template by name, should return a template`(name: String, expected: String?) {
        val stack = Stack(name = "stack 1", description = "Stack 1 description")
        val templatesPathMap =
            mapOf("stack 1" to listOf(createCliTemplate(), createCliTemplate(name = "Template 2 name")))
        stack.templatesMap = templatesPathMap

        val template = createTemplate(name = "Template name")
        every { "template.yaml".parseTemplateYaml(stack) } returns template

        val result = stack.getTemplateByName(name)

        result?.parseToStringJson() shouldBe expected
    }

    private fun templateArgs(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("Template", null),
            Arguments.of("Template name", createTemplate(name = "Template name").parseToStringJson())
        )
    }

    @ParameterizedTest
    @MethodSource("pluginArgs")
    fun `when searching plugin by name, should return a plugin`(name: String, expected: String?) {
        val stack = Stack(name = "stack 1", description = "Stack 1 description")
        val pluginsPathMap = mapOf("stack 1" to listOf(createCliPlugin(), createCliPlugin(name = "PLugin 2 name")))
        stack.pluginsMap = pluginsPathMap

        val plugin = createPlugin(name = "Plugin name")
        every { "plugin.yaml".parsePluginYaml(stack) } returns plugin

        val result = stack.getPluginByName(name)

        result?.parseToStringJson() shouldBe expected
    }

    private fun pluginArgs(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("Plugin", null),
            Arguments.of("Plugin name", createPlugin(name = "Plugin name").parseToStringJson())
        )
    }

    @Test
    fun `when searching the stack files, it must return a list fo stackfiles`() {
        val stack = Stack(name = "stack 1", description = "Stack 1 description")
        val stackfilesPathMap = getStackfilesPathMap()
        stack.stackfilesMap = stackfilesPathMap

        val stackfile = createStackfile()
        every { "stackfile.yaml".parseStackfile() } returns stackfile

        val result = stack.listStackfiles()

        result.parseToStringJson() shouldBe listOf(stackfile, stackfile).parseToStringJson()
    }

    private fun getStackfilesPathMap() =
        mapOf("stack 1" to listOf(createCliStackfile(), createCliStackfile(name = "Stackfile 2 name")))

    private fun Any.parseToStringJson(): String {
        return JacksonExtensions.objectMapperJson.writeValueAsString(this)
    }
}