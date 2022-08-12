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

package com.stackspot.intellij.ui.project_wizard

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import com.stackspot.intellij.services.CreateProjectService
import com.stackspot.intellij.services.enums.ProjectWizardState
import com.stackspot.intellij.ui.Icons
import com.stackspot.intellij.ui.project_wizard.panels.StackSpotParentPanel
import javax.swing.JComponent

class StackSpotWizardStep(private val context: WizardContext) : ModuleWizardStep() {

    private val service: CreateProjectService = service()
    private val stackSpotParentPanel = StackSpotParentPanel()

    override fun getComponent(): JComponent {
        return stackSpotParentPanel.getComponent()
    }

    override fun updateDataModel() {
        service.saveInfo(
            stackSpotParentPanel.selectedStackfile?.stack,
            stackSpotParentPanel.selectedStackfile?.stackfile
        )
    }

    override fun validate(): Boolean {
        val state = service.state
        val project = context.project
        if (ProjectWizardState.OK != state) {
            Messages.showMessageDialog(project, state.message, "", Icons.WARNING)
            return false
        }

        return true
    }

}