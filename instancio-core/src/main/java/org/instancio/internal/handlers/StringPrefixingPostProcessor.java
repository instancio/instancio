package org.instancio.internal.handlers;

import org.instancio.generator.Generator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.util.StringUtils;

class StringPrefixingPostProcessor implements GeneratedValuePostProcessor {

    private final boolean stringFieldPrefixEnabled;

    StringPrefixingPostProcessor(final boolean stringFieldPrefixEnabled) {
        this.stringFieldPrefixEnabled = stringFieldPrefixEnabled;
    }

    @Override
    public Object process(final Object value, final Node node, final Generator<?> generator) {
        if (stringFieldPrefixEnabled
                && node.getField() != null
                && generator.getClass() == StringGenerator.class
                && !StringUtils.isEmpty((String) value)) {

            return node.getField().getName() + "_" + value;
        }
        return value;
    }
}
