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

import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class StackSpotNotInstalledErrorPanel {

    fun getComponent(): JComponent {
        return panel {
            row {
                text(
                    "In order to create a StackSpot project, you need to have STK CLI installed.",
                    maxLineLength = 80
                )
            }
            row {
                browserLink(
                    "Go to installation doc",
                    "https://docs.stackspot.com.br/latest/docs/stk-cli/installation"
                )
            }
        }
    }

}