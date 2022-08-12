/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil.execAndGetOutput
import java.nio.charset.Charset

class BackgroundCommandRunner(private val workingDir: String) : CommandRunner {

    var stdout: String = ""
        get() {
            return field.replace("\\n".toRegex(), "")
        }
    lateinit var stderr: String

    override fun run(commandLine: List<String>, listener: CommandRunner.CommandEndedListener?) {
        val generalCommandLine = GeneralCommandLine(commandLine)
            .withCharset(Charset.forName("UTF-8"))
            .withWorkDirectory(workingDir)
            .withEnvironment(CommandRunner.STK_CHANNEL_ENVIRONMENT_VARIABLE, CommandRunner.STK_CHANNLE_INTELLIJ)
        val processOutput = execAndGetOutput(generalCommandLine)
        stdout = processOutput.stdout
        stderr = processOutput.stderr
        listener?.notifyEnded()
    }
}