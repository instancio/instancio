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
package org.instancio.test.features.model;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Changes made to the API after calling {@code toModel()}
 * should not modify the model.
 */
@FeatureTag(Feature.MODEL)
@ExtendWith(InstancioExtension.class)
class ModelNotModifiedAfterToModelTest {

    @Test
    void setWithSameSelector() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .set(field(StringsAbc::getA), "foo");

        final Model<StringsAbc> model = api.toModel();

        api.set(field(StringsAbc::getA), "bar");

        assertThat(api.create().getA()).isEqualTo("bar");
        assertThat(Instancio.create(model).getA()).isEqualTo("foo");
    }

    @Test
    void setWithNewSelector() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .set(field(StringsAbc::getA), "foo");

        final Model<StringsAbc> model = api.toModel();

        api.set(field(StringsAbc::getB), "bar");

        assertThat(api.create().getB()).isEqualTo("bar");
        assertThat(Instancio.create(model).getB()).isNotEqualTo("bar");
    }

    @Test
    void ignore() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .ignore(field(StringsAbc::getA));

        final Model<StringsAbc> model = api.toModel();

        api.ignore(field(StringsAbc::getB));

        assertThat(api.create().getB()).isNull();
        assertThat(Instancio.create(model).getB()).isNotNull();
    }

    @Test
    void assignWithSameDestination() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(field(StringsAbc::getA)).set("foo"));

        final Model<StringsAbc> model = api.toModel();

        api.assign(Assign.valueOf(field(StringsAbc::getA)).set("bar"));

        assertThat(api.create().getA()).isEqualTo("bar");
        assertThat(Instancio.create(model).getA()).isEqualTo("foo");
    }

    @Test
    void generate() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .generate(field(StringsAbc::getA), gen -> gen.oneOf("foo"));

        final Model<StringsAbc> model = api.toModel();

        api.generate(field(StringsAbc::getB), gen -> gen.oneOf("bar"));

        assertThat(api.create().getB()).isEqualTo("bar");
        assertThat(Instancio.create(model).getB()).isNotEqualTo("bar");
    }

    @Test
    void onComplete() {
        final AtomicInteger count = new AtomicInteger();
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .onComplete(field(StringsAbc::getA), (String a) -> {});

        final Model<StringsAbc> model = api.toModel();

        api.onComplete(field(StringsAbc::getB), (String b) -> count.incrementAndGet());

        Instancio.create(model);

        assertThat(count).hasValue(0);
    }

    @Test
    void withNullable() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .withNullable(field(StringsAbc::getA));

        final Model<StringsAbc> model = api.toModel();

        api.withNullable(field(StringsAbc::getB));

        assertThat(Instancio.ofList(model).size(500).create())
                .extracting(StringsAbc::getB)
                .doesNotContainNull();
    }

    @Test
    void size() {
        final InstancioApi<ListString> api = Instancio.of(ListString.class)
                .size(field(ListString::getList), 1);

        final Model<ListString> model = api.toModel();

        api.size(field(ListString::getList), 5);

        assertThat(api.create().getList()).hasSize(5);
        assertThat(Instancio.create(model).getList()).hasSize(1);
    }

    @Test
    void subtype() {
        final InstancioApi<ListString> api = Instancio.of(ListString.class)
                .subtype(field(ListString::getList), ArrayList.class);

        final Model<ListString> model = api.toModel();

        api.subtype(field(ListString::getList), LinkedList.class);

        assertThat(api.create().getList()).isExactlyInstanceOf(LinkedList.class);
        assertThat(Instancio.create(model).getList()).isExactlyInstanceOf(ArrayList.class);
    }

    @Test
    void setModel() {
        final Model<StringsDef> fooModel = Instancio.of(StringsDef.class)
                .set(field(StringsDef::getD), "foo")
                .toModel();

        final Model<StringsDef> barModel = Instancio.of(StringsDef.class)
                .set(field(StringsDef::getD), "bar")
                .toModel();

        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .setModel(field(StringsAbc::getDef), fooModel);

        final Model<StringsAbc> model = api.toModel();

        api.setModel(field(StringsAbc::getDef), barModel);

        assertThat(api.create().getDef().getD()).isEqualTo("bar");
        assertThat(Instancio.create(model).getDef().getD()).isEqualTo("foo");
    }
}
