/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.schema;

import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataEndStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaTest {

    interface Foo<T> {}

    interface Bar {}

    interface Zaz {}

    @SchemaResource(data = "id\n123")
    private interface SampleSchema extends Foo<Integer>, Bar, Schema, Zaz {
        SchemaSpec<Integer> id();
    }

    @Test
    void verifyToString() {
        final SampleSchema result = Instancio.createSchema(SampleSchema.class);

        assertThat(result).hasToString("Proxy[org.instancio.schema.SchemaTest$SampleSchema]");
    }

    @Test
    void verifyEqualsAndHashcode() {
        final SampleSchema result1 = Instancio.createSchema(SampleSchema.class);
        final SampleSchema result2 = Instancio.createSchema(SampleSchema.class);

        assertThat(result1).isEqualTo(result1)
                .isNotEqualTo(result2)
                .doesNotHaveSameHashCodeAs(result2);
    }

    @Test
    void create() {
        final SampleSchema result = Instancio.ofSchema(SampleSchema.class)
                .withSetting(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE)
                .create();

        assertThat(result.stringSpec("id").get()).isEqualTo("123");

        assertThat(result.id().get()).isEqualTo(123);
    }
}
