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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.cyclic.onetomany.DetailPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojoContainer;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;

@FeatureTag({Feature.ASSIGN, Feature.ROOT_SELECTOR})
@ExtendWith(InstancioExtension.class)
class AssignBackReferenceTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AssignBackReferenceToMainPojoTest {

        private final List<TargetSelector> rootSelector = Arrays.asList(root(), all(MainPojo.class));

        @FieldSource("rootSelector")
        @ParameterizedTest
        void withRoot(final TargetSelector rootObjectSelector) {
            final MainPojo result = Instancio.of(MainPojo.class)
                    .assign(Assign.valueOf(rootObjectSelector).to(DetailPojo::getMainPojo))
                    .assign(Assign.valueOf(MainPojo::getId).to(DetailPojo::getMainPojoId))
                    .create();

            assertThat(result.getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
                assertThat(detail.getMainPojo()).isSameAs(result);
                assertThat(detail.getMainPojoId()).isEqualTo(result.getId());
            });
        }

        @Test
        void createList() {
            final List<MainPojo> results = Instancio.ofList(MainPojo.class)
                    .assign(Assign.valueOf(all(MainPojo.class)).to(DetailPojo::getMainPojo))
                    .assign(Assign.valueOf(MainPojo::getId).to(DetailPojo::getMainPojoId))
                    .create();

            assertThat(results).isNotEmpty().allSatisfy(result -> {
                assertThat(result.getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
                    assertThat(detail.getMainPojo()).isSameAs(result);
                    assertThat(detail.getMainPojoId()).isEqualTo(result.getId());
                });
            });
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AssignBackReferenceToDetailPojoTest {

        private final List<TargetSelector> rootSelector = Arrays.asList(root(), all(DetailPojo.class));

        @FieldSource("rootSelector")
        @ParameterizedTest
        void withRoot(final TargetSelector selector) {
            final DetailPojo result = Instancio.of(DetailPojo.class)
                    .assign(Assign.valueOf(selector).to(all(DetailPojo.class).within(scope(List.class))))
                    .assign(Assign.valueOf(MainPojo::getId).to(DetailPojo::getMainPojoId))
                    .create();

            assertThat(result.getMainPojo().getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
                assertThat(detail).isSameAs(result);
                assertThat(detail.getMainPojoId()).isSameAs(result.getMainPojoId());
            });
        }

        private List<TargetSelector> rootListElementSelector = Arrays.asList(
                all(DetailPojo.class),
                all(DetailPojo.class).atDepth(1));

        @FieldSource("rootListElementSelector")
        @ParameterizedTest
        void createList(final TargetSelector rootListElementSelector) {
            final List<DetailPojo> results = Instancio.ofList(DetailPojo.class)
                    .assign(Assign.valueOf(rootListElementSelector)
                            .to(all(DetailPojo.class).within(scope(MainPojo.class))))
                    .assign(Assign.valueOf(MainPojo::getId).to(DetailPojo::getMainPojoId))
                    .create();

            assertThat(results).isNotEmpty().allSatisfy(result -> {
                assertThat(result.getMainPojo().getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
                    assertThat(detail).isSameAs(result);
                    assertThat(detail.getMainPojoId()).isSameAs(result.getMainPojoId());
                });
            });
        }
    }

    @Nested
    class AssignBackReferenceWithMainPojoContainerTest {
        private final Selector mainPojo1 = field(MainPojoContainer::getMainPojo1);
        private final Selector mainPojo2 = field(MainPojoContainer::getMainPojo2);
        private final Selector mainPojoId = field(MainPojo::getId);

        @Test
        void create() {
            final MainPojoContainer result = Instancio.of(MainPojoContainer.class)
                    .assign(Assign.valueOf(mainPojo1).to(field(DetailPojo::getMainPojo).within(mainPojo1.toScope())))
                    .assign(Assign.valueOf(mainPojo2).to(field(DetailPojo::getMainPojo).within(mainPojo2.toScope())))

                    .assign(Assign.valueOf(mainPojoId.atDepth(2).within(mainPojo1.toScope()))
                            .to(field(DetailPojo::getMainPojoId).within(mainPojo1.toScope())))

                    .assign(Assign.valueOf(mainPojoId.atDepth(2).within(mainPojo2.toScope()))
                            .to(field(DetailPojo::getMainPojoId).within(mainPojo2.toScope())))
                    .create();

            assertContainer(result);
        }

        @Test
        void createList() {
            final List<MainPojoContainer> results = Instancio.ofList(MainPojoContainer.class)
                    .assign(Assign.valueOf(mainPojo1).to(field(DetailPojo::getMainPojo).within(mainPojo1.toScope())))
                    .assign(Assign.valueOf(mainPojo2).to(field(DetailPojo::getMainPojo).within(mainPojo2.toScope())))

                    .assign(Assign.valueOf(mainPojoId.atDepth(3).within(mainPojo1.toScope()))
                            .to(field(DetailPojo::getMainPojoId).within(mainPojo1.toScope())))

                    .assign(Assign.valueOf(mainPojoId.atDepth(3).within(mainPojo2.toScope()))
                            .to(field(DetailPojo::getMainPojoId).within(mainPojo2.toScope())))
                    .create();

            results.forEach(this::assertContainer);
        }

        private void assertContainer(final MainPojoContainer result) {
            assertThat(result.getMainPojo1().getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
                assertThat(detail.getMainPojo()).isSameAs(result.getMainPojo1());
                assertThat(detail.getMainPojoId()).isSameAs(result.getMainPojo1().getId());
            });

            assertThat(result.getMainPojo2().getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
                assertThat(detail.getMainPojo()).isSameAs(result.getMainPojo2());
                assertThat(detail.getMainPojoId()).isSameAs(result.getMainPojo2().getId());
            });
        }
    }

    @Test
    void optionalContainer() {
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        class Container {
            Optional<Container> optional;
        }

        final Container result = Instancio.of(Container.class)
                .assign(Assign.valueOf(root()).to(all(Container.class).within(scope(Optional.class))))
                .create();

        assertThat(result.optional).get().isSameAs(result);
    }

    @Test
    void arrayContainer() {
        class Container {
            Container[] array;
        }

        final Container result = Instancio.of(Container.class)
                .assign(Assign.valueOf(root()).to(all(Container.class).within(scope(Container[].class))))
                .create();

        assertThat(result.array)
                .isNotEmpty()
                .allSatisfy(element -> assertThat(element).isSameAs(result));
    }

    @Test
    void listContainer() {
        class Container {
            List<Container> list;
        }

        final Container result = Instancio.of(Container.class)
                .assign(Assign.valueOf(root()).to(all(Container.class).within(scope(List.class))))
                .create();

        assertThat(result.list)
                .isNotEmpty()
                .allSatisfy(element -> assertThat(element).isSameAs(result));
    }

    @Test
    void mapContainer() {
        class Container {
            Map<String, Container> map;
        }

        final Container result = Instancio.of(Container.class)
                .assign(Assign.valueOf(root()).to(all(Container.class).within(scope(Map.class))))
                .create();

        assertThat(result.map.values())
                .isNotEmpty()
                .allSatisfy(value -> assertThat(value).isSameAs(result));
    }
}
