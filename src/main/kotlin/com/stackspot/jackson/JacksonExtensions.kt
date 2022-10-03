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

package com.stackspot.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jayway.jsonpath.JsonPath
import com.stackspot.constants.Constants
import com.stackspot.model.*
import com.stackspot.model.cli.CliPlugin
import com.stackspot.model.cli.CliStackfile
import com.stackspot.model.cli.CliTemplate
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.time.Duration
import java.time.Instant

object JacksonExtensions {

    private val kotlinModule = KotlinModule.Builder()
        .configure(KotlinFeature.NullToEmptyMap, true)
        .configure(KotlinFeature.NullIsSameAsDefault, false)
        .configure(KotlinFeature.SingletonSupport, false)
        .configure(KotlinFeature.StrictNullChecks, false)
        .build()

    val objectMapperJson: ObjectMapper by lazy { register(ObjectMapper())  }

    val objectMapperYaml: ObjectMapper by lazy { register(ObjectMapper(YAMLFactory())) }

    private fun register(objectMapper: ObjectMapper) =
        objectMapper
            .registerModule(kotlinModule)
            .also { it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}


private fun File.resolve(name: String): File? {
    val path = this.toPath()
    val pathToResolve = path.resolve(name)
    val file = pathToResolve.toFile()
    return if (file.exists()) {
        file
    } else {
        null
    }
}

fun <T> File.parseYaml(clazz: Class<T>): T {
    return this.bufferedReader().use {
        JacksonExtensions.objectMapperYaml.readValue(it, clazz)
    }
}

fun String.parseJsonToGetPaths(): List<String> {
    return JsonPath.parse(this).read("$..path")
}

inline fun <reified T> String.parseJsonToMapWithList(): HashMap<String, List<T>> {
    println("Order: $this")
    val typeRef: TypeReference<HashMap<String, List<T>>> = object : TypeReference<HashMap<String, List<T>>>() {}
    return JacksonExtensions.objectMapperJson.readValue(this, typeRef)
}

inline fun <reified T> String.parseJsonToList(): List<T> {
    println("Order Stack: $this")
    val typeRef: TypeReference<List<T>> = object : TypeReference<List<T>>() {}
    return JacksonExtensions.objectMapperJson.readValue(this, typeRef)
}

fun String.parsePluginYaml(stack: Stack): Plugin {
    val pluginYaml = File(this)
    val plugin = pluginYaml.parseYaml(Plugin::class.java)
    plugin.stack = stack
    return plugin
}

fun String.parseTemplateYaml(stack: Stack): Template {
    val templateYaml = File(this)
    val template = templateYaml.parseYaml(Template::class.java)
    template.stack = stack
    return template
}

private const val FILE_NAME_REGEX = "([A-Za-z0-9-_]+(.yaml|.yml))\$"
private const val REPLACE_FILE_REGEX = "\\/$FILE_NAME_REGEX"

fun String.parseStackYaml(
    pluginsMap: Map<String, List<CliPlugin>>,
    templatesMap: Map<String, List<CliTemplate>>,
    stackfilesMap: Map<String, List<CliStackfile>>
): Stack {
    val stackYaml = File(this)
    val stack = stackYaml.parseYaml(Stack::class.java)

    val pathName = this.replace(REPLACE_FILE_REGEX.toRegex(), StringUtils.EMPTY)
    stack.location = File(pathName)
    stack.pluginsMap = pluginsMap
    stack.templatesMap = templatesMap
    stack.stackfilesMap = stackfilesMap
    return stack
}

fun File.parseHistory(): History? {
    val stkYaml = this.resolve(Constants.Files.STK_YAML)
    return stkYaml?.parseYaml(History::class.java)
}

fun String.parseStackfile(): Stackfile {

    val stackYaml = File(this)
    val stackfile = stackYaml.parseYaml(Stackfile::class.java)

    val regex = FILE_NAME_REGEX.toRegex()
    val fileName = regex.find(this)?.value

    if (fileName?.isNotBlank() == true) {
        stackfile.name = fileName.split(".").first()
    }

    return stackfile
}