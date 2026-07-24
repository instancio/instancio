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

import lombok.Getter;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.CTOR_PREFIX;

/**
 * Selectors applied to fields that map to constructor parameters.
 *
 * <p>NOTE: classes are intentionally not records to verify handling of POJOs;
 * some fields are intentionally not final.
 *
 * @see ConstructorAssignTest for {@code assign()} against constructor parameters
 */
@SuppressWarnings({"ClassCanBeRecord", "FieldMayBeFinal"})
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class ConstructorSelectorsTest {

    @Getter
    private static class CtorPerson {
        private final String firstName;
        private String lastName;
        private @Nullable Integer age; // not in constructor

        CtorPerson(final String firstName, final String lastName) {
            this.firstName = firstName;
            this.lastName = CTOR_PREFIX + lastName;
        }
    }

    @Getter
    private static class Holder {
        private final @Nullable String value;

        Holder(@Nullable final String value) {
            this.value = value;
        }
    }

    @Test
    void setConstructorParameter() {
        final CtorPerson result = Instancio.of(CtorPerson.class)
                .set(field(CtorPerson::getFirstName), "foo")
                .set(field(CtorPerson::getLastName), "bar")
                .create();

        assertThat(result.getFirstName()).isEqualTo("foo");
        assertThat(result.getLastName()).isEqualTo(CTOR_PREFIX + "bar");
    }

    @Test
    void ignoredConstructorParameterIsLeftAtDefaultValue() {
        final CtorPerson result = Instancio.of(CtorPerson.class)
                .ignore(field(CtorPerson::getFirstName))
                .create();

        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).startsWith(CTOR_PREFIX);
    }

    @Test
    void nullableConstructorParameter() {
        final Stream<Holder> results = Instancio.of(Holder.class)
                .withNullable(field(Holder::getValue))
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results)
                .extracting("value")
                .anyMatch(Objects::isNull)
                .anyMatch(Objects::nonNull);
    }

    @Test
    void onCompleteConstructorParameter() {
        final AtomicReference<String> callbackValue = new AtomicReference<>();

        final CtorPerson result = Instancio.of(CtorPerson.class)
                .onComplete(field(CtorPerson::getFirstName), callbackValue::set)
                .create();

        assertThat(callbackValue).hasValue(result.getFirstName());
    }

}
