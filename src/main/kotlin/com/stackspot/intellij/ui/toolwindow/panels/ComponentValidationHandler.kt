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

package com.stackspot.intellij.ui.toolwindow.panels

import com.intellij.ui.dsl.builder.Row
import com.stackspot.model.component.Helper

abstract class ComponentValidationHandler {

    private var next: ComponentValidationHandler? = null

    fun linkHandler(handler: ComponentValidationHandler): ComponentValidationHandler {
        this.next = handler
        return handler
    }

    abstract fun check(helper: Helper, row: Row): Row?

    fun checkNext(helper: Helper, row: Row): Row? {
        return next?.check(helper, row)
    }
}