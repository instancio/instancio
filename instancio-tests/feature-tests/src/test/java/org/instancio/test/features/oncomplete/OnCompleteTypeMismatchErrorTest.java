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
package org.instancio.test.features.oncomplete;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag({Feature.GENERATOR, Feature.ON_COMPLETE})
class OnCompleteTypeMismatchErrorTest {

    @Test
    void mismatchedClassSelector() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .onComplete(allStrings(), (Integer wrongType) -> {
                });

        assertError(api);
    }

    @Test
    void mismatchedFieldSelector() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .onComplete(field("value"), (Integer wrongType) -> {
                });

        assertError(api);
    }

    private static void assertError(final InstancioApi<StringHolder> api) {
        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining(String.format(
                        "onComplete() callback error.%n%n" +
                                "ClassCastException was thrown by the callback.%n" +
                                "This usually happens because the type declared by the callback%n" +
                                "does not match the actual type of the target object.%n%n" +
                                "Example:%n" +
                                "onComplete(all(Foo.class), (Bar wrongType) -> {%n" +
                                "               ^^^^^^^^^    ^^^^^^^^^^^^^%n" +
                                "})"
                )).hasCauseExactlyInstanceOf(ClassCastException.class);
    }

}
