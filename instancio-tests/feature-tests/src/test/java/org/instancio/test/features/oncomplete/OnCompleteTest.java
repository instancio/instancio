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
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.fields;

@FeatureTag({Feature.GENERATOR, Feature.ON_COMPLETE})
class OnCompleteTest {

    private final AtomicInteger callbacksCount = new AtomicInteger();

    @Test
    @DisplayName("One callback for each generated String")
    void onComplete() {
        Instancio.of(new TypeToken<List<String>>() {})
                .generate(all(List.class), gen -> gen.collection().size(100))
                .onComplete(allStrings(), (String s) -> callbacksCount.incrementAndGet())
                .create();

        assertThat(callbacksCount.get()).isEqualTo(100);
    }

    @Test
    void supplyThenOnComplete() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .supply(allStrings(), random -> "foo")
                .onComplete(allStrings(), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo("foo");
                })
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
        assertThat(callbacksCount.get()).isEqualTo(1);
    }

    @Test
    void onCompleteThenSupply() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .onComplete(allStrings(), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo("foo");
                })
                .supply(allStrings(), random -> "foo")
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
        assertThat(callbacksCount.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("Ignore has higher precedence than other methods")
    void supplyAndOnCompleteWithIgnore() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .lenient()
                .ignore(allStrings())
                .supply(allStrings(), random -> "foo")
                .onComplete(allStrings(), (String s) -> callbacksCount.incrementAndGet())
                .create();

        assertThat(result.getValue()).isNull();
        assertThat(callbacksCount.get()).isZero();
    }

    @Test
    void onCompleteWithGenerate() {
        final String expectedValue = "foo";
        Instancio.of(StringFields.class)
                .generate(fields(), gen -> gen.text().pattern(expectedValue))
                .onComplete(fields(), (String s) -> {
                    assertThat(s).isEqualTo(expectedValue);
                    callbacksCount.incrementAndGet();
                })
                .create();

        assertThat(callbacksCount.get())
                .as("StringFields has 4 string fields, 1 callback per field expected")
                .isEqualTo(4);
    }
}
