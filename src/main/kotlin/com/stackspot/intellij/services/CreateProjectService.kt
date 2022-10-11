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
import com.stackspot.constants.Constants
import com.stackspot.intellij.commands.BackgroundCommandRunner
import com.stackspot.intellij.commands.git.GitConfig
import com.stackspot.intellij.commands.stk.Version
import com.stackspot.intellij.services.enums.ProjectWizardState
import com.stackspot.model.ImportedStacks
import com.stackspot.model.Stack
import com.stackspot.model.Stackfile
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val STK_VERSION_MESSAGE = "stk version"

@Service
class CreateProjectService() {

    var stack: Stack? = null
    var stackfile: Stackfile? = null
    val state: ProjectWizardState
        get() {
            return if (!isInstalled()) {
                ProjectWizardState.NOT_INSTALLED
            } else if (!ImportedStacks.getInstance().hasStackFiles()) {
                ProjectWizardState.STACKFILES_EMPTY
            } else if (!isGitConfigOk()) {
                ProjectWizardState.GIT_CONFIG_NOT_OK
            } else {
                ProjectWizardState.OK
            }
        }

    private var version = Version()
    private var gitConfigCmd = GitConfig(Constants.Paths.STK_HOME.toString())

    constructor(
        version: Version = Version(),
        gitConfigCmd: GitConfig = GitConfig(Constants.Paths.STK_HOME.toString())
    ) : this() {
        this.version = version
        this.gitConfigCmd = gitConfigCmd
    }

    private fun isInstalled(): Boolean {
        val stdout = version.runSync().stdout
        return stdout.contains(STK_VERSION_MESSAGE)
    }
    fun isStackfileSelected(): Boolean = stack != null && stackfile != null

    fun clearInfo() {
        stack = null
        stackfile = null
    }

    fun saveInfo(stack: Stack?, stackfile: Stackfile?): CreateProjectService {
        this.stack = stack
        this.stackfile = stackfile
        return this
    }

    fun addGitConfig(username: String, email: String) {
        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
            gitConfigCmd.flags = arrayOf("--global", "user.name", "\"$username\"")
            gitConfigCmd.run()

            gitConfigCmd.flags = arrayOf("--global", "user.email", "\"$email\"")
            gitConfigCmd.run()
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
        gitConfigCmd.flags = arrayOf("--get", "user.email")
        gitConfigCmd.run()
        return (gitConfigCmd.runner as BackgroundCommandRunner).stdout
    }

    private fun getUsernameGitConfig(): String {
        gitConfigCmd.flags = arrayOf("--get", "user.name")
        gitConfigCmd.run()
        return (gitConfigCmd.runner as BackgroundCommandRunner).stdout
    }
}