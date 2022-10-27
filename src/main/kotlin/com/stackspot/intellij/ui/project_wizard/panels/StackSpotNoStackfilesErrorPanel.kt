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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.panel
import com.stackspot.constants.Constants
import com.stackspot.intellij.actions.IMPORT_STACK
import com.stackspot.intellij.commands.BackgroundCommandRunner
import com.stackspot.intellij.commands.listeners.NotifyProjectWizardImportedStack
import com.stackspot.intellij.commands.stk.ImportStack
import com.stackspot.intellij.commons.ErrorDialog.URL_IS_NOT_VALID_MESSAGE
import com.stackspot.intellij.commons.ErrorDialog.URL_IS_NOT_VALID_TITLE
import com.stackspot.intellij.commons.InputDialog.REPOSITORY_URL
import com.stackspot.intellij.commons.isUrlValid
import java.util.concurrent.Executors
import javax.swing.JComponent

class StackSpotNoStackfilesErrorPanel(val parentPanel: StackSpotParentPanel) {

    fun getComponent(): JComponent {
        return panel {
            row {
                text(
                    "In order to create a StackSpot project, you need to have stacks imported with <b>stackfiles.</b>",
                    maxLineLength = 80
                )
            }
            row {
                text(
                    """
                        Check available stacks <a href="https://stackspot.com/studios">here</a><br>
                        Or create a demo application following
                        <a href="https://docs.stackspot.com.br/latest/docs/user-guide/howto-create-app">
                        this link</a>
                    """.trimMargin(),
                    maxLineLength = 80
                )
            }
            row {
                button("Import Stack") {
                    ApplicationManager.getApplication().invokeLater { runImportStack() }
                }
            }
        }
    }

    private fun askForStackUrl(): String? {
        return Messages.showInputDialog(REPOSITORY_URL, IMPORT_STACK, Messages.getQuestionIcon())
    }

    private fun runImportStack() {
        val url = askForStackUrl() ?: return

        if (!url.isUrlValid()) {
            Messages.showErrorDialog(URL_IS_NOT_VALID_MESSAGE, URL_IS_NOT_VALID_TITLE)
            return
        }

        parentPanel.showImportingStack()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
                ImportStack(url, BackgroundCommandRunner())
                    .run(NotifyProjectWizardImportedStack(parentPanel))
        }
        executor.shutdown()
    }

}
