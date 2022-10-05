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

import com.stackspot.exceptions.NotFoundException

enum class RepositoryUriGenerator(private val domainName: Array<String>? = null) {

    GITHUB(domainName = arrayOf("github.com")) {
        override fun generateUri(branchName: String): String {
            return "/blob/$branchName"
        }
    },
    GITLAB(domainName = arrayOf("gitlab.com", "gitcorp.prod.aws.cloud.ihf")) {
        override fun generateUri(branchName: String): String {
            return "/~/blob/$branchName"
        }
    },
    BITBUCKET(domainName = arrayOf("bitbucket.org")) {
        override fun generateUri(branchName: String): String {
            return "/src/$branchName"
        }
    },
    UNKNOWN {
        override fun generateUri(branchName: String): String {
            throw NotFoundException("Repository manager not found.")
        }
    };

    abstract fun generateUri(branchName: String): String

    companion object {
        fun findByName(name: String): RepositoryUriGenerator =
            RepositoryUriGenerator.values()
                .find { enum -> name.isNotEmpty() && enum.name.contains(name, ignoreCase = true) } ?: UNKNOWN

        fun findByDomainName(domainName: String): RepositoryUriGenerator {
            var repository: RepositoryUriGenerator = UNKNOWN
            RepositoryUriGenerator.values().forEach { repo ->
                repo.domainName?.find { dn ->
                    domainName.contains(dn, ignoreCase = true)
                }?.apply { repository = repo }
            }
            return repository
        }
    }

}