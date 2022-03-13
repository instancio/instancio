package org.instancio.testsupport.templates;

import org.instancio.model.Node;
import org.instancio.model.NodeContext;
import org.instancio.model.NodeFactory;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.util.Verify;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Test models created by the {@link NodeFactory}.
 *
 * @param <T> type being verified
 */
@ModelTag
@TestInstance(PER_CLASS)
public abstract class ModelTestTemplate<T> {

    private Class<?> typeClass;
    private Class<?>[] typeArguments;

    @BeforeAll
    protected final void templateSetup() {
        TypeContext typeContext = new TypeContext(this.getClass());
        typeClass = typeContext.getTypeClass();
        typeArguments = typeContext.getTypeArguments();
    }

    /**
     * Kicks off the test method.
     */
    @MethodSource("createdModel")
    @ParameterizedTest(name = "{0}")
    protected final void verifyingModel(Node result) {
        assertThat(result).isNotNull();
        verify(result);
    }

    /**
     * A method for verifying created node.
     */
    protected abstract void verify(Node rootNode);


    private Stream<Arguments> createdModel() {
        final String displayName = getTestDisplayName();
        final Map<TypeVariable<?>, Class<?>> rootTypeMap = new HashMap<>();
        final TypeVariable<?>[] typeParameters = typeClass.getTypeParameters();

        Verify.isTrue(typeParameters.length == typeArguments.length,
                "Type parameters (%s) and type arguments (%s) arrays have different lengths",
                typeParameters.length, typeArguments.length);

        for (int i = 0; i < typeParameters.length; i++) {
            final TypeVariable<?> typeParameter = typeParameters[i];
            final Class<?> actualType = typeArguments[i];
            rootTypeMap.put(typeParameter, actualType);
        }

        NodeFactory nodeFactory = new NodeFactory();
        NodeContext nodeContext = new NodeContext(rootTypeMap);

        final Node result = nodeFactory.createNode(nodeContext, typeClass, null, null, null);

        return Stream.of(Arguments.of(Named.of(displayName, result)));
    }

    private String getTestDisplayName() {
        String displayName = "of type " + typeClass.getSimpleName();

        if (typeArguments.length > 0) {
            String types = Arrays.stream(typeArguments)
                    .map(Class::getSimpleName)
                    .collect(joining(","));

            displayName += "<" + types + ">";
        }
        return displayName;
    }

}
