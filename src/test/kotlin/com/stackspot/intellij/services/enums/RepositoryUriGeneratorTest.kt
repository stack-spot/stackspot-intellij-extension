package com.stackspot.intellij.services.enums

import com.stackspot.exceptions.NotFoundException
import com.stackspot.intellij.services.enums.RepositoryUriGenerator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

internal class RepositoryUriGeneratorTest {

    private val branchName = "main"

    @Nested
    inner class SuccessCases {

        @ParameterizedTest
        @MethodSource("findByNameArgs")
        fun `should find by name`(name: String, expected: RepositoryUriGenerator) {
            val enum = RepositoryUriGenerator.findByName(name)
            enum shouldBe expected
        }

        @ParameterizedTest
        @MethodSource("findByDomainNameArgs")
        fun `should find by domain name`(domainName: String, expected: RepositoryUriGenerator) {
            val enum = RepositoryUriGenerator.findByDomainName(domainName)
            enum shouldBe expected
        }

        @ParameterizedTest
        @MethodSource("generateRepoUriArgs")
        fun `should generate repository uri`(actual: RepositoryUriGenerator, expected: String) {
            actual.generateUri(branchName) shouldBe expected
        }

        private fun findByNameArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of("github", RepositoryUriGenerator.GITHUB),
                Arguments.of("gitlab", RepositoryUriGenerator.GITLAB),
                Arguments.of("bitbucket", RepositoryUriGenerator.BITBUCKET)
            )

        private fun findByDomainNameArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of("github.com", RepositoryUriGenerator.GITHUB),
                Arguments.of("gitlab.com", RepositoryUriGenerator.GITLAB),
                Arguments.of("gitcorp.prod.aws.cloud.ihf", RepositoryUriGenerator.GITLAB),
                Arguments.of("bitbucket.org", RepositoryUriGenerator.BITBUCKET)
            )

        private fun generateRepoUriArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(RepositoryUriGenerator.GITHUB, "/blob/main"),
                Arguments.of(RepositoryUriGenerator.GITLAB, "/~/blob/main"),
                Arguments.of(RepositoryUriGenerator.BITBUCKET, "/src/main")
            )
    }

    @Nested
    inner class FailureCases {

        @ParameterizedTest
        @EmptySource
        fun `should return UNKNOWN enum when is empty string`(name: String) {
            val enum = RepositoryUriGenerator.findByName(name)
            enum shouldBe RepositoryUriGenerator.UNKNOWN
        }

        @ParameterizedTest
        @ValueSource(strings = ["anything"])
        fun `should return UNKNOWN enum when there isn't match`(name: String) {
            val enum = RepositoryUriGenerator.findByName(name)
            enum shouldBe RepositoryUriGenerator.UNKNOWN
        }

        @Test
        fun `should throw NotFoundException when UNKNOWN try to generate uri`() {
            shouldThrow<NotFoundException> { RepositoryUriGenerator.UNKNOWN.generateUri(branchName) }
        }

        @ParameterizedTest
        @EmptySource
        fun `should return UNKNOWN when domain name is empty string`(domainName: String) {
            val enum = RepositoryUriGenerator.findByDomainName(domainName)
            enum shouldBe RepositoryUriGenerator.UNKNOWN
        }
    }

}