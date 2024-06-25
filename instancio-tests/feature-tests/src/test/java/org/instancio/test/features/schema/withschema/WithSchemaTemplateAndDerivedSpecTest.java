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
package org.instancio.test.features.schema.withschema;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.junit.InstancioExtension;
import org.instancio.schema.CombinatorMethod;
import org.instancio.schema.CombinatorProvider;
import org.instancio.schema.DataSpec;
import org.instancio.schema.DerivedSpec;
import org.instancio.schema.PostProcessor;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.TemplateSpec;
import org.instancio.schema.WithPostProcessor;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataEndStrategy;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.SCHEMA, Feature.WITH_SCHEMA})
@ExtendWith(InstancioExtension.class)
class WithSchemaTemplateAndDerivedSpecTest {

    @SuppressWarnings("unused")
    @SchemaResource(data = "a,b\na1,b1\na2,b2\na3,b3\na4,b4")
    private interface StringFieldsSchema extends Schema {

        @DataSpec(propertyName = "a")
        @WithPostProcessor(UnderscoreAppender.class)
        SchemaSpec<String> one();

        @DataSpec(propertyName = "b")
        @WithPostProcessor(UnderscoreAppender.class)
        SchemaSpec<String> two();

        @WithPostProcessor(UnderscoreAppender.class)
        @TemplateSpec("three_${one}")
        SchemaSpec<String> three();

        @WithPostProcessor(UnderscoreAppender.class)
        @DerivedSpec(fromSpec = {"one", "two"}, by = Combinators.class)
        SchemaSpec<String> four();

        class Combinators implements CombinatorProvider {
            @CombinatorMethod
            String hyphenate(final String one, final String two) {
                return one + "-" + two;
            }
        }

        class UnderscoreAppender implements PostProcessor<String> {
            @Override
            public String process(final String input, final Random random) {
                return "_" + input;
            }
        }
    }

    @Test
    void shouldPopulateObjectsUsingDeclaredSchemaSpecMethods() {
        final Schema schema = Instancio.ofSchema(StringFieldsSchema.class)
                .withSetting(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE)
                .create();

        final List<StringFields> results = Instancio.ofList(StringFields.class)
                .size(4)
                .withSchema(all(StringFields.class), schema)
                .create();

        assertThat(results)
                .extracting(StringFields::getOne)
                .containsExactly("_a1", "_a2", "_a3", "_a4");

        assertThat(results)
                .extracting(StringFields::getTwo)
                .containsExactly("_b1", "_b2", "_b3", "_b4");

        assertThat(results)
                .extracting(StringFields::getThree)
                .containsExactly("_three__a1", "_three__a2", "_three__a3", "_three__a4");

        assertThat(results)
                .extracting(StringFields::getFour)
                .containsExactly("__a1-_b1", "__a2-_b2", "__a3-_b3", "__a4-_b4");

    }
}
