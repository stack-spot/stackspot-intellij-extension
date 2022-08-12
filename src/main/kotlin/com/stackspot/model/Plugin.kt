/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

import com.fasterxml.jackson.annotation.JsonProperty

class Plugin(
    override val name: String,
    override val description: String,
    override val types: List<String>,
    override val inputs: List<Input>?,
    override val displayName: String?,
    @JsonProperty("display-name") override val displayNameKebab: String?,
) : Template(name, description, types, inputs, displayName, displayNameKebab)