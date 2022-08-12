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

package com.stackspot.intellij.ui.project_wizard.panels

import com.intellij.openapi.components.service
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.stackspot.intellij.services.CreateProjectService
import javax.swing.JComponent

class StackSpotGitConfigErrorPanel(private val parentPanel: StackSpotParentPanel) {

    lateinit var username: Cell<JBTextField>
    lateinit var email: Cell<JBTextField>

    fun getComponent(): JComponent {
        return panel {
            row {
                text("Git config is not defined. Please insert your username and e-mail.", maxLineLength = 80)
            }
            group("Git Config Inputs") {
                row("Username:") {
                    username = textField()
                }
                row("E-mail:") {
                    email = textField()
                }
                row {
                    button("Save") {
                        saveGitConfig(username, email)
                        parentPanel.reload()
                    }.horizontalAlign(HorizontalAlign.RIGHT)
                }
            }
        }
    }

    private fun saveGitConfig(username: Cell<JBTextField>, email: Cell<JBTextField>) {
        val service: CreateProjectService = service()
        service.addGitConfig(username.component.text, email.component.text)
    }
}