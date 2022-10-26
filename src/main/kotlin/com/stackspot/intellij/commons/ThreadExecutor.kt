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

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun <R : Any> singleThread(
    timeout: Long = 500L,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    lambda: () -> R
): R {
    var done = false
    var result = Any()
    val executor = Executors.newSingleThreadExecutor()
    try {
        executor.submit {
            result = lambda()
            done = true
        }

        while (!done) {
            executor.awaitTermination(timeout, unit)
        }
    } finally {
        executor.shutdownNow()
    }

    return result as R
}

suspend fun <R : Any> singleThreadAsCoroutine(
    lambda: () -> R
): Deferred<R> {
    val dispatch = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    return withContext(dispatch) {
        async { lambda() }
    }
}

