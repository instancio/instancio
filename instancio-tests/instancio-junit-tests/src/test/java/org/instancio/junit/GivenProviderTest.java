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
package org.instancio.junit;

import org.instancio.TypeToken;
import org.instancio.support.DefaultRandom;
import org.instancio.support.Seeds;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GivenProviderTest {

    private static final TypeToken<?> LIST_STRING = new TypeToken<List<String>>() {};
    private static final String FIELD_VALUE = "foo";
    private static final String PARAM_VALUE = "bar";
    private static final long SEED = -1;

    @Given(FieldValueProvider.class)
    private List<String> field;

    @Seed(SEED)
    @Test
    void fieldFactory() {
        assertThat(field).containsOnly(FIELD_VALUE);
    }

    private static class FieldValueProvider implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            assertCommonFields(context);
            assertThat(context.getAnnotation(Given.class).value()).containsOnly(getClass());
            assertThat(context.getTargetElement()).isInstanceOf(Field.class);
            assertThat(((Field) context.getTargetElement()).getType()).isEqualTo(List.class);
            return Collections.singletonList(FIELD_VALUE);
        }
    }

    @Seed(SEED)
    @Test
    void paramFactory(@Given(ParameterValueProvider.class) List<String> param) {
        assertThat(param).containsOnly(PARAM_VALUE);
    }

    @Seed(SEED)
    @InstancioSource(samples = 5)
    @ParameterizedTest
    void instancioSource(@Given(ParameterValueProvider.class) List<String> param) {
        assertThat(param).containsOnly(PARAM_VALUE);
    }

    private static class ParameterValueProvider implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            assertCommonFields(context);
            assertThat(context.getAnnotation(Given.class).value()).containsOnly(getClass());
            assertThat(context.getTargetElement()).isInstanceOf(Parameter.class);
            assertThat(((Parameter) context.getTargetElement()).getType()).isEqualTo(List.class);
            return Collections.singletonList(PARAM_VALUE);
        }
    }

    private static void assertCommonFields(final GivenProvider.ElementContext context) {
        assertThat(context.getTargetType()).isEqualTo(LIST_STRING.get());
        assertThat(context.getTargetClass()).isEqualTo(List.class);

        final DefaultRandom random = (DefaultRandom) context.random();
        assertThat(random).isNotNull();
        assertThat(random.getSeed()).isEqualTo(SEED);
        assertThat(random.getSource()).isEqualTo(Seeds.Source.SEED_ANNOTATION);
    }
}
