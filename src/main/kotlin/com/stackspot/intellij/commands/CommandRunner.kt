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

import com.stackspot.constants.Constants
import kotlinx.coroutines.Deferred

interface CommandRunner {

    companion object {
        const val STK_CHANNEL_ENVIRONMENT_VARIABLE = "STK_CHANNEL"
        const val STK_CHANNEL_INTELLIJ = "intellij"
    }

    interface CommandEndedListener {
        fun notifyEnded()
    }

    fun run(
        commandLine: List<String>,
        listener: CommandEndedListener? = null,
        workingDir: String? = Constants.Paths.STK_HOME.toString()
    )

    fun runSync(
        commandLine: List<String>,
        workingDir: String? = Constants.Paths.STK_HOME.toString()
    ): BackgroundCommandRunner

    suspend fun runAsync(
        commandLine: List<String>,
        workingDir: String? = Constants.Paths.STK_HOME.toString()
    ): Deferred<BackgroundCommandRunner>
}
