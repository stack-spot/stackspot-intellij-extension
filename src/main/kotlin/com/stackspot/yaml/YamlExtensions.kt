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

package com.stackspot.yaml

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.stackspot.constants.Constants
import com.stackspot.model.*
import java.io.File

object YamlExtensions {

    val objectMapper: ObjectMapper by lazy {
        val kotlinModule = KotlinModule.Builder()
            .configure(KotlinFeature.NullToEmptyMap, true)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()

        ObjectMapper(YAMLFactory()).registerModule(kotlinModule)
            .also { it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }
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
        YamlExtensions.objectMapper.readValue(it, clazz)
    }
}

fun File.parsePluginYaml(stack: Stack): Plugin? {
    var pluginYaml: File? = null
    for (fileName in listOf(Constants.Files.PLUGIN_YAML, Constants.Files.PLUGIN_YML)) {
        pluginYaml = this.resolve(fileName)
        if (pluginYaml != null) {
            break
        }
    }
    if (pluginYaml == null) {
        return null
    }
    val plugin = pluginYaml.parseYaml(Plugin::class.java)
    plugin.stack = stack
    return plugin
}

fun File.parseTemplateYaml(stack: Stack): Template? {
    var templateYaml: File? = null
    for (fileName in listOf(
        Constants.Files.PLUGIN_YAML,
        Constants.Files.PLUGIN_YML,
        Constants.Files.TEMPLATE_YAML,
        Constants.Files.TEMPLATE_YML
    )) {
        templateYaml = this.resolve(fileName)
        if (templateYaml != null) {
            break
        }
    }
    if (templateYaml == null) {
        return null
    }
    val template = templateYaml.parseYaml(Template::class.java)
    template.stack = stack
    return template
}

fun File.parseStackYaml(): Stack? {
    var stackYaml: File? = null
    for (fileName in listOf(Constants.Files.STACK_YML, Constants.Files.STACK_YAML)) {
        stackYaml = this.resolve(fileName)
        if (stackYaml != null) {
            break
        }
    }
    if (stackYaml == null) {
        return null
    }
    val stack = stackYaml.parseYaml(Stack::class.java)
    stack.location = this
    return stack
}

fun File.parseHistory(): History? {
    val stkYaml = this.resolve(Constants.Files.STK_YAML)
    return stkYaml?.parseYaml(History::class.java)
}

fun File.parseStackfile(): Stackfile {
    val sf = this.parseYaml(Stackfile::class.java)
    sf.name = this.name.split(".").first()
    return sf
}