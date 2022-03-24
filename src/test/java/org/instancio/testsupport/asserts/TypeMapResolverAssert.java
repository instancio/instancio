package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.internal.model.TypeMap;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@SuppressWarnings("UnusedReturnValue")
public class TypeMapResolverAssert extends AbstractAssert<TypeMapResolverAssert, TypeMap> {

    private TypeMapResolverAssert(TypeMap actual) {
        super(actual, TypeMapResolverAssert.class);
    }

    public static TypeMapResolverAssert assertThatResolver(TypeMap actual) {
        return new TypeMapResolverAssert(actual);
    }

    public TypeMapResolverAssert hasTypeMapping(Class<?> klass, String typeVar, Type expectedMappedType) {
        Type mappedType = actual.getActualType(getTypeVar(klass, typeVar));
        assertThat(mappedType)
                .as("Wrong mapping for klass: %s, type variable: %s", klass.getName(), typeVar)
                .isEqualTo(expectedMappedType);
        return this;
    }

    public TypeMapResolverAssert hasTypeMapping(Class<?> klass, String typeVar, String expectedTypeName) {
        Type mappedType = actual.getActualType(getTypeVar(klass, typeVar));
        assertThat(mappedType)
                .as("Wrong mapping for klass: %s, type variable: %s", klass.getName(), typeVar)
                .isNotNull()
                .extracting(Type::getTypeName)
                .isEqualTo(expectedTypeName);
        return this;
    }

    public TypeMapResolverAssert hasTypeMapWithSize(int expected) {
        assertThat(actual.size()).isEqualTo(expected);
        return this;
    }

    public TypeMapResolverAssert hasEmptyTypeMap() {
        assertThat(actual.size()).isZero();
        return this;
    }

}