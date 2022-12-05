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
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.generator.hints.DataStructureHint;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.instancio.internal.util.ExceptionHandler.conditionalFailOnError;

/**
 * Entry point for generating an object.
 * <p>
 * A new instance of this class should be created for each object generated via {@link #createRootObject()}.
 */
@SuppressWarnings("PMD.GodClass")
class InstancioEngine {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioEngine.class);

    private final GeneratorFacade generatorFacade;
    private final ModelContext<?> context;
    private final Node rootNode;
    private final CallbackHandler callbackHandler;
    private final List<GenerationListener> listeners;
    private final RecordHelper recordHelper = new RecordHelperImpl();

    InstancioEngine(InternalModel<?> model) {
        context = model.getModelContext();
        rootNode = model.getRootNode();
        callbackHandler = new CallbackHandler(context);
        generatorFacade = new GeneratorFacade(context);
        listeners = Arrays.asList(callbackHandler, new GeneratedNullValueListener(context));
    }

    @SuppressWarnings("unchecked")
    <T> T createRootObject() {
        final T rootResult = (T) createObject(rootNode).map(GeneratorResult::getValue).orElse(null);
        callbackHandler.invokeCallbacks();
        context.reportUnusedSelectorWarnings();
        return rootResult;
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
        } else if (node.is(NodeKind.OPTIONAL)) {
            return generateOptional(node);
        } else if (node.is(NodeKind.DEFAULT)) {
            return generatePojo(node, node.getChildren());
        }

        conditionalFailOnError(() -> {
            throw new InstancioException(String.format("Unhandled node kind '%s' for %s", node.getNodeKind(), node));
        });

        return Optional.empty();
    }

    private Optional<GeneratorResult> generateOptional(final Node node) {
        final Optional<GeneratorResult> generatorResult = createObject(node.getOnlyChild());
        if (generatorResult.isPresent()) {
            final Object value = generatorResult.get().getValue();
            final GeneratorResult optResult = createGeneratorResult(Optional.ofNullable(value));
            return Optional.of(optResult);
        }

        return generatorResult;
    }

    private Optional<GeneratorResult> generateMap(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        if (!nodeResult.isPresent()
                || nodeResult.get().requiresNoAction()
                || node.getChildren().size() != 2) {
            return nodeResult;
        }

        final GeneratorResult generatorResult = nodeResult.get();
        final Map<Object, Object> map = (Map<Object, Object>) generatorResult.getValue();
        final DataStructureHint dsHint = generatorResult.getHints().get(DataStructureHint.class);
        final int size = dsHint.dataStructureSize();
        final boolean nullableKey = dsHint.nullableMapKeys();
        final boolean nullableValue = dsHint.nullableMapValues();

        for (int i = 0; i < size; i++) {

            final Object mapKey = createObject(node.getChildren().get(0), nullableKey);
            final Object mapValue = createObject(node.getChildren().get(1), nullableValue);

            if ((mapKey != null || nullableKey) && (mapValue != null || nullableValue)) {
                map.put(mapKey, mapValue);
            }
        }

        return nodeResult;
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

    private GeneratorResult createGeneratorResult(final Object value) {
        final PopulateAction defaultAction = context.getSettings().get(Keys.GENERATOR_HINT_POPULATE_ACTION);
        return GeneratorResult.create(value, Hints.withPopulateAction(defaultAction));
    }

    private Optional<GeneratorResult> generatePojo(final Node node, final List<Node> children) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        if (!nodeResult.isPresent()) {
            return Optional.empty();
        }

        final GeneratorResult generatorResult = nodeResult.get();
        final Object value = generatorResult.getValue();

        for (final Node child : children) {
            if (shouldSkipNode(child, generatorResult)) {
                continue;
            }

            final Optional<GeneratorResult> optResult = createObject(child);
            if (optResult.isPresent()) {
                final Object arg = optResult.get().getValue();
                final Field field = child.getField();

                if (arg != null) {
                    conditionalFailOnError(() -> ReflectionUtils.setField(value, field, arg));
                } else if (!field.getType().isPrimitive()) {
                    conditionalFailOnError(() -> ReflectionUtils.setField(value, field, null));
                }
            }
        }
        return nodeResult;
    }

    private boolean shouldSkipNode(final Node node, final GeneratorResult result) {
        final PopulateAction action = result.getHints().populateAction();

        if (action == PopulateAction.NONE) {
            return true;
        }
        if (action == PopulateAction.ALL) {
            return false;
        }

        // For APPLY_SELECTORS and remaining actions, if there is at least
        // one matching selector for this node, then it should not be skipped
        if (context.getGenerator(node).isPresent()) {
            return false;
        }
        if (action == PopulateAction.NULLS) {
            return ReflectionUtils.hasNonNullValue(node.getField(), result.getValue());
        }
        if (action == PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES) {
            return ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue(
                    node.getField(), result.getValue());
        }
        return true; // skip if action is null
    }

    private Optional<GeneratorResult> generateCollection(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        if (!nodeResult.isPresent()
                || nodeResult.get().requiresNoAction()
                || node.getChildren().size() != 1) {
            return nodeResult;
        }

        final GeneratorResult generatorResult = nodeResult.get();
        final Collection<Object> collection = (Collection<Object>) generatorResult.getValue();
        final DataStructureHint dsHint = generatorResult.getHints().get(DataStructureHint.class);
        final int size = dsHint.dataStructureSize();
        final boolean nullableElement = dsHint.nullableElements();

        for (int i = 0; i < size; i++) {
            final Object elementValue = createObject(node.getOnlyChild(), nullableElement);

            if (elementValue != null || nullableElement) {
                collection.add(elementValue);
            }
        }

        if (!dsHint.withElements().isEmpty()) {
            collection.addAll(dsHint.withElements());
            CollectionUtils.shuffle(collection, context.getRandom());
        }

        return nodeResult;
    }

    private Optional<GeneratorResult> generateArray(final Node node) {
        final Optional<GeneratorResult> nodeResult = generateValue(node);

        if (!nodeResult.isPresent()
                || nodeResult.get().requiresNoAction()
                || node.getChildren().size() != 1) {
            return nodeResult;
        }

        final GeneratorResult generatorResult = nodeResult.get();
        final Object arrayObj = generatorResult.getValue();
        final Node elementNode = node.getOnlyChild();
        final DataStructureHint dsHint = generatorResult.getHints().get(DataStructureHint.class);
        final List<?> withElements = dsHint.withElements();
        int index = 0;

        for (int len = Array.getLength(arrayObj) - withElements.size(); index < len; index++) {
            final Object elementValue = createObject(elementNode, dsHint.nullableElements());

            if (elementValue != null) {
                Array.set(arrayObj, index, elementValue);
            }
        }

        if (!withElements.isEmpty()) {
            for (int j = 0; j < withElements.size(); j++) {
                Array.set(arrayObj, j + index, withElements.get(j));
            }
            ArrayUtils.shuffle(arrayObj, context.getRandom());
        }
        return nodeResult;
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
