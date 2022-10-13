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

package com.stackspot.intellij.commands

import kotlinx.coroutines.Deferred

abstract class BaseCommand(val runner: CommandRunner) : Command {
    abstract fun commandLine(): List<String>
    override fun run(listener: CommandRunner.CommandEndedListener?) = runner.run(commandLine(), listener)
    override fun runSync(): BackgroundCommandRunner = runner.runSync(commandLine())
    override suspend fun runAsync(): Deferred<BackgroundCommandRunner> = runner.runAsync(commandLine())
}