package org.instancio.internal.handlers;

import org.instancio.documentation.InternalApi;
import org.instancio.generator.Generator;
import org.instancio.internal.nodes.InternalNode;

/**
 * Post-processor for generated values.
 *
 * @since 2.4.0
 */
@InternalApi
interface GeneratedValuePostProcessor {

    /**
     * Processes the specified value.
     *
     * @param value     that was generated
     * @param node      for which the value was generated
     * @param generator that generated the value
     * @return processed value, or the same value if no processing was done
     */
    Object process(Object value, InternalNode node, Generator<?> generator);
}
