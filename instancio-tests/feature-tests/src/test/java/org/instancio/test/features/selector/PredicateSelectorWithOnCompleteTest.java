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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.OnCompleteCallback;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class PredicateSelectorWithOnCompleteTest {

    @Test
    @DisplayName("Callbacks should be invoked on both, regular and predicate selector matches")
    void shouldInvokeOnCompleteOnRegularAndPredicateSelectors() {
        final AtomicInteger callbackCount = new AtomicInteger();
        final OnCompleteCallback<Address> callback = (Address address) -> callbackCount.incrementAndGet();

        Instancio.of(Person.class)
                .onComplete(all(all(Address.class)), callback)
                .onComplete(field("address"), callback)
                .onComplete(fields().ofType(Address.class), callback)
                .onComplete(types().of(Address.class), callback)
                .create();

        assertThat(callbackCount.get()).isEqualTo(4);
    }

}
