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
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.constructor.StringsAbcCtor;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

/**
 * Assigning an incompatible value to a constructor parameter is a user error
 * and must always be propagated, regardless of whether the target is a record
 * or a class, and regardless of the {@link Keys#ON_CONSTRUCTOR_ERROR} setting.
 *
 * <p>Previously, the type mismatch was reported as an internal error for records
 * (which is suppressed by default, producing a {@code null} object) and was
 * silently swallowed by the constructor fallback for classes.
 *
 * @see <a href="https://github.com/instancio/instancio/issues/1795">issue 1795</a>
 */
@FeatureTag({Feature.INSTANTIATION_STRATEGIES, Feature.VALIDATION})
@ExtendWith(InstancioExtension.class)
class ConstructorTypeMismatchTest {

    private static void assertTypeMismatch(final InstancioApi<?> api, final String expectedTarget) {
        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("error assigning value to: field " + expectedTarget)
                .hasMessageContaining("Type mismatch:")
                .hasMessageContaining("Provided argument type ...: Gender");
    }

    @Test
    void recordComponent() {
        final InstancioApi<StringsAbcRecord> api = Instancio.of(StringsAbcRecord.class)
                .set(field(StringsAbcRecord::a), Gender.FEMALE);

        assertTypeMismatch(api, "StringsAbcRecord.a");
    }

    @Test
    void constructorParameterOfClass() {
        final InstancioApi<StringsAbcCtor> api = Instancio.of(StringsAbcCtor.class)
                .set(field(StringsAbcCtor::getA), Gender.FEMALE);

        assertTypeMismatch(api, "StringsAbcCtor.a");
    }

    /**
     * The mismatch must be reported even though the setting allows
     * falling back to instantiation without a constructor.
     */
    @Test
    void constructorParameterOfClassWithFallbackEnabled() {
        final InstancioApi<StringsAbcCtor> api = Instancio.of(StringsAbcCtor.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FALLBACK)
                .set(field(StringsAbcCtor::getA), Gender.FEMALE);

        assertTypeMismatch(api, "StringsAbcCtor.a");
    }

    @Test
    void regularField() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .set(field(StringHolder::getValue), Gender.FEMALE);

        assertTypeMismatch(api, "StringHolder.value");
    }
}
