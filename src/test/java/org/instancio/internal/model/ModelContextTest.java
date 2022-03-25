package org.instancio.internal.model;

import org.instancio.Generator;
import org.instancio.Generators;
import org.instancio.exception.InstancioApiException;
import org.instancio.generators.ArrayGeneratorSpec;
import org.instancio.generators.coretypes.StringGeneratorSpec;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Pet;
import org.instancio.settings.Settings;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.field;
import static org.mockito.Mockito.when;

class ModelContextTest {
    private static final Field NAME_FIELD = ReflectionUtils.getField(Person.class, "name");
    private static final Field ADDRESS_FIELD = ReflectionUtils.getField(Person.class, "address");
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
                .withNullableField(NAME_FIELD)
                .withNullableField(ADDRESS_FIELD)
                .build();

        assertThat(ctx.getNullableFields()).containsExactlyInAnyOrder(NAME_FIELD, ADDRESS_FIELD);
    }

    @Test
    void withNullableFieldThrowsExceptionWhenGivenPrimitiveField() {
        final Field ageField = ReflectionUtils.getField(Person.class, "age");
        assertThatThrownBy(() ->
                ModelContext.builder(Person.class)
                        .withNullableField(ageField)
                        .build())
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Primitive field '%s' cannot be set to null", ageField);
    }

    @Test
    void withNullableClass() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withNullableClass(Address.class)
                .withNullableClass(UUID.class)
                .build();

        assertThat(ctx.getNullableClasses()).containsExactlyInAnyOrder(Address.class, UUID.class);
    }

    @Test
    void withNullableClassThrowsExceptionWhenGivenPrimitiveClass() {
        final Class<Integer> primitiveClass = int.class;
        assertThatThrownBy(() -> ModelContext.builder(Person.class).withNullableClass(primitiveClass).build())
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Primitive class '%s' cannot be set to null", primitiveClass);
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
                .withIgnoredField(NAME_FIELD)
                .withIgnoredField(ADDRESS_FIELD)
                .build();

        assertThat(ctx.getIgnoredFields()).containsExactlyInAnyOrder(NAME_FIELD, ADDRESS_FIELD);
    }

    @Test
    void withIgnoredClass() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withIgnoredClass(Address.class)
                .withIgnoredClass(Pet.class)
                .build();

        assertThat(ctx.getIgnoredClasses()).containsExactlyInAnyOrder(Address.class, Pet.class);
    }

    @Test
    void withGenerators() {
        final Generator<String> stringGenerator = () -> "some string";
        final Generator<Address> addressGenerator = Address::new;

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGenerator(field(ADDRESS_FIELD.getName()), addressGenerator)
                .withGenerator(all(String.class), stringGenerator)
                .build();

        assertThat(ctx.getUserSuppliedFieldGenerators().get(ADDRESS_FIELD)).isSameAs(addressGenerator);
        assertThat(ctx.getUserSuppliedClassGenerators().get(String.class)).isSameAs(stringGenerator);
    }

    @Test
    void withGeneratorSpecs() {
        final ModelContext<?> mockCtx = Mockito.mock(ModelContext.class);
        when(mockCtx.getSettings()).thenReturn(Settings.defaults());

        final Generators generators = new Generators(mockCtx);
        final ArrayGeneratorSpec<Object> petsSpec = generators.array().type(Set.class);
        final StringGeneratorSpec stringSpec = generators.string().minLength(5).allowEmpty();

        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withGeneratorSpec(field("pets"), gens -> petsSpec)
                .withGeneratorSpec(all(String.class), gen -> stringSpec)
                .build();

        assertThat(ctx.getUserSuppliedFieldGenerators().get(PETS_FIELD)).isSameAs(petsSpec);
        assertThat(ctx.getUserSuppliedClassGenerators().get(String.class)).isSameAs(stringSpec);
    }

    @Test
    void withSubtypeMapping() {
        ModelContext<?> ctx = ModelContext.builder(Person.class)
                .withSubtypeMapping(Collection.class, HashSet.class)
                .withSubtypeMapping(List.class, LinkedList.class)
                .build();

        assertThat(ctx.getSubtypeMap()).containsEntry(Collection.class, HashSet.class);
        assertThat(ctx.getSubtypeMap()).containsEntry(List.class, LinkedList.class);
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
}
