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

package com.stackspot.constants

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

object Constants {
    const val MODULE_TYPE = "STACK_SPOT_TYPE"
    const val MODULE_TYPE_NAME = "StackSpot"

    object Files {
        const val STK_YAML = "stk.yaml"
        const val STACK_YML = "stack.yml"
        const val STACK_YAML = "stack.yaml"
        const val PLUGIN_YML = "plugin.yml"
        const val PLUGIN_YAML = "plugin.yaml"
        const val TEMPLATE_YML = "template.yml"
        const val TEMPLATE_YAML = "template.yaml"
    }

    object Paths {
        val USER_HOME: Path = Path(System.getProperty("user.home"))
        val STK_HOME: Path = USER_HOME.resolve(".stk")
        val STK_BIN: Path = STK_HOME.resolve("bin")
        val STACKS_DIR: Path = if (STK_HOME.resolve("stacks").exists()) {
            STK_HOME.resolve("stacks")
        } else {
            STK_HOME.resolve("plugins")
        }
    }
}