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
import com.intellij.ui.components.BrowserLink
import com.intellij.ui.dsl.builder.*
import com.stackspot.intellij.services.GetDocumentationService
import com.stackspot.intellij.ui.project_wizard.SelectedStackfile
import com.stackspot.intellij.ui.project_wizard.StackfileComboboxModel
import com.stackspot.model.ImportedStacks
import com.stackspot.model.Stack
import com.stackspot.model.Stackfile
import java.awt.event.ItemEvent
import java.util.concurrent.Executors
import javax.swing.JEditorPane
import javax.swing.JPanel

class StackSpotSuccessPanel(private val parentPanel: StackSpotParentPanel) {

    private lateinit var stackDescriptionLabel: Cell<JEditorPane>
    private lateinit var linkPlaceholder: Placeholder

    private val importedStacks: List<Stack> = ImportedStacks.getInstance().list()
    private val stackfilesByStack: List<Pair<Stack, List<Stackfile>>> =
        importedStacks.map { stack ->
            val stackfiles = stack.listStackfiles().sortedBy { it.name }
            Pair(stack, stackfiles)
        }
    var selectedStackfile: SelectedStackfile? =
        stackfilesByStack
            .find { list -> list.second.isNotEmpty() }
            ?.let { SelectedStackfile(it.first, it.second.first()) }

    fun getComponent(): JPanel {
        val p = panel {
            row("Stackfile:") {
                comboBox(
                    StackfileComboboxModel(stackfilesByStack)
                ).component.addItemListener {
                    if (it.stateChange == ItemEvent.SELECTED) {
                        selectedStackfile = it.item as SelectedStackfile
                        stackDescriptionLabel.component.text = selectedStackfile?.stackfile?.description
                        setBrowserLink(selectedStackfile)
                        setParentSelectedStackfile(selectedStackfile)
                    }
                }
            }.topGap(TopGap.MEDIUM)
            row("Description:") {
                val description = selectedStackfile?.stackfile?.description ?: ""
                stackDescriptionLabel = comment(description, 80)
            }
            row(EMPTY_LABEL) {
                linkPlaceholder = placeholder()
            }
        }
        setBrowserLink(selectedStackfile)
        setParentSelectedStackfile(selectedStackfile)
        return p
    }

    private fun setBrowserLink(selectedStackfile: SelectedStackfile?) {
        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
            val url = service<GetDocumentationService>().getDocumentationUrl(selectedStackfile?.stack)
            linkPlaceholder.component = url.takeIf { str -> str.isNotEmpty() }?.let {
                BrowserLink("Read the Stack documentation", it)
            }
        }
        executor.shutdown()
    }

    private fun setParentSelectedStackfile(selectedStackfile: SelectedStackfile?) {
        parentPanel.selectedStackfile = selectedStackfile
    }

}