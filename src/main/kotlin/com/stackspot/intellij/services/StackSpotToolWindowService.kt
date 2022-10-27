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

package com.stackspot.intellij.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.stackspot.model.AppliedTemplate
import com.stackspot.model.History
import com.stackspot.model.ImportedStacks
import com.stackspot.jackson.parseHistory
import java.io.File

@Service
class StackSpotToolWindowService {
    val importedStacks = ImportedStacks.getInstance()
    var project: Project? = null
    var history: History? = null
    var template: AppliedTemplate? = null
    var appliedPlugins: List<AppliedTemplate> = ArrayList(0)

    fun reloadProjectData() {
        val p = project
        if (p != null) {
            history = parseHistory(File(p.basePath))
            reloadHistoryData()
        }
    }

    private fun parseHistory(basePath: File): History? {
        val history = basePath.parseHistory()
        if (history != null) {
            return history
        }
        if (basePath.parentFile != null) {
            return parseHistory(basePath.parentFile)
        }
        return null
    }

    private fun reloadHistoryData() {
        val appliedTemplates = history?.appliedTemplates
        if (!appliedTemplates.isNullOrEmpty()) {
            template = appliedTemplates.first()
            appliedPlugins = appliedTemplates.subList(1, appliedTemplates.size)
        }
    }

    fun pluginsOrTemplatesNotApplied(plugins: List<String>? = null): List<String> {
        return plugins
            ?.filter { p -> appliedPlugins.none { ap -> ap.templateDataPath == p } && p != template?.templateDataPath }
            ?.toList() ?: listOf()
    }
}
