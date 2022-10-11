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

package com.stackspot.intellij.jackson

import com.stackspot.jackson.*
import com.stackspot.model.*
import com.stackspot.model.UtilModelMock.createFullCliMock
import com.stackspot.model.UtilModelMock.createPlugin
import com.stackspot.model.UtilModelMock.createStackfile
import com.stackspot.model.UtilModelMock.createTemplate
import com.stackspot.model.cli.CliPlugin
import com.stackspot.model.cli.CliStack
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import java.io.File

class JacksonExtensionsTest {

    @Test
    fun `it must convert from yaml file to plugin object`() {
        mockkStatic("com.stackspot.jackson.JacksonExtensionsKt")
        every { File("plugin.yaml").parseYaml(Plugin::class.java) } returns createPlugin()

        val stack = createStack()
        val pluginYaml = "plugin.yaml".parsePluginYaml(stack)

        val expected = createPlugin()
        expected.stack = stack
        pluginYaml.parseToStringJson() shouldBe expected.parseToStringJson()
    }

    @Test
    fun `it must convert from yaml file to template object`() {
        mockkStatic("com.stackspot.jackson.JacksonExtensionsKt")
        every { File("template.yaml").parseYaml(Template::class.java) } returns createTemplate()

        val stack = createStack()
        val template = "template.yaml".parseTemplateYaml(stack)

        val expected = createTemplate()
        expected.stack = stack
        template.parseToStringJson() shouldBe expected.parseToStringJson()
    }

    @Test
    fun `it must convert from yaml file to stackfile object`() {
        mockkStatic("com.stackspot.jackson.JacksonExtensionsKt")
        every { File("stackfile.yaml").parseYaml(Stackfile::class.java) } returns createStackfile()

        val stackfile = "stackfile.yaml".parseStackfile()

        val expected = createStackfile()
        expected.name = "stackfile"
        stackfile.parseToStringJson() shouldBe expected.parseToStringJson()
    }

    @Test
    fun `it must convert from the json string to the given object list`() {
        val cliMock = createFullCliMock()
        val parseToStringJson = cliMock.stack.parseToStringJson()

        val parseJsonToList = parseToStringJson.parseJsonToList<CliStack>()

        parseJsonToList.parseToStringJson() shouldBe parseToStringJson
    }

    @Test
    fun `it must convert from the json string to a map with a list`() {
        val cliMock = createFullCliMock()
        val stringJson = cliMock.plugin.parseToStringJson()

        val parseJsonToList = stringJson.parseJsonToMapWithList<CliPlugin>()

        parseJsonToList.parseToStringJson() shouldBe stringJson
    }

    @Test
    fun `it must add plugin, template and stackfile maps inside the stack`() {
        mockkStatic("com.stackspot.jackson.JacksonExtensionsKt")
        every { File("stack.yaml").parseYaml(Stack::class.java) } returns createStack()

        val plugin = createFullCliMock().plugin
        val template = createFullCliMock().template
        val stackfile = createFullCliMock().stackfile


        val stack = "stack.yaml".parseStackYaml(plugin, template, stackfile)

        val expected = createStack()
        expected.pluginsMap = plugin
        expected.templatesMap = template
        expected.stackfilesMap = stackfile
        expected.location = File("stack.yaml")
        stack.parseToStringJson() shouldBe expected.parseToStringJson()
    }

    private fun createStack(): Stack {
        val stack = Stack(name = "Stack name", description = "Stack description")
        stack.location = File("")
        stack.pluginsMap = mutableMapOf()
        stack.stackfilesMap = mutableMapOf()
        stack.templatesMap = mutableMapOf()
        return stack
    }

    private fun Any.parseToStringJson(): String {
        return JacksonExtensions.objectMapperJson.writeValueAsString(this)
    }
}