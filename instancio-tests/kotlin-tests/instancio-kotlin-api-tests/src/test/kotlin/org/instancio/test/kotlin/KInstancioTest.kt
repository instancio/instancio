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
import org.instancio.junit.InstancioExtension
import org.instancio.junit.WithSettings
import org.instancio.kotlin.KInstancio
import org.instancio.kotlin.KSelect.allStrings
import org.instancio.kotlin.KSelect.field
import org.instancio.settings.Keys
import org.instancio.settings.Settings
import org.instancio.test.kotlin.pojo.person.basic.KStringHolder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstancioExtension::class)
class KInstancioTest {

    @Nested
    inner class Create {

        @Test
        fun nonNull() {
            val result: String = KInstancio.create<String>()

            assertThat(result).isNotBlank()
        }

        @Test
        fun nullable() {
            val result: String? = KInstancio.create<String?>()

            assertThat(result).isNotBlank()
        }

        @Test
        fun fromModel() {
            val model = KInstancio.of<String>()
                .set(allStrings(), "fixed")
                .toModel()

            val result = KInstancio.create(model)

            assertThat(result).isEqualTo("fixed")
        }
    }

    @Nested
    inner class CreateBlank {

        @Test
        fun blankPojo() {
            val result = KInstancio.createBlank<KStringHolder>()

            assertThat(result.value).isNull()
        }
    }

    @Nested
    inner class Stream {

        @Test
        fun nonNull() {
            val results = KInstancio.stream<String>()
                .limit(5)
                .toList()

            assertThat(results).hasSize(5).doesNotContainNull()
        }

        @Test
        fun fromModel() {
            val model = KInstancio.of<String>()
                .set(allStrings(), "fixed")
                .toModel()

            val results = KInstancio.stream(model)
                .limit(3)
                .toList()

            assertThat(results).hasSize(3).containsOnly("fixed")
        }
    }

    @Nested
    inner class Of {

        @Test
        fun nonNull() {
            val result: String = KInstancio.of<String>().create()

            assertThat(result).isNotBlank()
        }

        @Test
        fun nullable() {
            val result: String? = KInstancio.of<String?>()
                .set(allStrings(), null)
                .create()

            assertThat(result).isNull()
        }

        @Test
        fun fromModel() {
            val model = KInstancio.of<KStringHolder>()
                .set(field(KStringHolder::value), "base")
                .toModel()

            val result = KInstancio.of(model)
                .set(field(KStringHolder::value), "overridden")
                .create()

            assertThat(result.value).isEqualTo("overridden")
        }
    }

    @Nested
    inner class OfBlank {

        @Test
        fun blankPojo() {
            val result = KInstancio.ofBlank<KStringHolder>().create()

            assertThat(result.value).isNull()
        }

        @Test
        fun blankPojoWithOverride() {
            val result = KInstancio.ofBlank<KStringHolder>()
                .set(field(KStringHolder::value), "hello")
                .create()

            assertThat(result.value).isEqualTo("hello")
        }
    }

    @Nested
    inner class CollectionApi {

        @Nested
        inner class NonNullElements {

            @Test
            fun kotlinOfList() {
                val result = KInstancio.ofList<String>().create()

                assertThat(result).isNotEmpty().doesNotContainNull()
            }

            @Test
            fun kotlinOfSet() {
                val result = KInstancio.ofSet<String>().create()

                assertThat(result).isNotEmpty().doesNotContainNull()
            }

            @Test
            fun kotlinOfMap() {
                val result = KInstancio.ofMap<String, String>().create()

                assertThat(result).isNotEmpty()
                    .doesNotContainKey(null)
                    .doesNotContainValue(null)
            }
        }

        @Nested
        inner class CreateShortcuts {

            @Test
            fun kotlinCreateList() {
                val result = KInstancio.createList<String>()

                assertThat(result).isNotEmpty().doesNotContainNull()
            }

            @Test
            fun kotlinCreateSet() {
                val result = KInstancio.createSet<String>()

                assertThat(result).isNotEmpty().doesNotContainNull()
            }

            @Test
            fun kotlinCreateMap() {
                val result = KInstancio.createMap<String, String>()

                assertThat(result).isNotEmpty()
                    .doesNotContainKey(null)
                    .doesNotContainValue(null)
            }
        }

        /**
         * NOTE: using `String` instead of `String?` as element type
         * doesn't throw NPE even if collection contains null.
         */
        @Nested
        inner class NullableElements {

            @WithSettings
            val settings = Settings.create()
                .set(Keys.COLLECTION_ELEMENTS_NULLABLE, true)
                .set(Keys.MAP_KEYS_NULLABLE, true)
                .set(Keys.MAP_VALUES_NULLABLE, true)

            @Test
            fun kotlinOfList() {
                val result = KInstancio.ofList<String?>()
                    .size(1)
                    .set(allStrings(), null)
                    .create()

                assertThat(result).hasSize(1).containsOnlyNulls()
            }

            @Test
            fun kotlinOfSet() {
                val result = KInstancio.ofSet<String?>()
                    .size(1)
                    .set(allStrings(), null)
                    .create()

                assertThat(result).hasSize(1).containsOnlyNulls()
            }

            @Test
            fun kotlinOfMap() {
                val result = KInstancio.ofMap<String?, String?>()
                    .size(1)
                    .set(allStrings(), null)
                    .create()

                assertThat(result).hasSize(1).containsEntry(null, null)
            }
        }

        @Nested
        inner class FromElementModel {

            @Test
            fun ofList() {
                val elementModel = KInstancio.of<String>()
                    .set(allStrings(), "elem")
                    .toModel()

                val result = KInstancio.ofList(elementModel).create()

                assertThat(result).isNotEmpty().containsOnly("elem")
            }

            @Test
            fun ofSet() {
                val elementModel = KInstancio.of<String>()
                    .set(allStrings(), "elem")
                    .toModel()

                val result = KInstancio.ofSet(elementModel).size(1).create()

                assertThat(result).containsOnly("elem")
            }
        }
    }
}
