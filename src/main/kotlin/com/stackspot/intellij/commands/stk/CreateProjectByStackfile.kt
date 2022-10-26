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

package com.stackspot.intellij.commands.stk

import com.intellij.openapi.project.Project
import com.stackspot.intellij.commands.BaseCommand
import com.stackspot.intellij.ui.StackSpotTerminalRunner
import com.stackspot.model.Stack
import com.stackspot.model.Stackfile

class CreateProjectByStackfile(
    private val project: Project,
    private val stack: Stack,
    private val stackfile: Stackfile
) : BaseCommand(StackSpotTerminalRunner(project)) {

    override fun commandLine(): List<String> {
        return listOf(
            "stk",
            "create",
            stackfile.type,
            project.name,
            "--stackfile",
            "${stack.name}/${stackfile.name}",
            "--skip-empty-dir-check"
        )
    }
}