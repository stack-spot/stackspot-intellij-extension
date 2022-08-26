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

package com.stackspot.intellij.ui.project_wizard.panels

import com.intellij.openapi.components.service
import com.intellij.ui.dsl.builder.Placeholder
import com.intellij.ui.dsl.builder.panel
import com.stackspot.intellij.services.CreateProjectService
import com.stackspot.intellij.services.enums.ProjectWizardState
import com.stackspot.intellij.ui.project_wizard.SelectedStackfile
import com.stackspot.intellij.ui.project_wizard.StackSpotWizardStep
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel

class StackSpotParentPanel {

    private lateinit var panelPlaceholder: Placeholder
    private val service: CreateProjectService = service()

    var selectedStackfile: SelectedStackfile? = null

    fun getComponent(): JComponent {
        val container = panel {
            row {
                val clazz = StackSpotWizardStep::class.java
                val imageUrl = clazz.getResource("/images/banner.png")
                val bannerImage: BufferedImage = ImageIO.read(imageUrl)
                val banner = JLabel(ImageIcon(bannerImage))
                cell(banner)
            }
            indent {
                row {
                    panelPlaceholder = placeholder()
                }
            }
        }
        reload()
        return container
    }

    fun reload() {
        panelPlaceholder.component = when (service.state) {
            ProjectWizardState.OK -> StackSpotSuccessPanel(parentPanel = this).getComponent()
            ProjectWizardState.NOT_INSTALLED -> StackSpotNotInstalledErrorPanel().getComponent()
            ProjectWizardState.STACKFILES_EMPTY -> StackSpotNoStackfilesErrorPanel(parentPanel = this).getComponent()
            ProjectWizardState.GIT_CONFIG_NOT_OK -> StackSpotGitConfigErrorPanel(parentPanel = this).getComponent()
        }
    }

    fun showImportingStack() {
        panelPlaceholder.component = panel {
            row {
                text("Importing stacks...")
            }
        }
    }
}