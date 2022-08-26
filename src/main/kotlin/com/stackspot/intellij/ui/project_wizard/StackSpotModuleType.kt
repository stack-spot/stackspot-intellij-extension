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

package com.stackspot.intellij.ui.project_wizard

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.stackspot.constants.Constants
import com.stackspot.intellij.ui.Icons
import javax.swing.Icon

class StackSpotModuleType : ModuleType<StackSpotModuleBuilder>(Constants.MODULE_TYPE) {

    companion object {
        val INSTANCE: StackSpotModuleType by lazy {
            ModuleTypeManager.getInstance().findByID(Constants.MODULE_TYPE) as StackSpotModuleType
        }
    }

    override fun createModuleBuilder(): StackSpotModuleBuilder = StackSpotModuleBuilder()

    override fun getName(): String = Constants.MODULE_TYPE_NAME

    override fun getDescription(): String = "Create a StackSpot project"

    override fun getNodeIcon(isOpened: Boolean): Icon = Icons.STACK_SPOT
}