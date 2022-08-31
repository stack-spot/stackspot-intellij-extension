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

package com.stackspot.model

import com.stackspot.constants.Constants
import com.stackspot.yaml.parseStackYaml
import java.nio.file.Path
import kotlin.io.path.exists

class ImportedStacks {

    fun list(): List<Stack> {
        val stacksDir = getStacksDirPath().toFile()
        return stacksDir.walk().filter {
            it.isDirectory
        }.mapNotNull {
            it.parseStackYaml()
        }.sortedBy {
            it.name.lowercase()
        }.toList()
    }

    fun getByName(name: String): Stack? {
        return list().firstOrNull { it.name == name }
    }

    private fun getStacksDirPath(): Path {
        val stkHome = Constants.Paths.STK_HOME
        val stacks = stkHome.resolve("stacks")
        val stackDir: Path = if (stacks.exists()) {
            stacks
        } else {
            stkHome.resolve("plugins")
        }
        return stackDir
    }
}