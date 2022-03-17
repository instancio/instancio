package org.instancio.testsupport.templates;

import org.instancio.model.InternalModel;
import org.instancio.model.ModelContext;
import org.instancio.model.Node;
import org.instancio.model.NodeFactory;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Type;
import java.util.stream.Stream;

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

    private Type genericType;

    @BeforeAll
    protected final void templateSetup() {
        TypeContext typeContext = new TypeContext(this.getClass());
        genericType = typeContext.getGenericType();
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
        // display type name with shortened package for better readability
        final String displayName = "of type " + genericType.getTypeName()
                .replace("org.instancio.pojo", "...");

        ModelContext modelContext = ModelContext.builder(genericType).build();
        InternalModel model = new InternalModel(modelContext);
        final Node result = model.getRootNode();

        return Stream.of(Arguments.of(Named.of(displayName, result)));
    }

}
