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

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.stackspot.intellij.commands.listeners.NotifyStacksUpdatedCommandListener
import io.mockk.*
import org.junit.jupiter.api.Test

class RefreshActionTest {

    private val anActionEvent: AnActionEvent = mockk()
    private val notify: NotifyStacksUpdatedCommandListener = mockk(relaxUnitFun = true)
    private val project: Project = mockk()

    @Test
    fun `when project is not null`() {
        val refreshAction = RefreshAction(notify)
        every { anActionEvent.project } returns project
        refreshAction.actionPerformed(anActionEvent)
        verify { notify.notifyEnded() }
    }

    @Test
    fun `when project is null`() {
        val refreshAction = RefreshAction(notify)
        every { anActionEvent.project } returns null
        refreshAction.actionPerformed(anActionEvent)
        verify(exactly = 0) { notify.notifyEnded() }
        confirmVerified(notify)
    }
}