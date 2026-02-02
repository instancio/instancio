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
package org.instancio.test.features.fill;

import org.assertj.core.api.Condition;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FillType;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FILL)
@ExtendWith(InstancioExtension.class)
class FillTypeTest {

    private static final Condition<IntegerHolder> POPULATED_NULLS_AND_PRIMITIVES = new Condition<>(
            o -> o.getWrapper() != null && o.getPrimitive() > 0, "fully populated");

    private static final Condition<IntegerHolder> POPULATED_ONLY_NULLS = new Condition<>(
            o -> o.getWrapper() != null && o.getPrimitive() == 0, "only nulls populated");

    private static final Condition<IntegerHolder> NOT_POPULATED = new Condition<>(
            o -> o.getWrapper() == null && o.getPrimitive() == 0, "not populated");

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(FillType.APPLY_SELECTORS, NOT_POPULATED),
                Arguments.of(FillType.POPULATE_NULLS, POPULATED_ONLY_NULLS),
                Arguments.of(FillType.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES, POPULATED_NULLS_AND_PRIMITIVES));
    }

    @MethodSource("args")
    @ParameterizedTest
    void viaSettings(
            final FillType fillType,
            final Condition<IntegerHolder> expectedCondition) {

        final IntegerHolder object = new IntegerHolder();

        Instancio.ofObject(object)
                .withSetting(Keys.FILL_TYPE, fillType)
                .fill();

        assertThat(object).has(expectedCondition);
    }

    @MethodSource("args")
    @ParameterizedTest
    void viaBuilderApi(
            final FillType fillType,
            final Condition<IntegerHolder> expectedCondition) {

        final IntegerHolder object = new IntegerHolder();

        Instancio.ofObject(object)
                .withFillType(fillType)
                .fill();

        assertThat(object).has(expectedCondition);
    }

    @Test
    @DisplayName("By default, should populate nulls and default primitives")
    void defaultBehaviourIsPopulateNullsAndDefaultPrimitives() {
        final IntegerHolder object = new IntegerHolder();

        Instancio.fill(object);

        assertThat(object).has(POPULATED_NULLS_AND_PRIMITIVES);
    }

    @Test
    @DisplayName("Builder API should take precedence over settings")
    void builderApiShouldTakePrecedenceOverSettingKey() {
        final IntegerHolder object = new IntegerHolder();

        Instancio.ofObject(object)
                .withSetting(Keys.FILL_TYPE, FillType.APPLY_SELECTORS)
                .withFillType(FillType.POPULATE_NULLS)
                .fill();

        assertThat(object).has(POPULATED_ONLY_NULLS);
    }
}