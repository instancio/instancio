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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.dynamic.MixedPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.setter;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.AFTER_GENERATE, Feature.ASSIGNMENT_TYPE_METHOD, Feature.UNSUPPORTED})
@ExtendWith(InstancioExtension.class)
class AfterGenerateWithDynamicPojoTest {

    @WithSettings
    private static final Settings DISABLE_OVERWRITES = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .lock();

    private static class DynPojoGenerator implements Generator<MixedPojo> {
        private final AfterGenerate afterGenerate;

        private DynPojoGenerator(final AfterGenerate afterGenerate) {
            this.afterGenerate = afterGenerate;
        }

        @Override
        public MixedPojo generate(final Random random) {
            return new MixedPojo();
        }

        @Override
        public Hints hints() {
            return Hints.afterGenerate(afterGenerate);
        }
    }

    private static Model<MixedPojo> model(final AfterGenerate afterGenerate) {
        return Instancio.of(MixedPojo.class)
                .supply(all(MixedPojo.class), new DynPojoGenerator(afterGenerate))
                .toModel();
    }

    @Test
    void doNotModify() {
        final MixedPojo result = Instancio.create(model(AfterGenerate.DO_NOT_MODIFY));

        assertThat(result.getDynamicField()).isNull();
    }

    @Test
    void applySelectors() {
        final MixedPojo result = Instancio.create(model(AfterGenerate.APPLY_SELECTORS));

        assertThat(result.getDynamicField()).isNull();
    }

    @Test
    void applySelectorsWithSelector() {
        final MixedPojo result = Instancio.of(model(AfterGenerate.APPLY_SELECTORS))
                .set(field(MixedPojo::getRegularField), "foo")
                .set(setter(MixedPojo::setDynamicField), "bar")
                .create();

        assertThat(result.getRegularField()).isEqualTo("foo");
        assertThat(result.getDynamicField()).isEqualTo("bar");
    }

    /**
     * {@link AfterGenerate#POPULATE_NULLS} is not supported
     * for setter nodes without a field. The setter will be invoked.
     */
    @Test
    @FeatureTag(Feature.UNSUPPORTED)
    void populateNulls() {
        final MixedPojo result = Instancio.create(model(AfterGenerate.POPULATE_NULLS));

        assertThat(result.getDynamicField()).isNotBlank();
    }

    /**
     * {@link AfterGenerate#POPULATE_NULLS_AND_DEFAULT_PRIMITIVES} is not supported
     * for setter nodes without a field. The setter will be invoked.
     */
    @Test
    @FeatureTag(Feature.UNSUPPORTED)
    void populateNullsAndDefaultPrimitives() {
        final MixedPojo result = Instancio.create(model(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));

        assertThat(result.getDynamicField()).isNotBlank();
    }

    @Test
    void populateAll() {
        final MixedPojo result = Instancio.create(model(AfterGenerate.POPULATE_ALL));

        assertThat(result.getDynamicField()).isNotBlank();
    }
}
