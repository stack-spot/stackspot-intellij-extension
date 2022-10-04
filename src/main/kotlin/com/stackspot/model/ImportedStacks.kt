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

import com.stackspot.intellij.commands.BackgroundCommandRunner
import com.stackspot.intellij.commands.stk.CommandInfoList
import com.stackspot.intellij.commons.singleThread
import com.stackspot.intellij.services.enums.Command
import com.stackspot.jackson.parseJsonToGetPaths
import com.stackspot.jackson.parseJsonToList
import com.stackspot.jackson.parseJsonToMapWithList
import com.stackspot.jackson.parseStackYaml
import com.stackspot.model.cli.CliPlugin
import com.stackspot.model.cli.CliStack
import com.stackspot.model.cli.CliStackfile
import com.stackspot.model.cli.CliTemplate
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ImportedStacks {

    init {
            loadMapsAndLists()
    }

    lateinit var pluginsPathMap: Map<String, List<CliPlugin>>
    lateinit var templatesPathMap: Map<String, List<CliTemplate>>
    lateinit var stackfilesPathMap: Map<String, List<CliStackfile>>
    lateinit var stacksPathList: List<CliStack>


    private fun loadMapsAndLists() {
        runBlocking {
            launch {
                stacksPathList = getCommandInfoList(Command.STACK).parseJsonToList()
            }

            launch {
                stackfilesPathMap = getCommandInfoList(Command.STACKFILE).parseJsonToMapWithList()
            }

            launch {
                templatesPathMap = getCommandInfoList(Command.TEMPLATE).parseJsonToMapWithList()
            }

            launch {
                pluginsPathMap = getCommandInfoList(Command.PLUGIN).parseJsonToMapWithList()
            }
        }
    }

    fun hasStackFiles() = list().any { it.listStackfiles(filterByStack = false).isNotEmpty() }

    fun list(): List<Stack> {
        println("STACK")
        return stacksPathList
            .map {
                it.path.parseStackYaml(pluginsPathMap, templatesPathMap, stackfilesPathMap)
            }.sortedBy {
                it.name.lowercase()
            }
    }

    private fun pathsList(): List<String> {
        return singleThread {
            val stackList = CommandInfoList(Command.STACK.value)
            stackList.run()
            (stackList.runner as BackgroundCommandRunner).stdout
        }.parseJsonToGetPaths()
    }

    fun getByName(name: String): Stack? {
        return list().firstOrNull { it.name == name }
    }

    private suspend fun getCommandInfoList(command: Command): String {
        return CommandInfoList(command.value).runAsync().await().stdout
    }

    fun reload() {
        loadMapsAndLists()
    }
}
