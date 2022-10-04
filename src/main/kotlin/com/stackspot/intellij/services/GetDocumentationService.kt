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
import com.intellij.util.containers.isNullOrEmpty
import com.stackspot.intellij.commands.BackgroundCommandRunner
import com.stackspot.intellij.commands.git.GitBranch
import com.stackspot.intellij.commands.git.GitConfig
import com.stackspot.intellij.services.enums.RepositoryUriGenerator
import com.stackspot.model.Stack
import java.util.logging.Level
import java.util.logging.Logger

@Service
class GetDocumentationService {

    companion object {
        val LOGGER: Logger = Logger.getLogger(GetDocumentationService::class.java.name)
        val REMOTE_URL_SSH_REGEX = Regex("(?i)(git@).*?.git")
    }

    fun getDocumentationUrl(stack: Stack?): String {
        if (stack == null) {
            return ""
        }

        var remoteUrl = getRemoteUrl(stack)
        val branchName = getCurrentBranchName(stack)

        val useCaseContent = (stack.useCasesKebab ?: stack.useCases)
            ?.takeUnless { it.isNullOrEmpty() }
            ?.first()?.content

        if (isSshRemoteUrl(remoteUrl)) {
            remoteUrl = changeSshToHttps(remoteUrl)
        }

        if (remoteUrl.isNotBlank() && branchName.isNotBlank()) {
            useCaseContent?.let { useCaseUri ->
                return addUseCaseToUri(remoteUrl, branchName, useCaseUri)
            }
        }

        return remoteUrl
    }

    private fun addUseCaseToUri(remoteUrl: String, branchName: String, useCaseUri: String): String {
        return kotlin.runCatching {
            RepositoryUriGenerator.findByDomainName(remoteUrl).generateUri(branchName)
        }.onFailure { exception ->
            LOGGER.log(Level.SEVERE, exception.message, exception)
            return remoteUrl
        }.onSuccess { repoUri ->
            return "$remoteUrl$repoUri/$useCaseUri"
        }.getOrElse { "" }
    }

    private fun getRemoteUrl(stack: Stack): String {
        val gitRemoteCmd = GitConfig(stack.location.toPath().parent.toString(), arrayOf("--get", "remote.origin.url"))
        gitRemoteCmd.run()
        return (gitRemoteCmd.runner as BackgroundCommandRunner).stdout
    }

    private fun getCurrentBranchName(stack: Stack): String {
        val gitBranchCmd = GitBranch(stack, arrayOf("--show-current"))
        gitBranchCmd.run()
        return (gitBranchCmd.runner as BackgroundCommandRunner).stdout
    }

    private fun isSshRemoteUrl(remoteUrl: String): Boolean = REMOTE_URL_SSH_REGEX.matches(remoteUrl)

    private fun changeSshToHttps(remoteUrl: String): String =
        remoteUrl
            .replace(":", "/")
            .replace("git@", "https://")
            .removeSuffix(".git")

}