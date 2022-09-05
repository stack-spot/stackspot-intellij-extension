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
import com.intellij.util.io.exists
import com.stackspot.constants.Constants
import com.stackspot.intellij.commands.BackgroundCommandRunner
import com.stackspot.intellij.commands.git.GitConfig
import com.stackspot.intellij.services.enums.ProjectWizardState
import com.stackspot.model.ImportedStacks
import com.stackspot.model.Stack
import com.stackspot.model.Stackfile
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class CreateProjectService {

    var stack: Stack? = null
    var stackfile: Stackfile? = null
    val state: ProjectWizardState
        get() {
            return if (!Constants.Paths.STK_BIN.exists()) {
                ProjectWizardState.NOT_INSTALLED
            } else if (!ImportedStacks().hasStackFiles()) {
                ProjectWizardState.STACKFILES_EMPTY
            } else if (!isGitConfigOk()) {
                ProjectWizardState.GIT_CONFIG_NOT_OK
            } else {
                ProjectWizardState.OK
            }
        }

    fun isStackfileSelected(): Boolean = stack != null && stackfile != null

    fun clearInfo() {
        stack = null
        stackfile = null
    }

    fun saveInfo(stack: Stack?, stackfile: Stackfile?) {
        this.stack = stack
        this.stackfile = stackfile
    }

    fun addGitConfig(username: String, email: String) {
        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
            val workingDir = Constants.Paths.STK_HOME.toString()
            GitConfig(workingDir, arrayOf("--global", "user.name", "\"$username\"")).run()
            GitConfig(workingDir, arrayOf("--global", "user.email", "\"$email\"")).run()
        }
        executor.shutdown()
    }

    private fun isGitConfigOk(): Boolean {
        var done = false
        var username = ""
        var email = ""
        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
            username = getUsernameGitConfig()
            email = getEmailGitConfig()
            done = true
        }
        while (!done) {
            executor.awaitTermination(500L, TimeUnit.MILLISECONDS)
        }
        executor.shutdown()
        return username.isNotEmpty() && email.isNotEmpty()
    }

    private fun getEmailGitConfig(): String {
        val gitConfigUserEmail = GitConfig(Constants.Paths.STK_HOME.toString(), arrayOf("--get", "user.email"))
        gitConfigUserEmail.run()
        return (gitConfigUserEmail.runner as BackgroundCommandRunner).stdout
    }

    private fun getUsernameGitConfig(): String {
        val gitConfigUserName = GitConfig(Constants.Paths.STK_HOME.toString(), arrayOf("--get", "user.name"))
        gitConfigUserName.run()
        return (gitConfigUserName.runner as BackgroundCommandRunner).stdout
    }
}