/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.types;

@FeatureTag({Feature.MODEL, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class ModelWithOnCompleteCallbackTest {

    @Test
    void callback() {
        final AtomicInteger count = new AtomicInteger();
        final Model<Person> model = Instancio.of(Person.class)
                .onComplete(all(Phone.class), phone -> count.incrementAndGet())
                .toModel();

        final Person person = Instancio.create(model);
        assertThat(count.get()).isNotZero().isEqualTo(person.getAddress().getPhoneNumbers().size());
    }

    @Test
    void callbackWithPredicateSelector() {
        final AtomicInteger count = new AtomicInteger();
        final Model<Person> model = Instancio.of(Person.class)
                .onComplete(types().of(Phone.class), phone -> count.incrementAndGet())
                .toModel();

        final Person person = Instancio.create(model);
        assertThat(count.get()).isNotZero().isEqualTo(person.getAddress().getPhoneNumbers().size());
    }
}