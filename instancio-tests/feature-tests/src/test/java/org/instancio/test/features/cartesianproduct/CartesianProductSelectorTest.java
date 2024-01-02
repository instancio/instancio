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
package org.instancio.test.features.cartesianproduct;

import lombok.Data;
import org.instancio.CartesianProductApi;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.dynamic.DynPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;
import static org.instancio.Select.setter;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.CARTESIAN_PRODUCT,
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.SELECT_GROUP})
@ExtendWith(InstancioExtension.class)
class CartesianProductSelectorTest {

    @Test
    void withAllInts() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(allInts(), 1, 2)
                .list();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(1, 2);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(1, 2);
    }

    @Test
    void withSelectorGroup() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(all(
                        field(IntegerHolder::getPrimitive),
                        field(IntegerHolder::getWrapper)), 1, 2)
                .list();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(1, 2);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(1, 2);
    }

    @Test
    void withPredicateSelector() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(types().of(int.class), 1, 2)
                .with(types().of(Integer.class), 3, 4)
                .list();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(1, 1, 2, 2);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(3, 4, 3, 4);
    }

    @Test
    void withPredicateSelectorOverride() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(types().of(int.class), 1, 2)
                // unused because 'field(IntegerHolder::getWrapper)' overrides it
                .with(types().of(Integer.class), 3, 4)
                .set(field(IntegerHolder::getWrapper), 9)
                .lenient()
                .list();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(1, 1, 2, 2);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(9, 9, 9, 9);
    }

    @Test
    @RunWith.MethodAssignmentOnly
    void withSetterSelector() {
        final List<DynPerson> results = Instancio.ofCartesianProduct(DynPerson.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                        .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE))
                .with(setter(DynPerson::setName), "foo", "bar")
                .with(setter(DynPerson::setAge), 30, 40)
                .list();

        assertThat(results).extracting(DynPerson::getName).containsExactly("foo", "foo", "bar", "bar");
        assertThat(results).extracting(DynPerson::getAge).containsExactly(30, 40, 30, 40);
    }

    @Test
    void withRootSelector() {
        final List<DynPerson> results = Instancio.ofCartesianProduct(DynPerson.class)
                .set(root(), null)
                .list();

        assertThat(results).isNull();
    }

    @Test
    void selectorMatchesMultipleNodes() {
        final CartesianProductApi<TwoIntegerHolders> api = Instancio.ofCartesianProduct(TwoIntegerHolders.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .with(field(IntegerHolder::getWrapper), 3, 4);


        assertThatThrownBy(api::list)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("no item is available to emit()");
    }

    @Test
    void withSelectorScope() {

        final List<TwoIntegerHolders> results = Instancio.ofCartesianProduct(TwoIntegerHolders.class)
                .with(field(IntegerHolder::getPrimitive).within(scope(TwoIntegerHolders::getTwo)), -1, -2)
                .with(field(IntegerHolder::getWrapper).within(scope(TwoIntegerHolders::getTwo)), -3, -4)
                .list();

        assertThat(results)
                .extracting(r -> r.getTwo().getPrimitive())
                .containsExactly(-1, -1, -2, -2);

        assertThat(results)
                .extracting(r -> r.getTwo().getWrapper())
                .containsExactly(-3, -4, -3, -4);

        assertThat(results)
                .extracting(TwoIntegerHolders::getOne)
                .allSatisfy(one -> {
                    assertThat(one.getPrimitive()).isPositive();
                    assertThat(one.getWrapper()).isPositive();
                });
    }

    private static @Data class TwoIntegerHolders {
        private IntegerHolder one;
        private IntegerHolder two;
    }
}
