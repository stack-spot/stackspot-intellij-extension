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

package com.stackspot.intellij.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager
import com.jediterm.terminal.model.TerminalModelListener
import com.stackspot.constants.Constants
import com.stackspot.intellij.commands.BackgroundCommandRunner
import com.stackspot.intellij.commands.CommandRunner
import kotlinx.coroutines.Deferred
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.TerminalTabState
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalView

class StackSpotTerminalCommandMonitoringTask(
    project: Project,
    private val widget: ShellTerminalWidget,
    private val listener: CommandRunner.CommandEndedListener,
    val commandEndedMarker: String,
) : Task.Backgroundable(project, "StackSpot Terminal Command"), TerminalModelListener {
    var commandEnded = false

    override fun modelChanged() {
        val buffer = widget.terminalTextBuffer
        var lineNumber = buffer.height - 1
        while (lineNumber >= 0) {
            val line = buffer.getLine(lineNumber)
            if (line.text == commandEndedMarker) {
                commandEnded = true
                buffer.clearLines(lineNumber, lineNumber)
                break
            }
            lineNumber -= 1
        }
    }

    override fun run(indicator: ProgressIndicator) {
        while (!commandEnded) {
            Thread.sleep(100)
        }
        ApplicationManager.getApplication().invokeLater {
            listener.notifyEnded()
        }
        widget.terminalTextBuffer.removeModelListener(this)
    }

    private fun hasRunningCommand(): Boolean {
        return widget.ttyConnector != null && widget.hasRunningCommands()
    }
}

class StackSpotTerminalRunner(private val project: Project, private val workingDir: String? = null) : CommandRunner {

    companion object {
        const val CMD_SEPARATOR = " "
        var commandSequence = 0
    }

    override fun run(commandLine: List<String>, listener: CommandRunner.CommandEndedListener?) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val window = toolWindowManager.getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
        if (window != null) {
            executeCommandInTerminal(window, listener, commandLine)
        }
    }

    override fun runSync(
        commandLine: List<String>
    ): BackgroundCommandRunner {
        TODO("Not yet implemented")
    }

    override suspend fun runAsync(commandLine: List<String>): Deferred<BackgroundCommandRunner> {
        TODO("Not yet implemented")
    }

    private fun resolveWorkingDir() = workingDir ?: project.basePath

    private fun executeCommandInTerminal(
        window: ToolWindow,
        listener: CommandRunner.CommandEndedListener?,
        commandLine: List<String>
    ) {
        window.show()
        val contentManager = window.contentManager
        val stackSpotTab = contentManager.contents.firstOrNull { Constants.MODULE_TYPE_NAME == it.tabName }
        val workingDir = resolveWorkingDir()
        val terminalView = TerminalView.getInstance(project)
        if (stackSpotTab != null) {
            terminalView.closeTab(stackSpotTab)
        }
        val widget = getTerminalWidgetToRunCommand(terminalView, workingDir, contentManager)
        executeCommand(widget, workingDir, commandLine, listener)
    }

    private fun executeCommand(
        shellWidget: ShellTerminalWidget,
        workDir: String?,
        commandLine: List<String>,
        listener: CommandRunner.CommandEndedListener?
    ) {
        val monitoringTask = createMonitoringTask(shellWidget, listener)
        shellWidget.requestFocus()
        val setStkChannelEnvironmentVariableCommand = getSetStkChannelEnvironmentVariableCommand()
        shellWidget.executeCommand(setStkChannelEnvironmentVariableCommand)
        if (monitoringTask != null) {
            shellWidget.executeCommand(
                "(cd $workDir && " +
                        "${commandLine.joinToString(CMD_SEPARATOR)} && " +
                        "echo ${monitoringTask.commandEndedMarker}) || " +
                        "echo ${monitoringTask.commandEndedMarker}"
            )
            ProgressManager.getInstance().run(monitoringTask)
        } else {
            shellWidget.executeCommand(
                "cd $workDir && " +
                        "${commandLine.joinToString(CMD_SEPARATOR)}"
            )
        }
    }

    private fun getTerminalWidgetToRunCommand(
        terminalView: TerminalView,
        workDir: String?,
        contentManager: ContentManager
    ): ShellTerminalWidget {
        val runner = StackSpotLocalTerminalRunner(project)
        val tabState = TerminalTabState()
        tabState.myTabName = Constants.MODULE_TYPE_NAME
        tabState.myWorkingDirectory = workDir
        terminalView.createNewSession(runner, tabState)
        val content = contentManager.contents.first { Constants.MODULE_TYPE_NAME == it.tabName }
        return TerminalView.getWidgetByContent(content) as ShellTerminalWidget
    }

    private fun createMonitoringTask(
        shellWidget: ShellTerminalWidget,
        listener: CommandRunner.CommandEndedListener?
    ): StackSpotTerminalCommandMonitoringTask? {
        var monitoringTask: StackSpotTerminalCommandMonitoringTask? = null
        if (listener != null) {
            monitoringTask =
                StackSpotTerminalCommandMonitoringTask(project, shellWidget, listener, "stk-$commandSequence-end")
            commandSequence += 1
            shellWidget.terminalTextBuffer.addModelListener(monitoringTask)
        }
        return monitoringTask
    }

    private fun getSetStkChannelEnvironmentVariableCommand(): String {
        if (SystemInfo.isWindows) {
            return "set ${CommandRunner.STK_CHANNEL_ENVIRONMENT_VARIABLE}=${CommandRunner.STK_CHANNLE_INTELLIJ}"
        }
        return "export ${CommandRunner.STK_CHANNEL_ENVIRONMENT_VARIABLE}=${CommandRunner.STK_CHANNLE_INTELLIJ}"
    }
}