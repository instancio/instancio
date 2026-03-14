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
package org.instancio.test.kotlin.jspecify

import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.instancio.Select.field
import org.instancio.junit.InstancioExtension
import org.instancio.test.kotlin.jspecify.support.assertKotlinCompilationError
import org.instancio.test.support.pojo.basic.StringHolder
import org.instancio.test.support.tags.Feature
import org.instancio.test.support.tags.FeatureTag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Verifies Kotlin's compatibility with JSpecify-annotated
 * [org.instancio.generator.specs.EmitGeneratorSpec]
 * with both nullable and non-nullable arguments.
 */
@FeatureTag(Feature.EMIT_GENERATOR)
@ExtendWith(InstancioExtension::class)
internal class KEmitGeneratorSpecJspecifyTest {

    @Test
    fun `when emit is typed as String, items should reject null arguments`() {
        val source = """
        import org.instancio.Instancio
        import org.instancio.Select.field
        import org.instancio.test.support.pojo.basic.StringHolder
        
        val result = Instancio.of(StringHolder::class.java)
            .generate(field(StringHolder::getValue)) { gen ->
                gen.emit<String>()              // using `String` with `null` items() args
                    .items(null, "foo"))        // should produce a compilation error
            }
            .create()
        """

        assertKotlinCompilationError(source, "Null cannot be a value of a non-null type 'String'")
    }

    @Test
    fun emitSpec_nullable() {
        val result = Instancio.ofList(StringHolder::class.java)
            .size(5)
            .generate(field(StringHolder::getValue)) { gen ->
                // Changing `String?` to `String` should result in a
                // compilation error in all of the "item" methods
                gen.emit<String?>()
                    .items(null, "foo")
                    .item(null, 1)
                    .items(listOf(null, "bar"))
            }
            .create()

        assertThat(result).extracting("value").contains(null, "foo", null, null, "bar")
    }
}