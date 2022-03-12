package org.instancio;

import org.instancio.generator.GeneratorMap;
import org.instancio.generator.ValueGenerator;
import org.instancio.model.ArrayNode;
import org.instancio.model.ClassNode;
import org.instancio.model.MapNode;
import org.instancio.model.Node;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
import org.instancio.util.Random;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final Hierarchy hierarchy = new Hierarchy();
    private final GeneratorMap generatorMap = new GeneratorMap();
    private final ImplementationResolver implementationResolver = new InterfaceImplementationResolver();
    private final InstancioContext context;

    public GeneratorFacade(InstancioContext context) {
        this.context = context;
    }

    <C> C generateNodeValue(Node node, Object owner) {
        final Field field = node.getField();

        final Optional<C> optionalResult = attemptGenerateViaContext(field);
        if (optionalResult.isPresent()) {
            return optionalResult.get();
        }

        final Class<?> effectiveType = node.getEffectiveType().getRawType();

        if (effectiveType.isPrimitive()) {
            return (C) generatorMap.get(effectiveType).generate();
        }

        final Object ancestor = hierarchy.getAncestorWithClass(owner, effectiveType);

        if (ancestor != null) {
            LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                    owner.getClass().getSimpleName(), ancestor.getClass().getSimpleName());
            return null;
        }

        if (node instanceof ArrayNode) {
            return generateArray(node);
        }

        final ValueGenerator<?> generator = generatorMap.get(effectiveType);

        final Object result;

        if (generator == null) {

            if (effectiveType.isInterface()) {
                return resolveImplementationAndGenerate(node, owner, effectiveType);
            }

            result = ReflectionUtils.instantiate(effectiveType);

        } else {
            LOG.trace("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), effectiveType.getName());

            // If we already know how to generate this object, we don't need to collect its fields
            result = generator.generate();
            LOG.trace("Generated {} using '{}' generator ", result, generator.getClass().getSimpleName());
        }

        hierarchy.setAncestorOf(result, owner);
        return (C) result;
    }

    private <C> C generateArray(Node node) {
        final Class<?> arrayType = ((ArrayNode) node).getElementNode().getKlass(); // XXX use getEffectiveClass() ?
        final ValueGenerator<?> generator = generatorMap.getArrayGenerator(arrayType);
        final Object arrayObject = generator.generate();
        return (C) arrayObject;
    }

    /**
     * Resolve an implementation class for the given interface and attempt to generate it.
     * This method should not be called for JDK classes, such as Collection interfaces.
     */
    private <C> C resolveImplementationAndGenerate(Node parentNode, Object owner, Class<?> interfaceClass) {
        Verify.isTrue(!Collection.class.isAssignableFrom(interfaceClass)
                && !Map.class.isAssignableFrom(interfaceClass), "Must not be a collection interface!");

        LOG.debug("No generator for interface '{}'", interfaceClass.getName());

        Class<?> implementor = implementationResolver.resolve(interfaceClass).orElse(null);
        if (implementor == null) {
            LOG.debug("Interface '{}' has no implementation", interfaceClass.getName());
            return null;
        }
        ClassNode implementorClassNode = new ClassNode(parentNode.getNodeContext(), null, implementor, null, parentNode);
        return generateNodeValue(implementorClassNode, owner);
    }

    /**
     * If the context has enough information to generate a value for the field, then do so.
     * If not, return an empty {@link Optional} and proceed with the main generation flow.
     * <p>
     * TODO: hierarchy.setAncestorOf(value, owner) must be done for all generated objects
     *  unless they are from JDK classes
     */
    private <C> Optional<C> attemptGenerateViaContext(@Nullable Field field) {
        if (field == null) return Optional.empty();

        if (context.isNullable(field) && Random.trueOrFalse()) {
            // We can return a null 'GeneratorResult' or a null 'GeneratorResult.value'
            // Returning a null 'GeneratorResult.value' will ensure that a field value
            // will be overwritten with null. Otherwise, field value would retain its
            // old value (if one was assigned).
            return Optional.empty();
        }

        Object result = null;
        if (context.getUserSuppliedFieldValueGenerators().containsKey(field)) {
            ValueGenerator<?> generator = context.getUserSuppliedFieldValueGenerators().get(field);
            result = generator.generate();
        } else if (context.getUserSuppliedClassValueGenerators().containsKey(field.getType())) {
            ValueGenerator<?> generator = context.getUserSuppliedClassValueGenerators().get(field.getType());
            result = generator.generate();
        }
        return Optional.ofNullable((C) result);
    }

}
