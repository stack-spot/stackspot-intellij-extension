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

object Constants {
    const val MODULE_TYPE = "STACK_SPOT_TYPE"
    const val MODULE_TYPE_NAME = "StackSpot"

    object Files {
        const val STK_YAML = "stk.yaml"
    }

    object Paths {
        private val USER_HOME: Path = Path(System.getProperty("user.home"))
        val STK_HOME: Path = USER_HOME.resolve(".stk")
    }
}