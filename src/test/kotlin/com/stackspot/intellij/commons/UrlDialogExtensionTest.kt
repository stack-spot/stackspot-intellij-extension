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

package com.stackspot.intellij.commons

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

internal class UrlDialogExtensionTest {

    @ParameterizedTest
    @NullAndEmptySource
    fun `url should be invalid when is null or empty string`(url: String?) {
        url.isUrlValid() shouldBe false
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "git@github.com:bla/bla.git",
            "git@gitlab.com:bla/bla.git",
            "git@gitcorp.prod.aws.cloud.ihf:bla/bla",
            "https://github.com/bla/bla.git"
        ]
    )
    fun `url should be valid`(url: String) {
        url.isUrlValid() shouldBe true
    }

    @ParameterizedTest
    @ValueSource(strings = ["aaa", "aa@aa"])
    fun `url should be invalid`(url: String) {
        url.isUrlValid() shouldBe false
    }

}