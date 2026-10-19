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
package org.instancio.test.beanvalidation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

/**
 * Covers the examples in the "Annotations and Settings" section of the user guide.
 *
 * <p>See also: https://github.com/instancio/instancio/issues/1906
 */
@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class SizeWithSettingsBVTest {

    /**
     * This Maven module uses instancio.properties with custom settings.
     * Revert to defaults for this test class.
     */
    @WithSettings
    private static final Settings settings = Settings.defaults()
            .set(Keys.BEAN_VALIDATION_ENABLED, true);

    private interface HasValue {
        List<String> getValue();
    }

    @Data
    private static final class Outer {
        @NotNull
        @Size(min = 1, max = 25)
        private List<Inner> list;
    }

    @Data
    private static final class Inner {
        @NotNull
        @Size(min = 2, max = 24)
        private List<String> list;
    }

    @Data
    private static final class MapArrayStringBounded {
        @NotNull
        @Size(min = 1, max = 25)
        private Map<String, String> map;

        @NotNull
        @Size(min = 1, max = 25)
        private String[] array;

        @NotNull
        @Size(min = 1, max = 25)
        private String string;
    }

    @Data
    private static final class MapArrayStringUnbounded {
        @NotNull
        @Size(min = 1)
        private Map<String, String> map;

        @NotNull
        @Size(min = 1)
        private String[] array;

        @NotNull
        @Size(min = 1)
        private String string;
    }

    @Data
    private static final class SizeMin1Max25 implements HasValue {
        @NotNull
        @Size(min = 1, max = 25)
        private List<String> value;
    }

    @Data
    private static final class SizeMin1 implements HasValue {
        @NotNull
        @Size(min = 1)
        private List<String> value;
    }

    @Data
    private static final class SizeDefault implements HasValue {
        @NotNull
        @Size
        private List<String> value;
    }

    @Data
    private static final class SizeMax4 implements HasValue {
        @NotNull
        @Size(max = 4)
        private List<String> value;
    }

    @Data
    private static final class SizeMin5Max8 implements HasValue {
        @NotNull
        @Size(min = 5, max = 8)
        private List<String> value;
    }

    @Data
    private static final class SizeMin2Max24 implements HasValue {
        @NotNull
        @Size(min = 2, max = 24)
        private List<String> value;
    }

    @Data
    private static final class SizeMin8 implements HasValue {
        @NotNull
        @Size(min = 8)
        private List<String> value;
    }

    @Data
    private static final class SizeMax0 implements HasValue {
        @NotNull
        @Size(max = 0)
        private List<String> value;
    }

    @Data
    private static final class NotEmptyList implements HasValue {
        @NotEmpty
        private List<String> value;
    }

    private static List<String> create(final Class<? extends HasValue> klass, final int minSize, final int maxSize) {
        return Instancio.of(klass)
                .withSetting(Keys.COLLECTION_MIN_SIZE, minSize)
                .withSetting(Keys.COLLECTION_MAX_SIZE, maxSize)
                .create()
                .getValue();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void maxShouldBeLimitedBySetting() {
        assertThat(create(SizeMin1Max25.class, 11, 11)).hasSizeBetween(1, 11);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void annotationRangeShouldTakePrecedenceOverSettingsAboveRange() {
        assertThat(create(SizeMin1Max25.class, 50, 50)).hasSizeBetween(1, 25);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void unboundedMaxShouldBeLimitedBySetting() {
        assertThat(create(SizeMin1.class, 2, 6)).hasSizeBetween(1, 6);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void defaultSizeShouldNotGenerateEmpty() {
        assertThat(create(SizeDefault.class, 2, 6)).hasSizeBetween(1, 6);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void maxFromAnnotation() {
        assertThat(create(SizeMax4.class, 2, 6)).hasSizeBetween(1, 4);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void annotationRangeShouldTakePrecedenceOverSettingsWithinRange() {
        assertThat(create(SizeMin5Max8.class, 11, 11)).hasSizeBetween(5, 8);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void wideRangeShouldLimitMaxToMinPlus50Percent() {
        assertThat(create(SizeMin2Max24.class, 11, 11)).hasSizeBetween(2, 3);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void wideUnboundedRangeShouldLimitMaxToMinPlus50Percent() {
        assertThat(create(SizeMin8.class, 2, 3)).hasSizeBetween(8, 12);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void maxZeroShouldGenerateEmpty() {
        assertThat(create(SizeMax0.class, 2, 6)).isEmpty();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void notEmptyShouldUseSettings() {
        assertThat(create(NotEmptyList.class, 11, 11)).hasSize(11);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void nestedCollectionsShouldUseAnnotationRange() {
        final Outer result = Instancio.of(Outer.class)
                .withSetting(Keys.COLLECTION_MIN_SIZE, 11)
                .withSetting(Keys.COLLECTION_MAX_SIZE, 11)
                .create();

        assertThat(result.getList()).hasSizeBetween(1, 11);
        assertThat(result.getList()).allSatisfy(inner ->
                assertThat(inner.getList()).hasSizeBetween(2, 3));
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void mapArrayStringAnnotationRangeShouldTakePrecedenceOverSettings() {
        final MapArrayStringBounded result = Instancio.of(MapArrayStringBounded.class)
                .withSetting(Keys.MAP_MIN_SIZE, 50)
                .withSetting(Keys.MAP_MAX_SIZE, 50)
                .withSetting(Keys.ARRAY_MIN_SIZE, 50)
                .withSetting(Keys.ARRAY_MAX_SIZE, 50)
                .withSetting(Keys.STRING_MIN_LENGTH, 50)
                .withSetting(Keys.STRING_MAX_LENGTH, 50)
                .create();

        assertThat(result.getMap()).hasSizeBetween(1, 25);
        assertThat(result.getArray()).hasSizeBetween(1, 25);
        assertThat(result.getString()).hasSizeBetween(1, 25);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void mapArrayStringUnboundedMaxShouldBeLimitedBySettings() {
        final MapArrayStringUnbounded result = Instancio.of(MapArrayStringUnbounded.class)
                .withSetting(Keys.MAP_MAX_SIZE, 3)
                .withSetting(Keys.ARRAY_MAX_SIZE, 3)
                .withSetting(Keys.STRING_MAX_LENGTH, 3)
                .create();

        assertThat(result.getMap()).hasSizeBetween(1, 3);
        assertThat(result.getArray()).hasSizeBetween(1, 3);
        assertThat(result.getString()).hasSizeBetween(1, 3);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void generatorSpecShouldTakePrecedenceOverAnnotation() {
        final Outer result = Instancio.of(Outer.class)
                .generate(all(List.class), gen -> gen.collection().size(11))
                .create();

        assertThat(result.getList()).hasSize(11);
        assertThat(result.getList()).allSatisfy(inner ->
                assertThat(inner.getList()).hasSize(11));
    }
}
