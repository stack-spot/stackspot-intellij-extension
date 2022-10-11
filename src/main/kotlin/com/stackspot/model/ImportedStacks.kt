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
import com.stackspot.intellij.services.enums.Command
import com.stackspot.jackson.parseJsonToList
import com.stackspot.jackson.parseJsonToMapWithList
import com.stackspot.jackson.parseStackYaml
import com.stackspot.model.cli.CliPlugin
import com.stackspot.model.cli.CliStack
import com.stackspot.model.cli.CliStackfile
import com.stackspot.model.cli.CliTemplate
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

class ImportedStacks private constructor(
    private val stackInfoList: CommandInfoList,
    private val stackfileInfoList: CommandInfoList,
    private val templateInfoList: CommandInfoList,
    private val pluginInfoList: CommandInfoList
) {

    companion object : SingletonHolder(::ImportedStacks)

    init {
        refreshCommandInfo()
    }

    private lateinit var pluginsPathMap: Map<String, List<CliPlugin>>
    private lateinit var templatesPathMap: Map<String, List<CliTemplate>>
    private lateinit var stackfilesPathMap: Map<String, List<CliStackfile>>
    private lateinit var stacksPathList: List<CliStack>

    private fun refreshCommandInfo() {
        runBlocking {
            launch {
                stacksPathList = getCommandInfoList(stackInfoList, Command.STACK).parseJsonToList()
            }

            launch {
                stackfilesPathMap =
                    getCommandInfoList(stackfileInfoList, Command.STACKFILE).parseJsonToMapWithList()
            }

            launch {
                templatesPathMap = getCommandInfoList(templateInfoList, Command.TEMPLATE).parseJsonToMapWithList()
            }

            launch {
                pluginsPathMap = getCommandInfoList(pluginInfoList, Command.PLUGIN).parseJsonToMapWithList()
            }
        }
    }

    fun hasStackFiles() = list().any { it.listStackfiles().isNotEmpty() }

    fun list(): List<Stack> {
        return stacksPathList
            .map {
                it.path.parseStackYaml(pluginsPathMap, templatesPathMap, stackfilesPathMap)
            }.sortedBy {
                it.name.lowercase()
            }
    }

    fun getByName(name: String): Stack? {
        return list().firstOrNull { it.name == name }
    }

    private suspend fun getCommandInfoList(commandInfoList: CommandInfoList, command: Command): String {
        commandInfoList.command = command.value
        return commandInfoList.runAsync().await().stdout
    }

    fun refresh() {
        refreshCommandInfo()
    }
}

open class SingletonHolder(
    var constructor: (CommandInfoList, CommandInfoList, CommandInfoList, CommandInfoList) -> ImportedStacks
) {

    @Volatile
    private lateinit var instance: ImportedStacks
    fun getInstance(
        stackInfoList: CommandInfoList = CommandInfoList(),
        stackfileInfoList: CommandInfoList = CommandInfoList(),
        templateInfoList: CommandInfoList = CommandInfoList(),
        pluginInfoList: CommandInfoList = CommandInfoList(),
        newInstance: Boolean = false
    ): ImportedStacks {
        synchronized(this) {
            if (!::instance.isInitialized || newInstance) {
                instance = constructor(stackInfoList, stackfileInfoList, templateInfoList, pluginInfoList)
            }
            return instance
        }
    }
}