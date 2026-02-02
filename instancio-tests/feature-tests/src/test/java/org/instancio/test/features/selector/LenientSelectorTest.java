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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.setter;
import static org.instancio.Select.types;

@FeatureTag(Feature.LENIENT_SELECTOR)
@ExtendWith(InstancioExtension.class)
class LenientSelectorTest {

    @Test
    void typeSelector() {
        final String result = Instancio.of(String.class)
                .set(all(int.class).lenient(), 0)
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void primitiveAndWrapperSelector() {
        final String result = Instancio.of(String.class)
                .set(allInts().lenient(), 0)
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void getMethodReferenceSelector() {
        final String result = Instancio.of(String.class)
                .set(field(Person::getName).lenient(), "foo")
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void predicateSelector() {
        final String result = Instancio.of(String.class)
                .set(fields(f -> f.getName().equals("doesnt-exist")).lenient(), "foo")
                .set(types(t -> t.getName().equals("doesnt-exist")).lenient(), "foo")
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void predicateSelectorBuilder() {
        final String result = Instancio.of(String.class)
                .set(fields().named("doesnt-exist").lenient(), "foo")
                .set(types().of(int.class).lenient(), "foo")
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    @RunWith.MethodAssignmentOnly
    void setterSelector() {
        final String result = Instancio.of(String.class)
                .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(setter(Person::setName).lenient(), "foo")
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void depth() {
        final String result = Instancio.of(String.class)
                .set(field(Person::getName).atDepth(1).lenient(), "foo")
                .set(fields(f -> f.getName().equals("doesnt-exist")).atDepth(d -> d > 9).lenient(), "foo")
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void group() {
        final String result = Instancio.of(String.class)
                .set(all(
                        types().of(int.class).lenient(),
                        field(Person::getName).lenient()), "foo")
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void withinScope() {
        final String result = Instancio.of(String.class)
                .set(field(Person::getName).within(scope(Person.class)).lenient(), "foo")
                .create();

        assertThat(result).isNotBlank();
    }

    @Test
    void withModel() {
        final Model<String> model = Instancio.of(String.class)
                .set(all(int.class).lenient(), 0)
                .toModel();

        final String result = Instancio.create(model);

        assertThat(result).isNotBlank();
    }
}
