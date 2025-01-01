/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;

@FeatureTag({
        Feature.ON_COMPLETE,
        Feature.SET,
        Feature.SUPPLY
})
@ExtendWith(InstancioExtension.class)
class OnCompleteWithSupplierTest {

    private final AtomicInteger callbacksCount = new AtomicInteger();

    @Test
    @DisplayName("onComplete should not be called on objects from supply(Supplier) (lenient mode)")
    void supplyAndOnCompleteLenientMode() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .supply(allStrings(), () -> "foo")
                .onComplete(allStrings(), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo("foo");
                })
                .lenient()
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
        assertThat(callbacksCount.get()).isZero();
    }

    @Test
    @DisplayName("onComplete should not be called on objects from supply(Supplier) (strict mode)")
    void supplyAndOnCompleteStrictMode() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .supply(allStrings(), () -> "foo")
                .onComplete(allStrings(), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo("foo");
                });


        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("Unused selector in: onComplete()")
                .hasMessageContaining("all(String)");

        assertThat(callbacksCount.get()).isZero();
    }

    @Test
    @DisplayName("onComplete should not be called on objects from set(Object)")
    void setAndOnCompleteLenientMode() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .set(allStrings(), "foo")
                .onComplete(allStrings(), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo("foo");
                })
                .lenient()
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
        assertThat(callbacksCount.get()).isZero();
    }
}
