package org.instancio.testsupport.templates;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeTokenSupplier;
import org.instancio.model.InternalModel;
import org.instancio.model.Node;
import org.instancio.model.NodeFactory;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Test models created by the {@link NodeFactory}.
 *
 * @param <T> type being verified
 */
@ModelTag
@TestInstance(PER_CLASS)
public abstract class ModelTestTemplate<T> {

    private final TypeContext typeContext = new TypeContext(this.getClass());

    @Test
    protected final void verifyingModelFromTypeToken() {
        Model<?> model = Instancio.of((TypeTokenSupplier<Type>) typeContext::getGenericType).toModel();
        verify(((InternalModel<?>) model).getRootNode());
    }

    /**
     * A method for verifying created node.
     */
    protected abstract void verify(Node rootNode);
}
