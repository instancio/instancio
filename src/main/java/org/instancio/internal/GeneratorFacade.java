package org.instancio.internal;

import org.instancio.Generator;
import org.instancio.internal.model.ArrayNode;
import org.instancio.internal.model.ClassNode;
import org.instancio.internal.model.ModelContext;
import org.instancio.internal.model.Node;
import org.instancio.internal.random.RandomProvider;
import org.instancio.internal.reflection.ImplementationResolver;
import org.instancio.internal.reflection.InterfaceImplementationResolver;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final AncestorTree ancestorTree = new AncestorTree();
    private final ImplementationResolver implementationResolver = new InterfaceImplementationResolver();
    private final ModelContext<?> context;
    private final RandomProvider random;
    private final GeneratorMap generatorMap;
    private final Instantiator instantiator;

    public GeneratorFacade(final ModelContext<?> context) {
        this.context = context;
        this.random = context.getRandomProvider();
        this.generatorMap = new GeneratorMap(context);
        this.instantiator = new Instantiator();
    }

    GeneratorResult<?> generateNodeValue(final Node node, @Nullable final Object owner) {
        if (owner != null) {
            final Object ancestor = ancestorTree.getObjectAncestor(owner, node.getParent());
            if (ancestor != null) {
                LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                        owner.getClass().getSimpleName(), ancestor);

                return GeneratorResult.nullResult();
            }
        }

        final Optional<GeneratorResult<?>> optionalResult = attemptGenerateViaContext(node);
        if (optionalResult.isPresent()) {
            return optionalResult.get();
        }

        if (node.getKlass().isPrimitive()) {
            return GeneratorResult.build(generatorMap.get(node.getKlass()).generate());
        }

        if (node instanceof ArrayNode) {
            return generateArray(node);
        }

        final Class<?> effectiveType = context.getSubtypeMap().getOrDefault(node.getKlass(), node.getKlass());
        final Generator<?> generator = generatorMap.get(effectiveType);
        final GeneratorResult<?> result;

        if (generator == null) {
            if (effectiveType.isInterface()) {
                result = resolveImplementationAndGenerate(node, owner, effectiveType);
            } else {
                GeneratedHints hints = null;
                if (Collection.class.isAssignableFrom(effectiveType) || Map.class.isAssignableFrom(effectiveType)) {
                    hints = GeneratedHints.builder()
                            .dataStructureSize(getRandomSizeForCollectionOrMap(effectiveType))
                            .build();
                }
                result = GeneratorResult.builder(instantiator.instantiate(effectiveType)).withHints(hints).build();
            }
        } else {
            LOG.trace("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), effectiveType.getName());

            // If we already know how to generate this object, we don't need to collect its fields
            result = GeneratorResult.builder(generator.generate()).withHints(generator.getHints()).build();
            LOG.trace("Generated {} using '{}' generator ", result, generator.getClass().getSimpleName());
        }


        if (result != null && result.getValue() != null) {
            ancestorTree.setObjectAncestor(result.getValue(), new AncestorTree.AncestorTreeNode(owner, node.getParent()));
        }
        return result;
    }

    private int getRandomSizeForCollectionOrMap(Class<?> klass) {
        final Settings settings = context.getSettings();

        if (Collection.class.isAssignableFrom(klass)) {
            return random.intBetween(settings.get(Setting.COLLECTION_MIN_SIZE), settings.get(Setting.COLLECTION_MAX_SIZE));
        }
        if (Map.class.isAssignableFrom(klass)) {
            return random.intBetween(settings.get(Setting.MAP_MIN_SIZE), settings.get(Setting.MAP_MAX_SIZE));
        }

        throw new IllegalStateException("Unhandled type: " + klass); // "shouldn't happen"
    }

    private GeneratorResult<?> generateArray(Node node) {
        final Class<?> componentType = ((ArrayNode) node).getElementNode().getKlass();
        final Generator<?> generator = generatorMap.getArrayGenerator(componentType);
        final Object arrayObject = generator.generate();
        return GeneratorResult.builder(arrayObject).withHints(generator.getHints()).build();
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
     */
    private Optional<GeneratorResult<?>> attemptGenerateViaContext(final Node node) {

        if (node.getField() != null && context.getNullableFields().contains(node.getField()) && random.trueOrFalse()) {
            return Optional.of(GeneratorResult.nullResult());
        }

        GeneratorResult<?> result = null;
        if (node.getField() != null && context.getUserSuppliedFieldGenerators().containsKey(node.getField())) {
            Generator<?> generator = context.getUserSuppliedFieldGenerators().get(node.getField());

            result = GeneratorResult.builder(generator.generate())
                    .withHints(generator.getHints())
                    .build();

        } else if (context.getUserSuppliedClassGenerators().containsKey(node.getKlass())) {
            Generator<?> generator = context.getUserSuppliedClassGenerators().get(node.getKlass());
            result = GeneratorResult.builder(generator.generate())
                    .withHints(generator.getHints())
                    .build();
        }
        return Optional.ofNullable(result);
    }

}
