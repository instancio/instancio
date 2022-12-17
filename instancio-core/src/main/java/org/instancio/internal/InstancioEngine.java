/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal;

import org.instancio.exception.InstancioException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.generator.hints.MapHint;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.ContainerAddFunction;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.reflection.RecordHelper;
import org.instancio.internal.reflection.RecordHelperImpl;
import org.instancio.internal.util.ArrayUtils;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.instancio.internal.util.ExceptionHandler.conditionalFailOnError;
import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

/**
 * Entry point for generating an object.
 * <p>
 * A new instance of this class should be created for each object generated via {@link #createRootObject()}.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessiveImports"})
class InstancioEngine {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioEngine.class);

    private final GeneratorFacade generatorFacade;
    private final ModelContext<?> context;
    private final Node rootNode;
    private final CallbackHandler callbackHandler;
    private final List<GenerationListener> listeners;
    private final AfterGenerate defaultAfterGenerate;
    private final boolean overwriteExistingValues;
    private final RecordHelper recordHelper = new RecordHelperImpl();

    InstancioEngine(InternalModel<?> model) {
        context = model.getModelContext();
        rootNode = model.getRootNode();
        callbackHandler = new CallbackHandler(context);
        generatorFacade = new GeneratorFacade(context);
        defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        overwriteExistingValues = context.getSettings().get(Keys.OVERWRITE_EXISTING_VALUES);
        listeners = Arrays.asList(callbackHandler, new GeneratedNullValueListener(context));
    }

    @SuppressWarnings("unchecked")
    <T> T createRootObject() {
        return conditionalFailOnError(() -> {
            final T rootResult = (T) createObject(rootNode).map(GeneratorResult::getValue).orElse(null);
            callbackHandler.invokeCallbacks();
            context.reportUnusedSelectorWarnings();
            return rootResult;
        }).orElse(null);
    }

    private Optional<GeneratorResult> createObject(final Node node) {
        LOG.trace("Processing: {}", node);

        if (node.getChildren().isEmpty()) { // leaf - generate a value
            return generateValue(node);
        } else if (node.is(NodeKind.ARRAY)) {
            return generateArray(node);
        } else if (node.is(NodeKind.COLLECTION)) {
            return generateCollection(node);
        } else if (node.is(NodeKind.MAP)) {
            return generateMap(node);
        } else if (node.is(NodeKind.RECORD)) {
            return generateRecord(node);
        } else if (node.is(NodeKind.CONTAINER)) {
            return generateContainer(node);
        } else if (node.is(NodeKind.DEFAULT)) {
            return generatePojo(node);
        }

        conditionalFailOnError(() -> {
            throw new InstancioException(String.format("Unhandled node kind '%s' for %s", node.getNodeKind(), node));
        });

        return Optional.empty();
    }

    private Optional<GeneratorResult> generateContainer(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        // Note: GeneratorResult is allowed to contain null.
        // This means an instance needs to be created by the supplied createFunction()
        if (!nodeResult.isPresent()) {
            return nodeResult;
        }

        GeneratorResult generatorResult = nodeResult.get();

        final InternalContainerHint hint = defaultIfNull(
                generatorResult.getHints().get(InternalContainerHint.class),
                InternalContainerHint.empty());

        final List<Node> children = node.getChildren();

        // Creation delegated to the engine
        if (generatorResult.isNullResult() && hint.createFunction() != null) {
            final Object[] args = new Object[children.size()];
            for (int j = 0; j < children.size(); j++) {
                args[j] = createObject(children.get(j)).map(GeneratorResult::getValue).orElse(null);
            }
            final Object result = hint.createFunction().create(args);

            generatorResult = GeneratorResult.create(result, generatorResult.getHints());
        }

        final ContainerAddFunction<Object> addFunction = hint.addFunction();

        if (addFunction != null) {
            for (int i = 0; i < hint.generateEntries(); i++) {
                final Object[] args = new Object[children.size()];

                for (int j = 0; j < children.size(); j++) {
                    args[j] = createObject(children.get(j)).map(GeneratorResult::getValue).orElse(null);
                }

                addFunction.addTo(generatorResult.getValue(), args);
            }
        }

        if (hint.buildFunction() != null) {
            final Object builtContainer = hint.buildFunction().build(generatorResult.getValue());
            return Optional.of(GeneratorResult.create(builtContainer, generatorResult.getHints()));
        }


        final Optional<GeneratorResult> spiResult = substituteResult(node, generatorResult);
        return spiResult.isPresent() ? spiResult : Optional.of(generatorResult);
    }

    /**
     * Replaces the 3original result with another type. For example, converts
     * a Map to ImmutableMap using {@code ImmutableMap.copyOf(Map)}).
     */
    private Optional<GeneratorResult> substituteResult(
            final Node node,
            final GeneratorResult generatorResult) {

        return context.getContainerFactories()
                .stream()
                .map(it -> it.createFromOtherFunction(node.getTargetClass()))
                .filter(Objects::nonNull)
                .findFirst()
                .map(fn -> fn.apply(generatorResult.getValue()))
                .map(replacedValue -> GeneratorResult.create(replacedValue, generatorResult.getHints()));
    }

    private Optional<GeneratorResult> generatePojo(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);
        nodeResult.ifPresent(generatorResult -> populateChildren(node.getChildren(), generatorResult));
        return nodeResult;
    }

    private Optional<GeneratorResult> generateMap(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        if (!nodeResult.isPresent()
                || nodeResult.get().isNullResult()
                || node.getChildren().size() < 2) {
            return nodeResult;
        }

        final GeneratorResult generatorResult = nodeResult.get();
        final Hints hints = generatorResult.getHints();
        final MapHint hint = defaultIfNull(hints.get(MapHint.class), MapHint.empty());

        //noinspection unchecked
        final Map<Object, Object> map = (Map<Object, Object>) generatorResult.getValue();

        // Populated objects that were created/added in the generator itself
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            final List<Node> keyNodeChildren = node.getChildren().get(0).getChildren();
            final List<Node> valueNodeChildren = node.getChildren().get(1).getChildren();

            populateChildren(keyNodeChildren, GeneratorResult.create(entry.getKey(), hints));
            populateChildren(valueNodeChildren, GeneratorResult.create(entry.getValue(), hints));
        }

        final boolean nullableKey = hint.nullableMapKeys();
        final boolean nullableValue = hint.nullableMapValues();
        final Iterator<Object> withKeysIterator = hint.withKeys().iterator();

        for (int i = 0; i < hint.generateEntries(); i++) {

            final Object mapKey = withKeysIterator.hasNext()
                    ? withKeysIterator.next()
                    : createObject(node.getChildren().get(0), nullableKey);

            final Object mapValue = createObject(node.getChildren().get(1), nullableValue);

            if ((mapKey != null || nullableKey) && (mapValue != null || nullableValue)) {
                map.put(mapKey, mapValue);
            }
        }

        map.putAll(hint.withEntries());

        final Optional<GeneratorResult> spiResult = substituteResult(node, generatorResult);
        return spiResult.isPresent() ? spiResult : nodeResult;

    }

    @SuppressWarnings({
            "PMD.CyclomaticComplexity",
            "PMD.NPathComplexity",
            "PMD.ForLoopVariableCount",
            "PMD.AvoidReassigningLoopVariables"})
    private Optional<GeneratorResult> generateArray(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        if (!nodeResult.isPresent()
                || nodeResult.get().isNullResult()
                || node.getChildren().size() < 1) {
            return nodeResult;
        }

        final GeneratorResult generatorResult = nodeResult.get();
        final Object arrayObj = generatorResult.getValue();
        final Hints hints = generatorResult.getHints();
        final ArrayHint hint = defaultIfNull(hints.get(ArrayHint.class), ArrayHint.empty());

        final List<?> withElements = hint.withElements();
        final int arrayLength = Array.getLength(arrayObj);
        final Node elementNode = node.getOnlyChild();
        int lastIndex = 0;

        // Fill-in withElements first (if any)
        for (int i = 0, j = 0; i < arrayLength && j < withElements.size(); i++) {
            final Object elementValue = Array.get(arrayObj, i);

            // Populate objects created by user within the generator
            if (elementValue != null) {
                final List<Node> elementNodeChildren = node.getOnlyChild().getChildren();
                populateChildren(elementNodeChildren, GeneratorResult.create(elementValue, hints));
            }

            // Current element may have been set by a custom generator.
            // withElements will always override null values in object arrays
            // and default values in primitive arrays.
            if (!ReflectionUtils.neitherNullNorPrimitiveWithDefaultValue(elementNode.getRawType(), elementValue)) {
                Array.set(arrayObj, i, withElements.get(j));
                j++;
            }

            lastIndex = i + 1;
        }

        final AfterGenerate action = hints.afterGenerate();
        final boolean isPrimitiveArray = elementNode.getRawType().isPrimitive();
        final NodePopulationFilter filter = new ArrayElementNodePopulationFilter(context);

        for (int i = lastIndex; i < arrayLength; i++) {

            // Current value at index may have been set by a custom generator
            final Object currentValue = Array.get(arrayObj, i);

            // Populate objects created by user within the generator
            if (currentValue != null) {
                final List<Node> elementNodeChildren = node.getOnlyChild().getChildren();
                populateChildren(elementNodeChildren, GeneratorResult.create(currentValue, hints));
            }

            if (filter.shouldSkip(elementNode, action, currentValue)) {
                continue;
            }

            final Object elementValue = createObject(elementNode, hint.nullableElements());

            // can't assign null values to primitive arrays
            if (!isPrimitiveArray || elementValue != null) {
                Array.set(arrayObj, i, elementValue);
            }
        }

        if (hint.shuffle()) {
            ArrayUtils.shuffle(arrayObj, context.getRandom());
        }
        return nodeResult;
    }

    @SuppressWarnings("PMD.NPathComplexity")
    private Optional<GeneratorResult> generateCollection(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        if (!nodeResult.isPresent()
                || nodeResult.get().isNullResult()
                || node.getChildren().size() < 1) {
            return nodeResult;
        }

        final GeneratorResult generatorResult = nodeResult.get();
        final Hints hints = generatorResult.getHints();
        final CollectionHint hint = defaultIfNull(hints.get(CollectionHint.class), CollectionHint.empty());

        //noinspection unchecked
        final Collection<Object> collection = (Collection<Object>) generatorResult.getValue();

        // Populated objects that were created/added in the generator itself
        for (Object element : collection) {
            final List<Node> elementNodeChildren = node.getOnlyChild().getChildren();
            populateChildren(elementNodeChildren, GeneratorResult.create(element, hints));
        }

        final boolean nullableElement = hint.nullableElements();

        for (int i = 0; i < hint.generateElements(); i++) {
            final Object elementValue = createObject(node.getOnlyChild(), nullableElement);

            if (elementValue != null || nullableElement) {
                collection.add(elementValue);
            }
        }

        if (!hint.withElements().isEmpty()) {
            collection.addAll(hint.withElements());
        }
        if (hint.shuffle()) {
            CollectionUtils.shuffle(collection, context.getRandom());
        }

        final Optional<GeneratorResult> spiResult = substituteResult(node, generatorResult);
        return spiResult.isPresent() ? spiResult : nodeResult;
    }

    private Optional<GeneratorResult> generateRecord(final Node node) {

        // Handle the case where user supplies a generator for creating a record.
        final Optional<Generator<?>> generator = context.getGenerator(node);
        if (generator.isPresent()) {
            return generateValue(node);
        }

        final List<Node> children = node.getChildren();
        final Object[] args = new Object[children.size()];

        for (int i = 0; i < args.length; i++) {
            final Optional<GeneratorResult> optResult = createObject(children.get(i));

            if (optResult.isPresent()) {
                args[i] = optResult.get().getValue();
            }
        }

        try {
            final Optional<Constructor<?>> optCtor = recordHelper.getCanonicalConstructor(node.getTargetClass());
            if (optCtor.isPresent()) {
                final Constructor<?> ctor = optCtor.get();
                ctor.setAccessible(true); // NOSONAR
                final Object obj = ctor.newInstance(args);
                final GeneratorResult generatorResult = createGeneratorResult(obj);
                notifyListeners(node, generatorResult);
                return Optional.of(generatorResult);
            }
        } catch (Exception ex) {
            conditionalFailOnError(() -> {
                throw new InstancioException("Failed creating a record for: " + node, ex);
            });
        }
        return Optional.empty();
    }

    private void populateChildren(final List<Node> children, final GeneratorResult generatorResult) {
        final Object value = generatorResult.getValue();
        final AfterGenerate action = generatorResult.getHints().afterGenerate();
        final NodePopulationFilter filter = new FieldNodePopulationFilter(context);

        for (final Node child : children) {
            if (filter.shouldSkip(child, action, value)) {
                continue;
            }

            final Optional<GeneratorResult> optResult = createObject(child);
            if (optResult.isPresent()) {
                final Object arg = optResult.get().getValue();
                final Field field = child.getField();

                if (overwriteExistingValues || !ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue(field, value)) {
                    if (arg != null) {
                        conditionalFailOnError(() -> ReflectionUtils.setField(value, field, arg));
                    } else if (!field.getType().isPrimitive()) {
                        conditionalFailOnError(() -> ReflectionUtils.setField(value, field, null));
                    }
                }
            }
        }
    }

    private GeneratorResult createGeneratorResult(final Object value) {
        return GeneratorResult.create(value, Hints.afterGenerate(defaultAfterGenerate));
    }

    private Object createObject(final Node node, final boolean isNullable) {
        if (context.getRandom().diceRoll(isNullable)) {
            notifyListeners(node, GeneratorResult.nullResult());
            return null;
        }
        return createObject(node).map(GeneratorResult::getValue).orElse(null);
    }

    private Optional<GeneratorResult> generateValue(final Node node) {
        final Optional<GeneratorResult> generatorResult = generatorFacade.generateNodeValue(node);
        notifyListeners(node, generatorResult.orElse(GeneratorResult.nullResult()));
        return generatorResult;
    }

    private void notifyListeners(final Node node, final GeneratorResult result) {
        for (GenerationListener listener : listeners) {
            listener.objectCreated(node, result);
        }
    }
}
