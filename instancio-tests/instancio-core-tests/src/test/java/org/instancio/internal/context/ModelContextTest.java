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
package org.instancio.internal.context;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Mode;
import org.instancio.Random;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.Hints;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.generator.misc.SupplierAdapter;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.testsupport.fixtures.Types;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ModelContextTest {
    private static final Field NAME_FIELD = ReflectionUtils.getField(Person.class, "name");
    private static final Field ADDRESS_FIELD = ReflectionUtils.getField(Person.class, "address");
    private static final Field ADDRESS_CITY_FIELD = ReflectionUtils.getField(Address.class, "city");
    private static final Field PETS_FIELD = ReflectionUtils.getField(Person.class, "pets");

    @Test
    void getRootType() {
        final Type rootGenericType = Types.FOO_LIST_INTEGER.get();
        ModelContext<?> ctx = ModelContext.builder(rootGenericType).build();
        assertThat(ctx.getRootType()).isEqualTo(rootGenericType);
    }

    @Test
    void withNullableField() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withNullable(toFieldSelector(NAME_FIELD))
                .withNullable(toFieldSelector(ADDRESS_FIELD))
                .build();

        assertThat(ctx.isNullable(mockNode(Person.class, NAME_FIELD))).isTrue();
        assertThat(ctx.isNullable(mockNode(Person.class, ADDRESS_FIELD))).isTrue();
    }

    @Test
    void withNullableClass() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withNullable(all(Address.class))
                .withNullable(all(UUID.class))
                .build();

        assertThat(ctx.isNullable(mockNode(Address.class))).isTrue();
        assertThat(ctx.isNullable(mockNode(UUID.class))).isTrue();
    }

    @Test
    void withSeed() {
        final int expected = 123;
        ModelContext<?> ctx = ModelContext.builder(Person.class).withSeed(expected).build();
        assertThat(ctx.getRandom().getSeed()).isEqualTo(expected);
    }

    @Test
    void withIgnoredField() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withIgnored(toFieldSelector(NAME_FIELD))
                .withIgnored(toFieldSelector(ADDRESS_FIELD))
                .build();

        assertThat(ctx.isIgnored(mockNode(Person.class, NAME_FIELD))).isTrue();
        assertThat(ctx.isIgnored(mockNode(Person.class, ADDRESS_FIELD))).isTrue();
    }

    @Test
    void withIgnoredClass() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withIgnored(Select.all(all(Address.class), all(Pet.class)))
                .build();

        assertThat(ctx.isIgnored(mockNode(Address.class))).isTrue();
        assertThat(ctx.isIgnored(mockNode(Pet.class))).isTrue();
    }

    @Test
    void withGenerators() {
        final Generator<String> stringGenerator = random -> "some string";
        final Generator<Address> addressGenerator = new Generator<Address>() {
            @Override
            public Address generate(final Random random) {
                return Address.builder().build();
            }

            @Override
            public Hints hints() {
                return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS);
            }
        };

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGenerator(field(ADDRESS_FIELD.getName()), addressGenerator)
                .withGenerator(all(String.class), stringGenerator)
                .build();

        assertThat(ctx.getGenerator(mockNode(Person.class, ADDRESS_FIELD)))
                .as("Should NOT be decorated since it has AfterGenerate hint")
                .containsSame(addressGenerator);

        assertThat(ctx.getGenerator(mockNode(String.class))).isPresent().get()
                .as("Should be decorated since its Hints are null")
                .isNotSameAs(stringGenerator)
                .isExactlyInstanceOf(GeneratorDecorator.class);
    }

    @Test
    void withSuppliers() {
        final Supplier<Address> addressSupplier = Address::new;
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withSupplier(field(ADDRESS_FIELD.getName()), addressSupplier)
                .build();

        assertThat(ctx.getGenerator(mockNode(Person.class, ADDRESS_FIELD))).get()
                .as("Should NOT be decorated since it has AfterGenerate hint")
                .isExactlyInstanceOf(SupplierAdapter.class);
    }

    @Test
    void withGeneratorSpecs() {
        final GeneratorContext genContext = new GeneratorContext(Settings.defaults(), mock(Random.class));
        final Generators generators = new Generators(genContext);

        final ArrayGeneratorSpec<Object> petsSpec = generators.array().subtype(Pet[].class).length(3);

        final GeneratorSpec<String> stringSpec = generators.string().minLength(5).allowEmpty();

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGeneratorSpec(field("pets"), gens -> petsSpec)
                .withGeneratorSpec(allStrings(), gen -> stringSpec)
                .build();

        assertThat(ctx.getGenerator(mockNode(Person.class, PETS_FIELD))).isPresent().get().isSameAs(petsSpec);
        assertThat(ctx.getGenerator(mockNode(String.class))).isPresent().get().isSameAs(stringSpec);
    }

    @Test
    void withSubtypeMapping() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withSubtype(all(Collection.class), HashSet.class)
                .withSubtype(all(List.class), LinkedList.class)
                .build();

        assertThat(ctx.getSubtypeSelectorMap().getSubtype(mockNode(Collection.class))).contains(HashSet.class);
        assertThat(ctx.getSubtypeSelectorMap().getSubtype(mockNode(List.class))).contains(LinkedList.class);
    }

    @Test
    void nullSubtype() {
        final ModelContext.Builder<Object> builder = ModelContext.builder(Person.class);

        assertThatThrownBy(() -> builder.withSubtype(all(List.class), null))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Subtype must not be null");
    }

    @Test
    void lenient() {
        final ModelContext<?> ctx = ModelContext.builder(Person.class).lenient().build();
        assertThat((Mode) ctx.getSettings().get(Keys.MODE)).isEqualTo(Mode.LENIENT);
    }

    @Test
    void unusedSelectors() {
        final ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withSupplier(field("name"), () -> "foo")
                .withIgnored(field("address"))
                .build();

        assertThatThrownBy(ctx::reportUnusedSelectorWarnings).isInstanceOf(UnusedSelectorException.class);

        ctx.getGenerator(mockNode(Person.class, NAME_FIELD));
        ctx.isIgnored(mockNode(Person.class, ADDRESS_FIELD));
        ctx.reportUnusedSelectorWarnings(); // no error

        final ModelContext<?> newCtx = ctx.toBuilder().build();

        assertThatThrownBy(newCtx::reportUnusedSelectorWarnings)
                .as("Cloned context should have its own unused selectors state")
                .isInstanceOf(UnusedSelectorException.class);
    }

    @Test
    void withSettings() {
        int min = -100;
        int max = -50;

        Settings settings1 = Settings.create()
                .set(Keys.LONG_MIN, (long) min)
                .set(Keys.LONG_MAX, (long) max);

        Settings settings2 = Settings.create()
                .set(Keys.INTEGER_MIN, min)
                .set(Keys.INTEGER_MAX, max);

        final ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withSettings(settings1)
                .withSettings(Settings.create())
                .withSettings(settings2)
                .withSettings(Settings.create())
                .build();

        final Settings result = ctx.getSettings();
        assertThat((long) result.get(Keys.LONG_MIN)).isEqualTo(min);
        assertThat((long) result.get(Keys.LONG_MAX)).isEqualTo(max);
        assertThat((int) result.get(Keys.INTEGER_MIN)).isEqualTo(min);
        assertThat((int) result.get(Keys.INTEGER_MAX)).isEqualTo(max);
    }

    @Test
    void toBuilder() {
        final Generator<String> allStringsGenerator = random -> "foo";
        final Generator<String> addressCityGenerator = random -> "bar";
        final Generator<Pet[]> petsGenerator = random -> new Pet[0];
        final GeneratorSpecProvider<Pet[]> petsGeneratorFn = (gen) -> petsGenerator;
        final Class<UUID> ignoredClass = UUID.class;
        final Class<Date> nullableClass = Date.class;
        final int maxDepth = 754534;
        final long seed = 37635;
        final int integerMinValue = 26546;

        final ModelContext<?> ctx = ModelContext.builder(Person.class)
                .lenient()
                .withGenerator(all(String.class), allStringsGenerator)
                .withGenerator(toFieldSelector(ADDRESS_CITY_FIELD), addressCityGenerator)
                .withGeneratorSpec(toFieldSelector(PETS_FIELD), petsGeneratorFn)
                .withIgnored(all(ignoredClass))
                .withIgnored(toFieldSelector(NAME_FIELD))
                .withNullable(all(nullableClass))
                .withNullable(toFieldSelector(ADDRESS_FIELD))
                .withMaxDepth(maxDepth)
                .withSeed(seed)
                .withSettings(Settings.create().set(Keys.INTEGER_MIN, integerMinValue))
                .withSubtype(all(List.class), LinkedList.class)
                .build();

        final ModelContext<?> actual = ctx.toBuilder().build();

        assertThat(actual.getGenerator(mockNode(String.class))).get().isExactlyInstanceOf(GeneratorDecorator.class);
        assertThat(actual.getGenerator(mockNode(Person.class, ADDRESS_CITY_FIELD))).get().isExactlyInstanceOf(GeneratorDecorator.class);
        assertThat(actual.getGenerator(mockNode(Person.class, PETS_FIELD))).get().isExactlyInstanceOf(GeneratorDecorator.class);
        assertThat(actual.isIgnored(mockNode(Person.class, NAME_FIELD))).isTrue();
        assertThat(actual.isIgnored(mockNode(ignoredClass))).isTrue();
        assertThat(actual.isNullable(mockNode(nullableClass))).isTrue();
        assertThat(actual.isNullable(mockNode(Person.class, ADDRESS_FIELD))).isTrue();
        assertThat(actual.getMaxDepth()).isEqualTo(maxDepth);
        assertThat(actual.getRandom().getSeed()).isEqualTo(seed);
        assertThat((int) actual.getSettings().get(Keys.INTEGER_MIN)).isEqualTo(integerMinValue);
        assertThat((Mode) actual.getSettings().get(Keys.MODE)).isEqualTo(Mode.LENIENT);
        assertThat(ctx.getSubtypeSelectorMap().getSubtype(mockNode(List.class))).contains(LinkedList.class);

        assertThatThrownBy(() -> actual.getSettings().set(Keys.STRING_MIN_LENGTH, 5))
                .as("Settings should be locked")
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void useModelAsTypeArgument() {
        final long seed = 37635;
        final int integerMinValue = 26546;

        final ModelContext<?> elementModel = ModelContext.builder(Person.class)
                .withSeed(seed)
                .withSettings(Settings.defaults().set(Keys.INTEGER_MIN, integerMinValue))
                .withSubtype(all(List.class), LinkedList.class)
                .build();

        ModelContext<?> ctx = ModelContext.builder(List.class)
                .useModelAsTypeArgument(elementModel)
                .build();

        assertThat(ctx.getRootType()).isEqualTo(List.class);
        assertThat(ctx.getRootTypeMap())
                .as("Model type should be added as a type parameter")
                .hasSize(1)
                .containsValue(Person.class);

        assertThat(ctx.getContainerFactories()).isEqualTo(elementModel.getContainerFactories());
        assertThat(ctx.getSubtypeSelectorMap().getSubtypeSelectors())
                .isNotEmpty()
                .isEqualTo(elementModel.getSubtypeSelectorMap().getSubtypeSelectors());

        assertThat((Integer) ctx.getSettings().get(Keys.INTEGER_MIN)).isEqualTo(integerMinValue);
        assertThat(ctx.getRandom().getSeed()).isEqualTo(seed);
    }

    private static TargetSelector toFieldSelector(final Field field) {
        return field(field.getDeclaringClass(), field.getName());
    }

    private static InternalNode mockNode(Class<?> targetClass, Field field) {
        final InternalNode node = mock(InternalNode.class);
        doReturn(targetClass).when(node).getRawType();
        doReturn(field).when(node).getField();
        return node;
    }

    private static InternalNode mockNode(Class<?> targetClass) {
        return mockNode(targetClass, null);
    }
}