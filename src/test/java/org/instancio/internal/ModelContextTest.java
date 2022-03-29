/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal;

import org.instancio.Binding;
import org.instancio.Generator;
import org.instancio.GeneratorContext;
import org.instancio.GeneratorSpec;
import org.instancio.Generators;
import org.instancio.exception.InstancioApiException;
import org.instancio.generators.ArrayGeneratorSpec;
import org.instancio.generators.coretypes.StringGeneratorSpec;
import org.instancio.internal.random.RandomProvider;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Pet;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.field;
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
                .withNullable(toFieldBinding(NAME_FIELD))
                .withNullable(toFieldBinding(ADDRESS_FIELD))
                .build();

        assertThat(ctx.isNullable(NAME_FIELD)).isTrue();
        assertThat(ctx.isNullable(ADDRESS_FIELD)).isTrue();
    }


    @Test
    void withNullableFieldIgnoresPrimitiveField() {
        final Field ageField = ReflectionUtils.getField(Person.class, "age");
        final ModelContext<Object> ctx = ModelContext.builder(Person.class)
                .withNullable(toFieldBinding(ageField))
                .build();

        assertThat(ctx.getUserSuppliedGenerator(ageField)).isEmpty();
    }

    @Test
    void withNullableClass() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withNullable(all(Address.class))
                .withNullable(all(UUID.class))
                .build();

        assertThat(ctx.isNullable(Address.class)).isTrue();
        assertThat(ctx.isNullable(UUID.class)).isTrue();
    }

    @Test
    void withNullableClassThrowsExceptionWhenGivenPrimitiveClass() {
        final Class<?> primitiveClass = int.class;
        final ModelContext<Object> ctx = ModelContext.builder(Person.class)
                .withNullable(all(primitiveClass))
                .build();

        assertThat(ctx.getUserSuppliedGenerator(primitiveClass)).isEmpty();
    }

    @Test
    void withSeed() {
        final int expected = 123;
        ModelContext<?> ctx = ModelContext.builder(Person.class).withSeed(expected).build();
        assertThat(ctx.getSeed()).isEqualTo(expected);
    }

    @Test
    void withIgnoredField() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withIgnored(toFieldBinding(NAME_FIELD))
                .withIgnored(toFieldBinding(ADDRESS_FIELD))
                .build();

        assertThat(ctx.isIgnored(NAME_FIELD)).isTrue();
        assertThat(ctx.isIgnored(ADDRESS_FIELD)).isTrue();
    }

    @Test
    void withIgnoredClass() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withIgnored(Binding.of(all(Address.class), all(Pet.class)))
                .build();

        assertThat(ctx.isIgnored(Address.class)).isTrue();
        assertThat(ctx.isIgnored(Pet.class)).isTrue();
    }

    @Test
    void withGenerators() {
        final Generator<String> stringGenerator = () -> "some string";
        final Generator<Address> addressGenerator = Address::new;

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGenerator(field(ADDRESS_FIELD.getName()), addressGenerator)
                .withGenerator(all(String.class), stringGenerator)
                .build();

        assertThat(ctx.getUserSuppliedGenerator(ADDRESS_FIELD)).containsSame(addressGenerator);
        assertThat(ctx.getUserSuppliedGenerator(String.class)).containsSame(stringGenerator);
    }

    @Test
    void withGeneratorSpecs() {
        final GeneratorContext genContext = new GeneratorContext(Settings.defaults(), mock(RandomProvider.class));
        final Generators generators = new Generators(genContext);
        final ArrayGeneratorSpec<Object> petsSpec = generators.array().type(Set.class);
        final StringGeneratorSpec stringSpec = generators.string().minLength(5).allowEmpty();

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGeneratorSpec(field("pets"), gens -> petsSpec)
                .withGeneratorSpec(all(String.class), gen -> stringSpec)
                .build();

        assertThat(ctx.getUserSuppliedGenerator(PETS_FIELD)).isPresent().get().isSameAs(petsSpec);
        assertThat(ctx.getUserSuppliedGenerator(String.class)).isPresent().get().isSameAs(stringSpec);
    }

    @Test
    void withSubtypeMapping() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withSubtypeMapping(Collection.class, HashSet.class)
                .withSubtypeMapping(List.class, LinkedList.class)
                .build();

        assertThat(ctx.getSubtypeMapping(Collection.class)).isEqualTo(HashSet.class);
        assertThat(ctx.getSubtypeMapping(List.class)).isEqualTo(LinkedList.class);
    }

    @Test
    void withSubtypeMappingRejectsInvalidMapping() {
        final ModelContext.Builder<Object> builder = ModelContext.builder(Person.class);

        assertThatThrownBy(() -> builder.withSubtypeMapping(ArrayList.class, List.class))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Class '%s' is not a subtype of '%s'", List.class.getName(), ArrayList.class.getName());

        assertThatThrownBy(() ->
                builder.withSubtypeMapping(ArrayList.class, ArrayList.class))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Cannot map the class to itself: '%s'", ArrayList.class.getName());

        assertThatThrownBy(() -> builder.withSubtypeMapping(List.class, AbstractList.class))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Class must not be an interface or abstract class: '%s'", AbstractList.class.getName());
    }

    @Test
    void toBuilder() {
        final Generator<?> allStringsGenerator = () -> "foo";
        final Generator<?> addressCityGenerator = () -> "bar";
        final Generator<?> petsGenerator = () -> new Pet[0];
        final Function<Generators, ? extends GeneratorSpec<?>> petsGeneratorFn = (gen) -> petsGenerator;
        final Class<UUID> ignoredClass = UUID.class;
        final Class<Date> nullableClass = Date.class;
        final int seed = 37635;
        final int integerMinValue = 26546;

        final ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGenerator(all(String.class), allStringsGenerator)
                .withGenerator(toFieldBinding(ADDRESS_CITY_FIELD), addressCityGenerator)
                .withGeneratorSpec(toFieldBinding(PETS_FIELD), petsGeneratorFn)
                .withIgnored(all(ignoredClass))
                .withIgnored(toFieldBinding(NAME_FIELD))
                .withNullable(all(nullableClass))
                .withNullable(toFieldBinding(ADDRESS_FIELD))
                .withSeed(seed)
                .withSettings(Settings.create().set(Setting.INTEGER_MIN, integerMinValue))
                .withSubtypeMapping(List.class, LinkedList.class)
                .build();

        final ModelContext<?> context = ctx.toBuilder().build();

        assertThat(context.getUserSuppliedGenerator(String.class)).containsSame(allStringsGenerator);
        assertThat(context.getUserSuppliedGenerator(ADDRESS_CITY_FIELD)).containsSame(addressCityGenerator);
        assertThat(context.getUserSuppliedGenerator(PETS_FIELD)).containsSame(petsGenerator);
        assertThat(context.isIgnored(NAME_FIELD)).isTrue();
        assertThat(context.isIgnored(ignoredClass)).isTrue();
        assertThat(context.isNullable(nullableClass)).isTrue();
        assertThat(context.isNullable(ADDRESS_FIELD)).isTrue();
        assertThat(context.getSeed()).isEqualTo(seed);
        assertThat((int) context.getSettings().get(Setting.INTEGER_MIN)).isEqualTo(integerMinValue);
        assertThat(context.getSubtypeMapping(List.class)).isEqualTo(LinkedList.class);

        assertThatThrownBy(() -> context.getSettings().set(Setting.STRING_MIN_LENGTH, 5))
                .as("Settings should be locked")
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static Binding toFieldBinding(final Field field) {
        return Binding.fieldBinding(field.getDeclaringClass(), field.getName());
    }

}
