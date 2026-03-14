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

import java.io.File
import java.nio.file.Files
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services

/**
 * Compiles the given Kotlin [source] snippet at test runtime and asserts
 * that it produces a compiler error containing [expectedError].
 *
 * This is used to verify that JSpecify nullability annotations on Instancio's
 * Java APIs are enforced by the Kotlin compiler. This is the only way to test
 * the *negative* case - that invalid nullable/non-null usage is rejected at
 * compile time - since such code cannot appear in a regular test file.
 */
fun assertKotlinCompilationError(source: String, expectedError: String) {
    val srcFile = File.createTempFile("snippet", ".kt").apply {
        writeText(source.trimIndent())
        deleteOnExit()
    }
    val outDir = Files.createTempDirectory("kotlinc-out").toFile().apply { deleteOnExit() }

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
        .withFailMessage(
            "Expected error containing \"$expectedError\" but got:" +
                    "\n${errors.joinToString("\n")}"
        )
        .anyMatch { expectedError in it }
}