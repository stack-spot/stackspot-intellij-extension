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