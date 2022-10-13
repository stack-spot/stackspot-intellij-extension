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

package com.stackspot.intellij.commands.listeners

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.stackspot.intellij.commands.stk.CommandInfoList
import com.stackspot.intellij.messaging.StackUpdatesNotifier
import com.stackspot.model.ImportedStacks
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotifyStacksUpdatedCommandListenerTest {

    private val application: Application = mockk()
    private val stackUpdatesNotifier: StackUpdatesNotifier = mockk(relaxUnitFun = true)
    private val stackInfoList: CommandInfoList = mockk(relaxed = true)
    private val stackfileInfoList: CommandInfoList = mockk(relaxed = true)
    private val templateInfoList: CommandInfoList = mockk(relaxed = true)
    private val pluginInfoList: CommandInfoList = mockk(relaxed = true)

    @BeforeEach
    fun init() {
        stubbing()
        ImportedStacks.getInstance(stackInfoList, stackfileInfoList, templateInfoList, pluginInfoList)
        mockkObject(ImportedStacks)
    }

    private fun stubbing() {
        coEvery { stackInfoList.runAsync().await().stdout } returns "[]"
        coEvery { stackfileInfoList.runAsync().await().stdout } returns "{}"
        coEvery { templateInfoList.runAsync().await().stdout } returns "{}"
        coEvery { pluginInfoList.runAsync().await().stdout } returns "{}"
    }

    @Test
    fun `when notify stacks update, it must update the tool window`() {
        mockkStatic(ApplicationManager::class)
        every { ApplicationManager.getApplication() } returns application
        every { application.messageBus.syncPublisher(StackUpdatesNotifier.TOPIC) } returns stackUpdatesNotifier

        val listener = NotifyStacksUpdatedCommandListener()
        listener.notifyEnded()

        verify { stackUpdatesNotifier.stacksUpdated() }
        verify { application.messageBus }
        verify { ImportedStacks.getInstance(any(), any(), any(), any()) }
        confirmVerified(stackUpdatesNotifier)
        confirmVerified(application)
        confirmVerified(ImportedStacks)
    }
}