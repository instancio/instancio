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
package org.instancio.test.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.instancio.Select.all
import org.instancio.junit.InstancioExtension
import org.instancio.test.support.tags.Feature
import org.instancio.test.support.tags.FeatureTag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@FeatureTag(Feature.SUBTYPE)
@ExtendWith(InstancioExtension::class)
internal class KSealedClassTest {

    private sealed class SealedClass {
        abstract val foo: String
    }

    private data class Subclass(override val foo: String, val bar: String) : SealedClass()

    private class Holder(val sealedClass: SealedClass)

    @Test
    fun shouldPopulateAllFields() {
        val result = Instancio.of(Holder::class.java)
            .subtype(all(SealedClass::class.java), Subclass::class.java)
            .create()

        assertThat(result).isNotNull
        assertThat(result.sealedClass.foo).isNotBlank
        assertThat(result.sealedClass).isExactlyInstanceOf(Subclass::class.java)

        val subclass = result.sealedClass as Subclass
        assertThat(subclass.bar).isNotBlank
    }
}