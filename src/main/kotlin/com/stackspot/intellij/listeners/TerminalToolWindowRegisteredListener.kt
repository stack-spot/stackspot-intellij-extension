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

package com.stackspot.intellij.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.stackspot.constants.Constants
import com.stackspot.intellij.commands.listeners.CompositeCommandEndedListener
import com.stackspot.intellij.commands.listeners.LinkGradleProjectsToProjectListener
import com.stackspot.intellij.commands.listeners.LinkMavenProjectsToProjectListener
import com.stackspot.intellij.commands.listeners.NotifyStackYamlUpdatedCommandListener
import com.stackspot.intellij.commands.stk.CreateProjectByStackfile
import com.stackspot.intellij.services.CreateProjectService
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import kotlin.io.path.Path

class TerminalToolWindowRegisteredListener : ToolWindowManagerListener {

    override fun toolWindowsRegistered(ids: MutableList<String>, toolWindowManager: ToolWindowManager) {
        ids.firstOrNull { TerminalToolWindowFactory.TOOL_WINDOW_ID == it }?.let {
            initStackSpotProject()
        }
    }

    private fun initStackSpotProject() {
        val service = service<CreateProjectService>()

        if (service.isStackfileSelected()) {
            ProjectManager.getInstance().openProjects.forEach { project ->
                val projectBasePath = project.basePath ?: return
                val isInitialized = Path(projectBasePath, Constants.Files.STK_YAML).toFile().exists()
                val isStackSpotProject = ModuleManager.getInstance(project).modules
                    .firstOrNull { Constants.MODULE_TYPE == ModuleType.get(it).id } != null

                if (isStackSpotProject && !isInitialized) {
                    execCommand(project, service)
                    service.clearInfo()
                }
            }
        }
    }

    private fun execCommand(project: Project, service: CreateProjectService) {
        val stack = service.stack
        val stackfile = service.stackfile
        if (stack != null && stackfile != null) {
            val command = CreateProjectByStackfile(
                project,
                stack,
                stackfile
            )
            val listeners = listOf(
                NotifyStackYamlUpdatedCommandListener(),
                LinkGradleProjectsToProjectListener(project),
                LinkMavenProjectsToProjectListener(project)
            )
            command.run(CompositeCommandEndedListener(listeners))
        }
    }
}