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

    /**
     * Also covers the local variable slot arithmetic: {@code long} and
     * {@code double} occupy two slots each, so a miscalculation would shift
     * the names of the parameters that follow them.
     */
    private static final class WithPrimitiveParameters {
        private final String stringValue;
        private final int intValue;
        private final long longValue;
        private final double doubleValue;
        private final boolean booleanValue;

        private WithPrimitiveParameters(
                final String stringValue,
                final int intValue,
                final long longValue,
                final double doubleValue,
                final boolean booleanValue) {
            this.stringValue = CTOR_PREFIX + stringValue;
            this.intValue = intValue;
            this.longValue = longValue;
            this.doubleValue = doubleValue;
            this.booleanValue = booleanValue;
        }
    }

    private static final class WithWrapperParameters {
        private final String stringValue;
        private final Integer intValue;
        private final Long longValue;

        private WithWrapperParameters(
                final String stringValue,
                final Integer intValue,
                final Long longValue) {
            this.stringValue = CTOR_PREFIX + stringValue;
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
        private final String stringValue;
        private final Inner inner;

        private WithNestedPojoParameter(final String stringValue, final Inner inner) {
            this.stringValue = CTOR_PREFIX + stringValue;
            this.inner = inner;
        }
    }

    private static final class WithCollectionParameter {
        private final String stringValue;
        private final List<String> items;

        private WithCollectionParameter(final String stringValue, final List<String> items) {
            this.stringValue = CTOR_PREFIX + stringValue;
            this.items = items;
        }
    }

    private static final class WithMapParameter {
        private final String stringValue;
        private final Map<String, Integer> data;

        private WithMapParameter(final String stringValue, final Map<String, Integer> data) {
            this.stringValue = CTOR_PREFIX + stringValue;
            this.data = data;
        }
    }

    /**
     * {@code long[]} is a reference and occupies a single local variable slot,
     * unlike the {@code long} it holds.
     */
    private static final class WithArrayParameter {
        private final String stringValue;
        private final String[] tags;
        private final long[] values;

        private WithArrayParameter(final String stringValue, final String[] tags, final long[] values) {
            this.stringValue = CTOR_PREFIX + stringValue;
            this.tags = tags;
            this.values = values;
        }
    }

    private static final class WithEnumParameter {
        private final String stringValue;
        private final Color color;

        private WithEnumParameter(final String stringValue, final Color color) {
            this.stringValue = CTOR_PREFIX + stringValue;
            this.color = color;
        }
    }

    @Test
    void primitiveParameters() {
        final WithPrimitiveParameters result = Instancio.create(WithPrimitiveParameters.class);

        assertThat(result.stringValue).startsWith(CTOR_PREFIX);
        assertThat(result.intValue).is(RANDOM_INTEGER);
        assertThat(result.longValue).isPositive();
        assertThat(result.doubleValue).isPositive();
    }

    @Test
    void wrapperParameters() {
        final WithWrapperParameters result = Instancio.create(WithWrapperParameters.class);

        assertThat(result.stringValue).startsWith(CTOR_PREFIX);
        assertThat(result.intValue).is(RANDOM_INTEGER);
        assertThat(result.longValue).isNotNull();
    }

    @Test
    void nestedPojoParameter() {
        final WithNestedPojoParameter result = Instancio.create(WithNestedPojoParameter.class);

        assertThat(result.stringValue).startsWith(CTOR_PREFIX);
        assertThat(result.inner).isNotNull();
        assertThat(result.inner.value).startsWith(CTOR_PREFIX);
    }

    @Test
    void collectionParameter() {
        final WithCollectionParameter result = Instancio.create(WithCollectionParameter.class);

        assertThat(result.stringValue).startsWith(CTOR_PREFIX);
        assertThat(result.items).isNotEmpty().are(RANDOM_STRING);
    }

    @Test
    void mapParameter() {
        final WithMapParameter result = Instancio.create(WithMapParameter.class);

        assertThat(result.stringValue).startsWith(CTOR_PREFIX);
        assertThat(result.data.values()).isNotEmpty().are(RANDOM_INTEGER);
    }

    @Test
    void arrayParameter() {
        final WithArrayParameter result = Instancio.create(WithArrayParameter.class);

        assertThat(result.stringValue).startsWith(CTOR_PREFIX);
        assertThat(result.tags).isNotEmpty().are(RANDOM_STRING);
        assertThat(result.values).isNotEmpty();
    }

    @Test
    void enumParameter() {
        final WithEnumParameter result = Instancio.create(WithEnumParameter.class);

        assertThat(result.stringValue).startsWith(CTOR_PREFIX);
        assertThat(result.color).isIn((Object[]) Color.values());
    }
}
