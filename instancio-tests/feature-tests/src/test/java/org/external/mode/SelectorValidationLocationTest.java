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
package org.external.mode;

import org.instancio.FieldSelectorBuilder;
import org.instancio.Select;
import org.instancio.TypeSelectorBuilder;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.VALIDATION})
class SelectorValidationLocationTest {

    @Test
    void fieldsSelectorFullErrorMessage() {
        final FieldSelectorBuilder builder = Select.fields()
                .ofType(Foo.class)
                .declaredIn(Bar.class);

        assertThatThrownBy(() -> builder.annotated(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .satisfies(ex -> assertThat(ex.getMessage()).containsSubsequence(
                        "field's declared annotation must not be null.",
                        "  method invocation: fields().ofType(Foo).declaredIn(Bar).annotated( -> null <- )",
                        "  at org.external.mode.SelectorValidationLocationTest.",
                        "SelectorValidationLocationTest.java:41"));
    }

    @Test
    void typesSelectorFullErrorMessage() {
        final TypeSelectorBuilder builder = Select.types()
                .of(Foo.class)
                .annotated(Pojo.class);

        assertThatThrownBy(() -> builder.excluding(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .satisfies(ex -> assertThat(ex.getMessage()).containsSubsequence(
                        "excluded type must not be null.",
                        "  method invocation: types().of(Foo).annotated(Pojo).excluding( -> null <- )",
                        "  at org.external.mode.SelectorValidationLocationTest.",
                        "SelectorValidationLocationTest.java:56"));
    }
}
