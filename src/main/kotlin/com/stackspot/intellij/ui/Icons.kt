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

package com.stackspot.intellij.ui

import com.intellij.openapi.util.IconLoader

object Icons {
    @JvmField
    val STACK_SPOT = IconLoader.getIcon("/images/logo.svg", javaClass)

    @JvmField
    val STACK_SPOT_MONO = IconLoader.getIcon("/images/logo_mono.svg", javaClass)

    @JvmField
    val APPLY_PLUGIN = IconLoader.getIcon("/images/apply_plugin.svg", javaClass)

    @JvmField
    val REFRESH = IconLoader.getIcon("/images/refresh.svg", javaClass)

    @JvmField
    val TRASH = IconLoader.getIcon("/images/trash.svg", javaClass)

    @JvmField
    val AVAILABLE_PLUGINS = IconLoader.getIcon("/images/available_plugins.svg", javaClass)

    @JvmField
    val APPLICATION_DETAILS = IconLoader.getIcon("/images/application_details.svg", javaClass)

    @JvmField
    val IMPORT_STACK = IconLoader.getIcon("/images/import_stack.svg", javaClass)

    @JvmField
    val IMPORTED_STACKS = IconLoader.getIcon("/images/imported_stacks.svg", javaClass)

    @JvmField
    val WARNING = IconLoader.getIcon("/images/warning.svg", javaClass)
}