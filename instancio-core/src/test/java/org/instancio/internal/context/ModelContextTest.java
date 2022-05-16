/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.Generator;
import org.instancio.Random;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.nodes.Node;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
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
        assertThat(ctx.getRootClass()).isEqualTo(Foo.class);
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
        final Generator<Address> addressGenerator = random -> new Address();

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGenerator(field(ADDRESS_FIELD.getName()), addressGenerator)
                .withGenerator(all(String.class), stringGenerator)
                .build();

        assertThat(ctx.getUserSuppliedGenerator(mockNode(Person.class, ADDRESS_FIELD))).containsSame(addressGenerator);
        assertThat(ctx.getUserSuppliedGenerator(mockNode(String.class))).containsSame(stringGenerator);
    }

    @Test
    void withGeneratorSpecs() {
        final GeneratorContext genContext = new GeneratorContext(Settings.defaults(), mock(Random.class));
        final Generators generators = new Generators(genContext);

        final ArrayGeneratorSpec<Object> petsSpec = generators.array().type(Pet[].class).length(3);

        final StringGeneratorSpec stringSpec = generators.string().minLength(5).allowEmpty();

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGeneratorSpec(field("pets"), gens -> petsSpec)
                .withGeneratorSpec(all(String.class), gen -> stringSpec)
                .build();

        assertThat(ctx.getUserSuppliedGenerator(mockNode(Person.class, PETS_FIELD))).isPresent().get().isSameAs(petsSpec);
        assertThat(ctx.getUserSuppliedGenerator(mockNode(String.class))).isPresent().get().isSameAs(stringSpec);
    }

    @Test
    void withSubtypeMapping() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withSubtype(all(Collection.class), HashSet.class)
                .withSubtype(all(List.class), LinkedList.class)
                .build();

        assertThat(ctx.getSubtypeMapping(Collection.class)).isEqualTo(HashSet.class);
        assertThat(ctx.getSubtypeMapping(List.class)).isEqualTo(LinkedList.class);
    }

    @Test
    void invalidSubtypeMappingArrayListToList() {
        final ModelContext.Builder<Object> builder = ModelContext.builder(Person.class);
        assertThatThrownBy(() -> builder.withSubtype(all(ArrayList.class), List.class).build())
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Class '%s' is not a subtype of '%s'", List.class.getName(), ArrayList.class.getName());
    }

    @Test
    void invalidSubtypeMappingArrayListToArrayList() {
        final ModelContext.Builder<Object> builder = ModelContext.builder(Person.class);
        assertThatThrownBy(() -> builder.withSubtype(all(ArrayList.class), ArrayList.class).build())
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Cannot map the class to itself: '%s'", ArrayList.class.getName());
    }

    @Test
    void invalidSubtypeMappingArrayListToAbstractList() {
        final ModelContext.Builder<Object> builder = ModelContext.builder(Person.class);
        assertThatThrownBy(() -> builder.withSubtype(all(List.class), AbstractList.class).build())
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Class must not be an interface or abstract class: '%s'", AbstractList.class.getName());
    }

    @Test
    void toBuilder() {
        final Generator<?> allStringsGenerator = random -> "foo";
        final Generator<?> addressCityGenerator = random -> "bar";
        final Generator<?> petsGenerator = random -> new Pet[0];
        final Function<Generators, ? extends GeneratorSpec<?>> petsGeneratorFn = (gen) -> petsGenerator;
        final Class<UUID> ignoredClass = UUID.class;
        final Class<Date> nullableClass = Date.class;
        final int seed = 37635;
        final int integerMinValue = 26546;

        final ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGenerator(all(String.class), allStringsGenerator)
                .withGenerator(toFieldSelector(ADDRESS_CITY_FIELD), addressCityGenerator)
                .withGeneratorSpec(toFieldSelector(PETS_FIELD), petsGeneratorFn)
                .withIgnored(all(ignoredClass))
                .withIgnored(toFieldSelector(NAME_FIELD))
                .withNullable(all(nullableClass))
                .withNullable(toFieldSelector(ADDRESS_FIELD))
                .withSeed(seed)
                .withSettings(Settings.create().set(Keys.INTEGER_MIN, integerMinValue))
                .withSubtype(all(List.class), LinkedList.class)
                .build();

        final ModelContext<?> actual = ctx.toBuilder().build();

        assertThat(actual.getUserSuppliedGenerator(mockNode(String.class))).containsSame(allStringsGenerator);
        assertThat(actual.getUserSuppliedGenerator(mockNode(Person.class, ADDRESS_CITY_FIELD))).containsSame(addressCityGenerator);
        assertThat(actual.getUserSuppliedGenerator(mockNode(Person.class, PETS_FIELD))).containsSame(petsGenerator);
        assertThat(actual.isIgnored(mockNode(Person.class, NAME_FIELD))).isTrue();
        assertThat(actual.isIgnored(mockNode(ignoredClass))).isTrue();
        assertThat(actual.isNullable(mockNode(nullableClass))).isTrue();
        assertThat(actual.isNullable(mockNode(Person.class, ADDRESS_FIELD))).isTrue();
        assertThat(actual.getRandom().getSeed()).isEqualTo(seed);
        assertThat((int) actual.getSettings().get(Keys.INTEGER_MIN)).isEqualTo(integerMinValue);
        assertThat(actual.getSubtypeMapping(List.class)).isEqualTo(LinkedList.class);

        assertThatThrownBy(() -> actual.getSettings().set(Keys.STRING_MIN_LENGTH, 5))
                .as("Settings should be locked")
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static TargetSelector toFieldSelector(final Field field) {
        return field(field.getDeclaringClass(), field.getName());
    }


    private static Node mockNode(Class<?> targetClass, @Nullable Field field) {
        final Node node = mock(Node.class);
        doReturn(targetClass).when(node).getTargetClass();
        doReturn(field).when(node).getField();
        return node;
    }

    private static Node mockNode(Class<?> targetClass) {
        return mockNode(targetClass, null);
    }
}