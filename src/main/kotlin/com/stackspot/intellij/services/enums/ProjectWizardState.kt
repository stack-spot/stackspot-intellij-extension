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

package com.stackspot.intellij.services.enums

enum class ProjectWizardState(val message: String? = null) {
    NOT_INSTALLED("Please install STK CLI before continue."),
    STACKFILES_EMPTY("Please import a stack with stackfiles before continue."),
    GIT_CONFIG_NOT_OK("Please insert git config username and e-mail before continue."),
    OK
}