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
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

object ImportedStacks {

    init {
        val measureTimeMillis = measureTimeMillis {
//            loadMapsAndLists()
            load()
        }
        println("TOTAL $measureTimeMillis")
        //            println("Took: ${Duration.between(start, Instant.now()).toMillis()}ms to execute")
    }

    lateinit var pluginsMap: Map<String, List<CliPlugin>>
    lateinit var templatesMap: Map<String, List<CliTemplate>>
    lateinit var stackfilesMap: Map<String, List<CliStackfile>>
    lateinit var stacksList: List<CliStack>


    private fun loadMapsAndLists() {
        runBlocking {
            launch {
                stacksList = getCommandInfoList(Command.STACK).parseJsonToList<CliStack>()
            }

            launch {
                stackfilesMap = getCommandInfoList(Command.STACKFILE).parseJsonToMapWithList<CliStackfile>()
            }

            launch {
                templatesMap = getCommandInfoList(Command.TEMPLATE).parseJsonToMapWithList<CliTemplate>()
            }

            launch {
                pluginsMap = getCommandInfoList(Command.PLUGIN).parseJsonToMapWithList<CliPlugin>()
            }
        }
    }

//    private val pluginsMap = getCommandInfoList(Command.PLUGIN).parseJsonToMapWithList<CliPlugin>()
//    private val templatesMap = getCommandInfoList(Command.TEMPLATE).parseJsonToMapWithList<CliTemplate>()
//    private val stackfilesMap = getCommandInfoList(Command.STACKFILE).parseJsonToMapWithList<CliStackfile>()
//    private val stacksList = getCommandInfoList(Command.STACK).parseJsonToList<CliStack>()


    private fun load() {
        pluginsMap = getCommandInfoList(Command.PLUGIN).parseJsonToMapWithList<CliPlugin>()
        templatesMap = getCommandInfoList(Command.TEMPLATE).parseJsonToMapWithList<CliTemplate>()
        stackfilesMap = getCommandInfoList(Command.STACKFILE).parseJsonToMapWithList<CliStackfile>()
        stacksList = getCommandInfoList(Command.STACK).parseJsonToList<CliStack>()
    }

    fun hasStackFiles() = list().any { it.listStackfiles(filterByStack = false).isNotEmpty() }

    fun list(): List<Stack> {
        println("STACK")
        return stacksList
            .map {
                it.path.parseStackYaml(pluginsMap, templatesMap, stackfilesMap)
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

    private fun getCommandInfoList(command: Command): String {
        return CommandInfoList(command.value).runSync().stdout
    }

//    private suspend fun getCommandInfoList(command: Command): String {
//        return CommandInfoList(command.value).runAsync().await().stdout
//    }

    fun reload() {
        loadMapsAndLists()
    }
}
