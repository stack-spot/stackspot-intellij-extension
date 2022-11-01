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

package com.stackspot.model

import com.stackspot.intellij.commands.stk.Version
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val STK_VERSION_MESSAGE = "stk version"

class StkVersion(private val version: Version) {

    private lateinit var stkVersion: String

    init {
        runBlocking {
            launch { stkVersion = version.runAsync().await().stdout }
        }
    }

    companion object {
        @Volatile
        private lateinit var instance: StkVersion

        fun getInstance(
            version: Version = Version(),
            newInstance: Boolean = false
        ): StkVersion {
            synchronized(this) {
                if (!::instance.isInitialized || newInstance) {
                    instance = StkVersion(version)
                }
                return instance
            }
        }
    }

    fun isInstalled(): Boolean {
        return stkVersion.contains(STK_VERSION_MESSAGE)
    }
}