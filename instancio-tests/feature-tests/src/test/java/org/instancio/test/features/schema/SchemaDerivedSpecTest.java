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
package org.instancio.test.features.schema;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.schema.CombinatorMethod;
import org.instancio.schema.CombinatorProvider;
import org.instancio.schema.DataSpec;
import org.instancio.schema.DerivedSpec;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.TemplateSpec;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataAccess;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaDerivedSpecTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

    /**
     * This schema uses individual combinator providers.
     */
    @SchemaResource(data = "x,y\n1,2")
    private interface BaseSchema extends Schema {
        String X = "x";
        String Y = "y";

        // Although x() and y() are not used, they must still be declared
        // so that combinators can resolve the property type
        @DataSpec(propertyName = X)
        SchemaSpec<Integer> x();

        @DataSpec(propertyName = Y)
        SchemaSpec<Integer> y();

        @TemplateSpec("${x} + ${y}")
        SchemaSpec<String> sumFormula();

        @DerivedSpec(fromSpec = {X, Y}, by = SumCombinator.class)
        SchemaSpec<Integer> sum();

        @DerivedSpec(fromSpec = {X, Y}, by = SumCombinatorWithRandom.class)
        SchemaSpec<Integer> sumWithRandom();

        @DerivedSpec(fromSpec = {"sumFormula", X, Y}, by = SumFormulaCombinator.class)
        SchemaSpec<String> sumFormulaWithResult();
    }

    /**
     * This schema uses a combinator provider {@link AllCombinators}
     * which contains multiple {@link CombinatorMethod} methods.
     */
    @SchemaResource(data = "x,y\n1,2")
    private interface SchemaWithSingleCombinatorProvider extends BaseSchema {

        @Override
        @DerivedSpec(fromSpec = {BaseSchema.X, BaseSchema.Y},
                by = AllCombinators.class, method = "addIntegers")
        SchemaSpec<Integer> sum();

        @Override
        @DerivedSpec(fromSpec = {BaseSchema.X, BaseSchema.Y},
                by = AllCombinators.class, method = "addWithRandom")
        SchemaSpec<Integer> sumWithRandom();

        @Override
        @DerivedSpec(fromSpec = {"sumFormula", BaseSchema.X, BaseSchema.Y},
                by = AllCombinators.class, method = "formulaWithResult")
        SchemaSpec<String> sumFormulaWithResult();
    }

    private static class AllCombinators implements CombinatorProvider {
        @CombinatorMethod
        Integer addIntegers(final Integer x, final Integer y) {
            return new SumCombinator().addIntegers(x, y);
        }

        @CombinatorMethod
        String formulaWithResult(final String formula, final Integer x, final Integer y) {
            return new SumFormulaCombinator().formulaWithResult(formula, x, y);
        }

        // static methods are supported
        @CombinatorMethod
        static Integer addWithRandom(final Integer x, final Integer y, final Random random) {
            return new SumCombinatorWithRandom().addWithRandom(x, y, random);
        }
    }

    private static class SumCombinator implements CombinatorProvider {
        @CombinatorMethod
        Integer addIntegers(final Integer x, final Integer y) {
            return x + y;
        }
    }

    private static class SumFormulaCombinator implements CombinatorProvider {
        @CombinatorMethod
        String formulaWithResult(final String formula, final Integer x, final Integer y) {
            final int result = x + y;
            return String.format("%s = %d", formula, result);
        }
    }

    private static class SumCombinatorWithRandom implements CombinatorProvider {
        @CombinatorMethod
        Integer addWithRandom(final Integer x, final Integer y, final Random random) {
            final int z = random.oneOf(10, 100);
            return x + y + z;
        }
    }

    @ValueSource(classes = {BaseSchema.class, SchemaWithSingleCombinatorProvider.class})
    @ParameterizedTest
    void sumCombinator(final Class<? extends BaseSchema> klass) {
        final BaseSchema spec = Instancio.createSchema(klass);

        assertThat(spec.sum().get()).isEqualTo(3);
    }

    @ValueSource(classes = {BaseSchema.class, SchemaWithSingleCombinatorProvider.class})
    @ParameterizedTest
    void formulaWithResultCombinator(final Class<? extends BaseSchema> klass) {
        final BaseSchema result = Instancio.createSchema(klass);

        assertThat(result.sumFormulaWithResult().get()).isEqualTo("1 + 2 = 3");
    }

    @ValueSource(classes = {BaseSchema.class, SchemaWithSingleCombinatorProvider.class})
    @ParameterizedTest
    void combinatorWithRandom(final Class<? extends BaseSchema> klass) {
        final BaseSchema schema = Instancio.createSchema(klass);

        final Set<Integer> results = Stream.generate(() -> schema.sumWithRandom().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly(13, 103);
    }
}
