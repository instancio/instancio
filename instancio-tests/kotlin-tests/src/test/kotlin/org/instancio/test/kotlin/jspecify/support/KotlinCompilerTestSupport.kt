/*
 * Copyright 2022-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.test.kotlin.jspecify.support

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.konan.file.createTempDir
import org.jetbrains.kotlin.konan.file.createTempFile


/**
 * Asserts that the given {@code source} snippet produces the
 * {@code expectedError} ()
 */
fun assertKotlinCompilationError(source: String, expectedError: String) {
    val srcFile = createTempFile("snippet", ".kt").apply {
        writeText(source.trimIndent())
        deleteOnExit()
    }
    val outDir = createTempDir("kotlinc-out").apply { deleteOnExit() }

    val errors = mutableListOf<String>()
    val collector = object : MessageCollector {
        override fun clear() {}
        override fun hasErrors() = errors.isNotEmpty()
        override fun report(
            severity: CompilerMessageSeverity,
            message: String,
            location: CompilerMessageSourceLocation?
        ) {
            if (severity.isError) errors += message
        }
    }

    val args = K2JVMCompilerArguments().apply {
        freeArgs = listOf(srcFile.absolutePath)
        destination = outDir.absolutePath
        classpath = System.getProperty("java.class.path")
        noStdlib = false
        noReflect = true
    }

    K2JVMCompiler().exec(collector, Services.EMPTY, args)

    assertThat(errors)
        .isNotEmpty()
        .withFailMessage("Expected error containing \"$expectedError\" but got:\n${errors.joinToString("\n")}")
        .anyMatch { expectedError in it }
}