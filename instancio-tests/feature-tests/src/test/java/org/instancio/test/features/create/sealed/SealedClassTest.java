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
package org.instancio.test.features.create.sealed;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.sealed.FooRecord;
import org.instancio.test.support.pojo.sealed.SealedFoo;
import org.instancio.test.support.pojo.sealed.SealedFooSubclass;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@ExtendWith(InstancioExtension.class)
class SealedClassTest {

    @Test
    void sealedFoo() {
        final SealedFoo result = Instancio.of(SealedFoo.class)
                .set(field(FooRecord.class, "fooValue"), "foo")
                .create();

        assertThat(result.getFooRecord().fooValue()).isEqualTo("foo");
        assertThat(result.getFooRecord().fooId()).isNotNull();
    }

    @Test
    @FeatureTag(Feature.INHERITANCE)
    void sealedFooSubclass() {
        final SealedFooSubclass result = Instancio.create(SealedFooSubclass.class);

        assertThat(result.getFooRecord().fooValue()).isNotBlank();
        assertThat(result.getFooRecord().fooId()).isNotNull();
        assertThat(result.getFooDate()).isNotNull();
    }
}
