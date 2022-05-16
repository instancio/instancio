/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.ON_COMPLETE, Feature.STREAM})
class OnCompleteStreamTest {

    private final AtomicInteger callbacksCount = new AtomicInteger();

    @Test
    void setThenOnComplete() {
        final List<StringHolder> result = Instancio.of(StringHolder.class)
                .set(allStrings(), "foo")
                .onComplete(allStrings(), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo("foo");
                })
                .stream()
                .limit(100)
                .collect(toList());

        assertThat(result).hasSize(100)
                .extracting(StringHolder::getValue)
                .containsOnly("foo");

        assertThat(callbacksCount.get()).isEqualTo(100);
    }

}
