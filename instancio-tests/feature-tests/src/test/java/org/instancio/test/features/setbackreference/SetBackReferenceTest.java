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

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.cyclic.ClassesABCWithCrossReferences;
import org.instancio.test.support.pojo.cyclic.IndirectCircularRef;
import org.instancio.test.support.pojo.cyclic.onetomany.DetailPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojoContainer;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.SET_BACK_REFERENCES)
@ExtendWith(InstancioExtension.class)
class SetBackReferenceTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SET_BACK_REFERENCES, true);

    @Test
    void mainPojo() {
        final MainPojo result = Instancio.create(MainPojo.class);

        assertThat(result.getDetailPojos()).isNotEmpty().allSatisfy(detail ->
                assertThat(detail.getMainPojo()).isSameAs(result));
    }

    /**
     * This case produces the following result:
     *
     * <pre>{@code
     *   DetailPojo -> MainPojo -> List<DetailPojo>
     * }</pre>
     *
     * <p>where all elements of {@code List<DetailPojo>} are
     * the <b>same instance</b> of {@code DetailPojo}.
     */
    @Test
    void detailPojo() {
        final int size = 3;
        final DetailPojo result = Instancio.of(DetailPojo.class)
                .generate(field(MainPojo::getDetailPojos), gen -> gen.collection().size(size))
                .create();

        assertThat(result.getMainPojo().getDetailPojos())
                .hasSize(size)
                .allSatisfy(detail -> assertThat(detail).isSameAs(result));
    }

    @Test
    void mainPojoList() {
        final List<MainPojo> results = Instancio.ofList(MainPojo.class).create();

        assertThat(results).isNotEmpty().allSatisfy(result ->
                assertThat(result.getDetailPojos()).isNotEmpty().allSatisfy(detail ->
                        assertThat(detail.getMainPojo()).isSameAs(result)));
    }

    @Test
    void mainPojoContainer() {
        final MainPojoContainer result = Instancio.create(MainPojoContainer.class);

        assertThat(result.getMainPojo1().getDetailPojos()).isNotEmpty().allSatisfy(detail ->
                assertThat(detail.getMainPojo()).isSameAs(result.getMainPojo1()));

        assertThat(result.getMainPojo2().getDetailPojos()).isNotEmpty().allSatisfy(detail ->
                assertThat(detail.getMainPojo()).isSameAs(result.getMainPojo2()));
    }

    @Test
    void mainPojoContainerList() {
        final List<MainPojoContainer> results = Instancio.ofList(MainPojoContainer.class).create();

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            assertThat(result.getMainPojo1().getDetailPojos()).isNotEmpty().allSatisfy(detail ->
                    assertThat(detail.getMainPojo()).isSameAs(result.getMainPojo1()));

            assertThat(result.getMainPojo2().getDetailPojos()).isNotEmpty().allSatisfy(detail ->
                    assertThat(detail.getMainPojo()).isSameAs(result.getMainPojo2()));
        });
    }

    @Test
    void indirectCircularRef() {
        final IndirectCircularRef result = Instancio.create(IndirectCircularRef.class);

        assertThat(result.getStartA()).isNotNull()
                // A -> B
                .extracting(IndirectCircularRef.A::getB)
                .satisfies(b -> assertThat(b).isNotNull())
                // B -> C
                .extracting(IndirectCircularRef.B::getC)
                .satisfies(c -> assertThat(c).isNotNull())
                // C -> endA
                .extracting(IndirectCircularRef.C::getEndA)
                .satisfies(endA -> assertThat(endA)
                        .as("Back reference to A")
                        .isSameAs(result.getStartA()));
    }

    @Test
    void classesABCWithCrossReferences() {
        final ClassesABCWithCrossReferences result = Instancio.create(ClassesABCWithCrossReferences.class);

        assertThat(result.getObjectA().getObjectA()).isSameAs(result.getObjectA());
        assertThat(result.getObjectA().getObjectB().getObjectA()).isSameAs(result.getObjectA());
        assertThat(result.getObjectA().getObjectC().getObjectA()).isSameAs(result.getObjectA());

        assertThat(result.getObjectB().getObjectB()).isSameAs(result.getObjectB());
        assertThat(result.getObjectB().getObjectA().getObjectB()).isSameAs(result.getObjectB());
        assertThat(result.getObjectB().getObjectC().getObjectB()).isSameAs(result.getObjectB());

        assertThat(result.getObjectC().getObjectC()).isSameAs(result.getObjectC());
        assertThat(result.getObjectC().getObjectA().getObjectC()).isSameAs(result.getObjectC());
        assertThat(result.getObjectC().getObjectB().getObjectC()).isSameAs(result.getObjectC());
    }

    /**
     * Should be able to populate nested lists when back-reference setting is enabled.
     */
    @Test
    void nestedLists() {
        final List<List<List<String>>> outer = Instancio.create(new TypeToken<List<List<List<String>>>>() {});

        assertThat(outer).isNotEmpty().allSatisfy(mid -> {
            assertThat(mid).isNotEmpty().allSatisfy(inner -> {
                assertThat(inner).isNotEmpty().doesNotContainNull();
            });
        });
    }
}
