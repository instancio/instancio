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
package org.instancio.test.features.instantiation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.conditions.Conditions.RANDOM_INTEGER;
import static org.instancio.test.support.conditions.Conditions.RANDOM_STRING;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.CTOR_PREFIX;

// NOTE: classes are intentionally not records to verify handling of POJOs.
@SuppressWarnings({"ClassCanBeRecord", "unused"})
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class ConstructorParameterTypesTest {

    private enum Color {RED, GREEN, BLUE}

    private static final class WithPrimitiveParameters {
        private final String canary;
        private final int intValue;
        private final long longValue;
        private final boolean booleanValue;

        private WithPrimitiveParameters(
                final String canary,
                final int intValue,
                final long longValue,
                final boolean booleanValue) {
            this.canary = CTOR_PREFIX + canary;
            this.intValue = intValue;
            this.longValue = longValue;
            this.booleanValue = booleanValue;
        }
    }

    private static final class WithWrapperParameters {
        private final String canary;
        private final Integer intValue;
        private final Long longValue;

        private WithWrapperParameters(
                final String canary,
                final Integer intValue,
                final Long longValue) {
            this.canary = CTOR_PREFIX + canary;
            this.intValue = intValue;
            this.longValue = longValue;
        }
    }

    private static final class Inner {
        private final String value;

        private Inner(final String value) {
            this.value = CTOR_PREFIX + value;
        }
    }

    private static final class WithNestedPojoParameter {
        private final String canary;
        private final Inner inner;

        private WithNestedPojoParameter(final String canary, final Inner inner) {
            this.canary = CTOR_PREFIX + canary;
            this.inner = inner;
        }
    }

    private static final class WithCollectionParameter {
        private final String canary;
        private final List<String> items;

        private WithCollectionParameter(final String canary, final List<String> items) {
            this.canary = CTOR_PREFIX + canary;
            this.items = items;
        }
    }

    private static final class WithMapParameter {
        private final String canary;
        private final Map<String, Integer> data;

        private WithMapParameter(final String canary, final Map<String, Integer> data) {
            this.canary = CTOR_PREFIX + canary;
            this.data = data;
        }
    }

    private static final class WithArrayParameter {
        private final String canary;
        private final String[] tags;

        private WithArrayParameter(final String canary, final String[] tags) {
            this.canary = CTOR_PREFIX + canary;
            this.tags = tags;
        }
    }

    private static final class WithEnumParameter {
        private final String canary;
        private final Color color;

        private WithEnumParameter(final String canary, final Color color) {
            this.canary = CTOR_PREFIX + canary;
            this.color = color;
        }
    }

    @Test
    void primitiveParameters() {
        final WithPrimitiveParameters result = Instancio.create(WithPrimitiveParameters.class);

        assertThat(result.canary).startsWith(CTOR_PREFIX);
        assertThat(result.intValue).is(RANDOM_INTEGER);
        assertThat(result.longValue).isPositive();
    }

    @Test
    void wrapperParameters() {
        final WithWrapperParameters result = Instancio.create(WithWrapperParameters.class);

        assertThat(result.canary).startsWith(CTOR_PREFIX);
        assertThat(result.intValue).is(RANDOM_INTEGER);
        assertThat(result.longValue).isNotNull();
    }

    @Test
    void nestedPojoParameter() {
        final WithNestedPojoParameter result = Instancio.create(WithNestedPojoParameter.class);

        assertThat(result.canary).startsWith(CTOR_PREFIX);
        assertThat(result.inner).isNotNull();
        assertThat(result.inner.value).startsWith(CTOR_PREFIX);
    }

    @Test
    void collectionParameter() {
        final WithCollectionParameter result = Instancio.create(WithCollectionParameter.class);

        assertThat(result.canary).startsWith(CTOR_PREFIX);
        assertThat(result.items).isNotEmpty().are(RANDOM_STRING);
    }

    @Test
    void mapParameter() {
        final WithMapParameter result = Instancio.create(WithMapParameter.class);

        assertThat(result.canary).startsWith(CTOR_PREFIX);
        assertThat(result.data.values()).isNotEmpty().are(RANDOM_INTEGER);
    }

    @Test
    void arrayParameter() {
        final WithArrayParameter result = Instancio.create(WithArrayParameter.class);

        assertThat(result.canary).startsWith(CTOR_PREFIX);
        assertThat(result.tags).isNotEmpty().are(RANDOM_STRING);
    }

    @Test
    void enumParameter() {
        final WithEnumParameter result = Instancio.create(WithEnumParameter.class);

        assertThat(result.canary).startsWith(CTOR_PREFIX);
        assertThat(result.color).isIn((Object[]) Color.values());
    }
}
