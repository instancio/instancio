/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.values;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.generator.ValueSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
public abstract class AbstractValueSpecTestTemplate<T> {

    /**
     * Returns the spec under test.
     */
    protected abstract ValueSpec<T> spec();

    protected void assertDefaultSpecValue(T actual) {
        // other asserts, if needed,
        // in addition to the not null check done by this template
    }

    @Test
    protected final void get() {
        final T actual = spec().get();
        assertThat(actual).isNotNull();
        assertDefaultSpecValue(actual);
    }

    @Test
    protected final void list() {
        final int size = 10;
        final List<T> results = spec().list(size);
        assertThat(results)
                .hasSize(size)
                .allSatisfy(this::assertDefaultSpecValue);
    }

    @Test
    protected final void map() {
        final String result = spec().map(Object::toString);
        assertThat(result).isNotBlank();
    }

    @Test
    protected final void nullable() {
        for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {
            final T result = spec().nullable().get();
            if (result == null) {
                return;
            }
        }

        Assertions.fail("%s.nullable() did not generate null after %s attempts",
                spec().getClass().getSimpleName(), Constants.SAMPLE_SIZE_DDD);
    }

    @Test
    protected final void stream() {
        final int size = 10;
        final Stream<T> results = spec().stream().limit(size);
        assertThat(results)
                .hasSize(size)
                .allSatisfy(this::assertDefaultSpecValue);
    }

    @Test
    protected void toModel() {
        final Model<T> model = spec().toModel();
        final T result = Instancio.create(model);
        assertDefaultSpecValue(result);
    }
}
