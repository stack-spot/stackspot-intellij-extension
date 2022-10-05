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

package com.stackspot.intellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.stackspot.intellij.commands.listeners.NotifyStacksUpdatedCommandListener
import com.stackspot.intellij.ui.Icons


private const val REFRESH_ALL_STACKS = "Refresh"

class RefreshAction : AnAction(REFRESH_ALL_STACKS, REFRESH_ALL_STACKS, Icons.REFRESH), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            NotifyStacksUpdatedCommandListener().notifyEnded()
        }
    }
}