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
package org.instancio.test.features.setbackreference;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.cyclic.onetomany.DetailPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@FeatureTag(Feature.SET_BACK_REFERENCES)
@ExtendWith(InstancioExtension.class)
class SetBackReferenceWithSelectorTest {

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.SET_BACK_REFERENCES, true);

    @Test
    void overrideBackReferenceWithCustomSelector() {
        final MainPojo expected = new MainPojo();

        final MainPojo result = Instancio.of(MainPojo.class)
                .set(all(MainPojo.class).within(scope(List.class)), expected)
                .create();

        assertThat(result.getDetailPojos())
                .isNotEmpty()
                .extracting(DetailPojo::getMainPojo)
                .allSatisfy(mainPojo -> assertThat(mainPojo).isSameAs(expected));
    }

    @Test
    void withNullableBackReference() {
        final MainPojo result = Instancio.of(MainPojo.class)
                .generate(field(MainPojo::getDetailPojos), gen -> gen.collection().size(500))
                .withNullable(all(MainPojo.class).within(scope(List.class)))
                .create();

        assertThat(result.getDetailPojos())
                .as("Some references are set to null")
                .extracting(DetailPojo::getMainPojo)
                .containsNull();

        assertThat(result.getDetailPojos())
                .as("Non-null references point to the root object")
                .extracting(DetailPojo::getMainPojo)
                .filteredOn(Objects::nonNull)
                .isNotEmpty()
                .allSatisfy(mainPojo -> assertThat(mainPojo).isSameAs(result));
    }

    @Test
    void withIgnoredBackReference() {
        final MainPojo result = Instancio.of(MainPojo.class)
                .ignore(all(MainPojo.class).within(scope(List.class)))
                .create();

        assertThat(result.getDetailPojos())
                .as("All references are null")
                .extracting(DetailPojo::getMainPojo)
                .containsOnlyNulls();
    }

    @Nested
    class WithAssignTest {
        //@formatter:off
        @Data class B { A d; Integer id; }
        @Data class A { B b; Integer id; }
        @Data class Root { A a; }
        //@formatter:on

        @Test
        void withAssign() {
            final Root result = Instancio.of(Root.class)
                    .withSettings(SETTINGS)
                    .assign(Assign.valueOf(B::getId).to(A::getId))
                    .create();

            assertThat(result.a.id).isEqualTo(result.a.b.id);
            assertThat(result.a).isSameAs(result.a.b.d);
        }
    }
}
