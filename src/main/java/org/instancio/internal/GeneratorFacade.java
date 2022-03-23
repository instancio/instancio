package org.instancio.internal;

import org.instancio.Generator;
import org.instancio.internal.model.ArrayNode;
import org.instancio.internal.model.ClassNode;
import org.instancio.internal.model.ModelContext;
import org.instancio.internal.model.Node;
import org.instancio.internal.random.RandomProvider;
import org.instancio.internal.reflection.ImplementationResolver;
import org.instancio.internal.reflection.InterfaceImplementationResolver;
import org.instancio.util.ObjectUtils;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final AncestorTree ancestorTree = new AncestorTree();
    private final ImplementationResolver implementationResolver = new InterfaceImplementationResolver();
    private final ModelContext<?> context;
    private final RandomProvider random;
    private final GeneratorMap generatorMap;

    public GeneratorFacade(final ModelContext<?> context) {
        this.context = context;
        final Integer seed = ObjectUtils.defaultIfNull(context.getSeed(), ThreadLocalRandom.current().nextInt());
        LOG.debug("Seed: {}", seed);
        this.random = new RandomProvider(seed);
        generatorMap = new GeneratorMap(random);
    }

    GeneratorResult<?> generateNodeValue(Node node, @Nullable Object owner) {
        final Class<?> effectiveType = node.getKlass();
        final Object ancestor = ancestorTree.getObjectAncestor(owner, node.getParent());

        if (ancestor != null) {
            LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                    owner.getClass().getSimpleName(), ancestor.getClass().getSimpleName());

            return GeneratorResult.nullResult();
        }

        final Optional<GeneratorResult<?>> optionalResult = attemptGenerateViaContext(node);
        if (optionalResult.isPresent()) {
            return optionalResult.get();
        }

        if (effectiveType.isPrimitive()) {
            return GeneratorResult.build(generatorMap.get(effectiveType).generate());
        }

        if (node instanceof ArrayNode) {
            return generateArray(node);
        }

        final Generator<?> generator = generatorMap.get(effectiveType);

        final GeneratorResult<?> result;

        if (generator == null) {

            if (effectiveType.isInterface()) {
                return resolveImplementationAndGenerate(node, owner, effectiveType);
            }

            GeneratorSettings settings = null;
            if (Collection.class.isAssignableFrom(effectiveType) || Map.class.isAssignableFrom(effectiveType)) {
                settings = GeneratorSettings.builder().dataStructureSize(2).build();
            }
            result = GeneratorResult.builder(ReflectionUtils.instantiate(effectiveType)).withSettings(settings).build();

        } else {
            LOG.trace("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), effectiveType.getName());

            // If we already know how to generate this object, we don't need to collect its fields
            result = GeneratorResult.builder(generator.generate()).withSettings(generator.getSettings()).build();
            LOG.trace("Generated {} using '{}' generator ", result, generator.getClass().getSimpleName());
        }


        ancestorTree.setObjectAncestor(result.getValue(), new AncestorTree.InstanceNode(owner, node.getParent()));
        return result;
    }

    private GeneratorResult<?> generateArray(Node node) {
        final Class<?> componentType = ((ArrayNode) node).getElementNode().getKlass();
        final Generator<?> generator = generatorMap.getArrayGenerator(componentType);
        final Object arrayObject = generator.generate();
        return GeneratorResult.build(arrayObject);
    }

    /**
     * Resolve an implementation class for the given interface and attempt to generate it.
     * This method should not be called for JDK classes, such as Collection interfaces.
     */
    private GeneratorResult<?> resolveImplementationAndGenerate(
            final Node parentNode,
            @Nullable final Object owner,
            final Class<?> interfaceClass) {
        Verify.isNotArrayCollectionOrMap(interfaceClass);

        LOG.debug("No generator for interface '{}'", interfaceClass.getName());

        Class<?> implementor = implementationResolver.resolve(interfaceClass).orElse(null);
        if (implementor == null) {
            LOG.debug("Interface '{}' has no implementation", interfaceClass.getName());
            return null;
        }
        ClassNode implementorClassNode = new ClassNode(parentNode.getNodeContext(), implementor, null, null, parentNode);
        return generateNodeValue(implementorClassNode, owner);
    }

    /**
     * If the context has enough information to generate a value for the field, then do so.
     * If not, return an empty {@link Optional} and proceed with the main generation flow.
     * <p>
     * TODO: hierarchy.setAncestorOf(value, owner) must be done for all generated objects
     *  unless they are from JDK classes
     */
    private Optional<GeneratorResult<?>> attemptGenerateViaContext(final Node node) {

        if (node.getField() != null && context.getNullableFields().contains(node.getField()) && random.trueOrFalse()) {
            // We can return a null 'GeneratorResult' or a null 'GeneratorResult.value'
            // Returning a null 'GeneratorResult.value' will ensure that a field value
            // will be overwritten with null. Otherwise, field value would retain its
            // old value (if one was assigned).
            return Optional.of(GeneratorResult.nullResult());
        }

        GeneratorResult<?> result = null;
        if (node.getField() != null && context.getUserSuppliedFieldGenerators().containsKey(node.getField())) {
            Generator<?> generator = context.getUserSuppliedFieldGenerators().get(node.getField());

            result = GeneratorResult.builder(generator.generate())
                    .withSettings(generator.getSettings())
                    .build();

        } else if (context.getUserSuppliedClassGenerators().containsKey(node.getKlass())) {
            Generator<?> generator = context.getUserSuppliedClassGenerators().get(node.getKlass());
            result = GeneratorResult.builder(generator.generate())
                    .withSettings(generator.getSettings())
                    .build();
        }
        return Optional.ofNullable(result);
    }

}
