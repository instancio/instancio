/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.generator.hints.MapHint;
import org.instancio.internal.NodePopulationFilter.NodeFilterResult;
import org.instancio.internal.assigners.Assigner;
import org.instancio.internal.assigners.AssignerImpl;
import org.instancio.internal.assignment.AssignmentErrorUtil;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.AssigmentObjectStore;
import org.instancio.internal.generation.GenerationListener;
import org.instancio.internal.generation.GeneratorFacade;
import org.instancio.internal.generator.ContainerAddFunction;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.util.ArrayUtils;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.RecordUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Keys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

/**
 * Entry point for generating an object.
 *
 * <p>A new instance of this class should be created for each
 * object generated via {@link #createRootObject()}.
 */
@SuppressWarnings({
        "PMD.CouplingBetweenObjects",
        "PMD.CyclomaticComplexity",
        "PMD.ExcessiveImports",
        "PMD.GodClass"
})
class InstancioEngine {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioEngine.class);

    private final GeneratorFacade generatorFacade;
    private final ModelContext context;
    private final InternalNode rootNode;
    private final ErrorHandler errorHandler;
    private final CallbackHandler callbackHandler;
    private final ContainerFactoriesHandler containerFactoriesHandler;
    private final GenerationListener[] listeners;
    private final AfterGenerate defaultAfterGenerate;
    private final NodeFilter nodeFilter;
    private final Assigner assigner;
    private final AssigmentObjectStore assigmentObjectStore;
    private final DelayedNodeQueue delayedNodeQueue = new DelayedNodeQueue();
    private final int maxGenerationAttempts;

    InstancioEngine(InternalModel<?> model) {
        context = model.getModelContext();
        rootNode = model.getRootNode();
        errorHandler = new ErrorHandler(context);
        callbackHandler = CallbackHandler.create(context);
        containerFactoriesHandler = new ContainerFactoriesHandler(context.getInternalServiceProviders());
        assigmentObjectStore = AssigmentObjectStore.create(context);
        generatorFacade = new GeneratorFacade(context, assigmentObjectStore);
        defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        maxGenerationAttempts = context.getSettings().get(Keys.MAX_GENERATION_ATTEMPTS);
        nodeFilter = new NodeFilter(context);
        assigner = new AssignerImpl(context);
        listeners = new GenerationListener[]{
                callbackHandler,
                assigmentObjectStore,
                GeneratedNullValueListener.create(context),
                SetModelValidatingListener.create(context)};
    }

    @SuppressWarnings("unchecked")
    <T> T createRootObject() {
        return (T) errorHandler
                .conditionalFailOnError(this::createRootObjectInternal)
                .orElse(null);
    }

    @Nullable
    private Object createRootObjectInternal() {
        final GeneratorResult generatorResult = createObject(rootNode);
        callbackHandler.invokeCallbacks();
        processDelayedNodes(true);
        context.reportWarnings();

        if (generatorResult.isEmpty()) {
            final Class<?> rootClass = rootNode.getTargetClass();

            if (Modifier.isAbstract(rootClass.getModifiers())
                    && !context.getSubtypeSelectorMap().getSubtype(rootNode).isPresent()) {
                throw Fail.withUsageError(ErrorMessageUtils.abstractRootWithoutSubtype(rootClass));
            }
        }

        return generatorResult.getValue();
    }

    private void processDelayedNodes(final boolean failOnUnprocessed) {
        int i = delayedNodeQueue.size();
        while (i >= 0 && !delayedNodeQueue.isEmpty()) {
            final DelayedNode entry = delayedNodeQueue.removeFirst();
            final GeneratorResult result = createObject(entry.getNode());

            if (result.isDelayed()) {
                i--;
                delayedNodeQueue.addLast(entry);
            } else {
                assignValue(entry.getParentResult().getValue(), entry.getNode(), result);
            }
        }

        if (failOnUnprocessed && (delayedNodeQueue.hasRecordNodes() || !delayedNodeQueue.isEmpty())) {
            final String msg = AssignmentErrorUtil.getUnresolvedAssignmentErrorMessage(
                    generatorFacade.getUnresolvedAssignments(), delayedNodeQueue);

            throw Fail.withUnresolvedAssignment(msg);
        }
    }

    @NotNull
    private GeneratorResult createObject(final InternalNode node, final boolean isNullable) {
        LOG.trace(" >> {}", node);

        GeneratorResult generatorResult = doCreateObject(node, isNullable);

        int retryCount = 0;

        while (!context.isAccepted(node, generatorResult.getValue())) {
            if (++retryCount > maxGenerationAttempts) { // NOPMD
                throw Fail.withUsageError(ErrorMessageUtils.maxGenerationAttemptsExceeded(
                        node, maxGenerationAttempts));
            }
            generatorResult = doCreateObject(node, isNullable);
        }

        notifyListeners(node, generatorResult);

        if (assigmentObjectStore.hasNewValues()) {
            processDelayedNodes(false);
        }

        LOG.trace("<< {} : {}", node, generatorResult);

        return generatorResult;
    }

    @NotNull
    private GeneratorResult doCreateObject(final InternalNode node, final boolean isNullable) {
        final GeneratorResult generatorResult;

        if (context.getRandom().diceRoll(isNullable)) {
            generatorResult = GeneratorResult.nullResult();
        } else if (node.is(NodeKind.JDK) || node.getChildren().isEmpty()) { // leaf - generate a value
            generatorResult = generateValue(node);
        } else if (node.is(NodeKind.ARRAY)) {
            generatorResult = generateArray(node);
        } else if (node.is(NodeKind.COLLECTION)) {
            generatorResult = generateCollection(node);
        } else if (node.is(NodeKind.MAP)) {
            generatorResult = generateMap(node);
        } else if (node.is(NodeKind.RECORD)) {
            generatorResult = generateRecord(node);
        } else if (node.is(NodeKind.CONTAINER)) {
            generatorResult = generateContainer(node);
        } else if (node.is(NodeKind.POJO)) {
            generatorResult = generatePojo(node);
        } else { // unreachable
            throw Fail.withFataInternalError("Unhandled node kind: '%s' for %s", node.getNodeKind(), node);
        }
        return generatorResult;
    }

    @NotNull
    private GeneratorResult createObject(final InternalNode node) {
        return createObject(node, false);
    }

    @NotNull
    private GeneratorResult generatePojo(final InternalNode node) {
        final GeneratorResult nodeResult = generateValue(node);

        if (!nodeResult.isDelayed()) {
            populateChildren(node.getChildren(), nodeResult);
        }
        return nodeResult;
    }

    private void populateArray(final InternalNode node, final GeneratorResult result) {
        final InternalNode elementNode = node.getOnlyChild();
        if (elementNode.is(NodeKind.POJO)) {
            final Object[] array = (Object[]) result.getValue();
            for (Object element : array) {
                final GeneratorResult elementResult = GeneratorResult.create(element, result.getHints());
                populateChildren(elementNode.getChildren(), elementResult);
            }
        }
    }

    private void populateCollection(final InternalNode node, final GeneratorResult result) {
        final InternalNode elementNode = node.getOnlyChild();
        if (elementNode.is(NodeKind.POJO)) {
            final Iterable<?> iterable = (Iterable<?>) result.getValue();
            for (Object element : iterable) {
                final GeneratorResult elementResult = GeneratorResult.create(element, result.getHints());
                populateChildren(elementNode.getChildren(), elementResult);
            }
        }
    }

    private void populateMap(final InternalNode node, final GeneratorResult result) {
        final InternalNode keyNode = node.getChildren().get(0);
        final InternalNode valueNode = node.getChildren().get(1);
        final Map<?, ?> map = (Map<?, ?>) result.getValue();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            final Object k = entry.getKey();
            final Object v = entry.getValue();

            if (keyNode.is(NodeKind.POJO)) {
                final GeneratorResult keyResult = GeneratorResult.create(k, result.getHints());
                populateChildren(keyNode.getChildren(), keyResult);
            }
            if (valueNode.is(NodeKind.POJO)) {
                final GeneratorResult valueResult = GeneratorResult.create(v, result.getHints());
                populateChildren(valueNode.getChildren(), valueResult);
            }
        }
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.NPathComplexity"})
    private GeneratorResult generateMap(final InternalNode node) {
        final GeneratorResult generatorResult = generateValue(node);

        if (generatorResult.containsNull() || node.getChildren().size() < 2) {
            return generatorResult;
        }

        ApiValidator.validateValueIsAssignableToTargetClass(generatorResult.getValue(), Map.class, node);

        //noinspection unchecked
        final Map<Object, Object> map = (Map<Object, Object>) generatorResult.getValue();
        final InternalNode keyNode = node.getChildren().get(0);
        final InternalNode valueNode = node.getChildren().get(1);
        final Hints hints = generatorResult.getHints();

        // Populated objects that were created/added in the generator itself
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            final List<InternalNode> keyNodeChildren = keyNode.getChildren();
            final List<InternalNode> valueNodeChildren = valueNode.getChildren();

            populateChildren(keyNodeChildren, GeneratorResult.create(entry.getKey(), hints));
            populateChildren(valueNodeChildren, GeneratorResult.create(entry.getValue(), hints));
        }

        if (keyNode.isIgnored() || valueNode.isIgnored()) {
            return generatorResult;
        }

        final MapHint hint = defaultIfNull(hints.get(MapHint.class), MapHint.empty());
        final boolean nullableKey = hint.nullableMapKeys();
        final boolean nullableValue = hint.nullableMapValues();
        final Iterator<Object> withKeysIterator = hint.withKeys().iterator();

        int entriesToGenerate = hint.generateEntries();
        int failedAdditions = 0;

        while (entriesToGenerate > 0) {

            assigmentObjectStore.enterScope();
            final GeneratorResult mapKeyResult = createObject(keyNode, nullableKey);
            final GeneratorResult mapValueResult = createObject(valueNode, nullableValue);
            assigmentObjectStore.exitScope();

            if (mapKeyResult.isDelayed() || mapValueResult.isDelayed()) {
                return GeneratorResult.delayed();
            }

            final Object mapValue = mapValueResult.getValue();

            final Object mapKey = withKeysIterator.hasNext()
                    ? withKeysIterator.next()
                    : mapKeyResult.getValue();

            // Note: map key does not support emit() null
            if ((mapKey != null || nullableKey)
                    && (mapValue != null || nullableValue || mapValueResult.hasEmitNullHint())) {
                if (!map.containsKey(mapKey)) {
                    ApiValidator.validateValueIsAssignableToElementNode(
                            "error adding key to map", mapKey, node, keyNode);

                    ApiValidator.validateValueIsAssignableToElementNode(
                            "error adding value to map", mapValue, node, valueNode);

                    map.put(mapKey, mapValue);
                    entriesToGenerate--;
                } else {
                    failedAdditions++;
                }
            } else {
                failedAdditions++;
            }

            if (failedAdditions > maxGenerationAttempts) {
                if (!keyNode.isCyclic() && !valueNode.isCyclic()) {
                    errorHandler.conditionalFailOnError(() -> {
                        throw Fail.withInternalError(
                                ErrorMessageUtils.mapCouldNotBePopulated(
                                        context, node, hint.generateEntries()));
                    });
                }
                break;
            }
        }

        if (!hint.withEntries().isEmpty()) {
            map.putAll(hint.withEntries());
        }

        return containerFactoriesHandler.substituteResult(node, generatorResult);
    }

    @SuppressWarnings({
            "PMD.CognitiveComplexity",
            "PMD.NPathComplexity",
            "PMD.AvoidReassigningLoopVariables"})
    private GeneratorResult generateArray(final InternalNode node) {
        final GeneratorResult generatorResult = generateValue(node);

        if (generatorResult.containsNull() || node.getChildren().isEmpty()) {
            return generatorResult;
        }

        final Object arrayObj = generatorResult.getValue();
        final Hints hints = generatorResult.getHints();
        final ArrayHint hint = defaultIfNull(hints.get(ArrayHint.class), ArrayHint.empty());

        final List<?> withElements = hint.withElements();
        final int arrayLength = Array.getLength(arrayObj);
        final InternalNode elementNode = node.getOnlyChild();
        int lastIndex = 0;

        // Fill-in withElements first (if any)
        for (int i = 0, j = 0; i < arrayLength && j < withElements.size(); i++) {
            final Object elementValue = Array.get(arrayObj, i);

            // Populate objects created by user within the generator
            if (elementValue != null) {
                final List<InternalNode> elementNodeChildren = node.getOnlyChild().getChildren();
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

        // If array elements fail to generate for any reason and null is returned,
        // terminate the loop once we reach the threshold to avoid an infinite loop.
        int failedAdditions = 0;

        for (int i = lastIndex; i < arrayLength; i++) {

            // Current value at index may have been set by a custom generator
            final Object currentValue = Array.get(arrayObj, i);

            // Populate objects created by user within the generator
            if (currentValue != null) {
                final List<InternalNode> elementNodeChildren = node.getOnlyChild().getChildren();
                populateChildren(elementNodeChildren, GeneratorResult.create(currentValue, hints));
            }

            if (nodeFilter.filter(elementNode, action, currentValue) == NodeFilterResult.SKIP) {
                continue;
            }

            assigmentObjectStore.enterScope();
            final GeneratorResult elementResult = createObject(elementNode, hint.nullableElements());
            assigmentObjectStore.exitScope();

            if (elementResult.isDelayed()) {
                return GeneratorResult.delayed();
            }

            Object elementValue = elementResult.getValue();

            // If elements are not nullable, keep generating until a non-null
            while (elementValue == null
                    && !hint.nullableElements()
                    && !elementResult.hasEmitNullHint()
                    && !context.isIgnored(elementNode)
                    && failedAdditions < maxGenerationAttempts) {

                failedAdditions++;
                elementValue = createObject(elementNode, false).getValue();
            }

            // can't assign null values to primitive arrays
            if (!isPrimitiveArray || elementValue != null) {
                ApiValidator.validateValueIsAssignableToElementNode(
                        "array element type mismatch", elementValue, node, elementNode);

                Array.set(arrayObj, i, elementValue);
            }
        }

        if (hint.shuffle()) {
            ArrayUtils.shuffle(arrayObj, context.getRandom());
        }
        return generatorResult;
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.NPathComplexity"})
    private GeneratorResult generateCollection(final InternalNode node) {
        final GeneratorResult generatorResult = generateValue(node);

        if (generatorResult.containsNull() || node.getChildren().isEmpty()) {
            return generatorResult;
        }

        ApiValidator.validateValueIsAssignableToTargetClass(generatorResult.getValue(), Collection.class, node);

        //noinspection unchecked
        final Collection<Object> collection = (Collection<Object>) generatorResult.getValue();
        final InternalNode elementNode = node.getOnlyChild();
        final Hints hints = generatorResult.getHints();

        // Populated objects that were created/added in the generator itself
        for (Object element : collection) {
            final List<InternalNode> elementNodeChildren = elementNode.getChildren();
            populateChildren(elementNodeChildren, GeneratorResult.create(element, hints));
        }

        if (elementNode.isIgnored()) {
            return generatorResult;
        }

        final CollectionHint hint = defaultIfNull(hints.get(CollectionHint.class), CollectionHint.empty());
        final boolean nullableElements = hint.nullableElements();
        final boolean requireUnique = hint.unique();

        int elementsToGenerate = hint.generateElements();
        int failedAdditions = 0;

        final Set<Object> generated = new HashSet<>(elementsToGenerate);

        while (elementsToGenerate > 0) {
            assigmentObjectStore.enterScope();
            final GeneratorResult elementResult = createObject(elementNode, nullableElements);
            assigmentObjectStore.exitScope();

            if (elementResult.isDelayed()) {
                return GeneratorResult.delayed();
            }

            final Object elementValue = elementResult.getValue();

            if (elementValue != null || nullableElements || elementResult.hasEmitNullHint()) {

                boolean canAdd = !requireUnique || !generated.contains(elementValue);

                if (requireUnique) {
                    generated.add(elementValue);
                }

                if (canAdd && collection.add(elementValue)) {
                    ApiValidator.validateValueIsAssignableToElementNode(
                            "error adding element to collection", elementValue, node, elementNode);

                    elementsToGenerate--;

                } else {
                    // Special case for hash based collections.
                    // If requested size is impossible (e.g. a Set<Boolean> of size 5)
                    // then abandon populating it after the threshold is reached
                    failedAdditions++;
                }
            } else {
                // Avoid infinite loop when a value cannot be generated
                failedAdditions++;
            }

            if (failedAdditions > maxGenerationAttempts) {
                if (!elementNode.isCyclic()) {
                    errorHandler.conditionalFailOnError(() -> {
                        throw Fail.withInternalError(
                                ErrorMessageUtils.collectionCouldNotBePopulated(
                                        context, node, hint.generateElements()));
                    });
                }
                break;
            }
        }

        if (!hint.withElements().isEmpty()) {
            collection.addAll(hint.withElements());
        }
        if (hint.shuffle()) {
            CollectionUtils.shuffle(collection, context.getRandom());
        }

        return containerFactoriesHandler.substituteResult(node, generatorResult);
    }

    private GeneratorResult generateRecord(final InternalNode node) {
        // Handle the case where user supplies a generator for creating a record,
        final GeneratorResult customRecord = generateValue(node);

        if (!customRecord.isEmpty()) {
            populateChildren(node.getChildren(), customRecord);
            return customRecord;
        }

        final List<InternalNode> children = node.getChildren();
        final Object[] args = new Object[children.size()];
        final Class<?>[] ctorArgs = RecordUtils.getComponentTypes(node.getTargetClass());

        if (ctorArgs.length != args.length) {
            LOG.debug("Record {} has {} constructor arguments, but the node has {} children. Returning a null result",
                    node.getTargetClass(), ctorArgs.length, args.length);

            return GeneratorResult.nullResult();
        }

        // Record's constructor argument nodes can depend on each other.
        // If a node depends on a subsequent node, add it to the queue,
        // along with the index, and attempt to generate it again later.
        final Deque<DelayedRecordComponentNode> recordComponentQueue = new ArrayDeque<>();

        for (int i = 0; i < args.length; i++) {
            final InternalNode child = children.get(i);
            final GeneratorResult result = createObject(child);

            if (result.isDelayed()) {
                LOG.trace("Delayed record arg: {}", child);
                recordComponentQueue.add(new DelayedRecordComponentNode(child, i));
            } else {
                args[i] = result.containsNull()
                        ? ObjectUtils.defaultValue(ctorArgs[i])
                        : result.getValue();
            }
        }

        int threshold = recordComponentQueue.size();

        while (!recordComponentQueue.isEmpty()) {
            final DelayedRecordComponentNode entry = recordComponentQueue.removeLast();
            final GeneratorResult result = createObject(entry.getNode());

            LOG.trace("Attempt to create delayed record component: {}", entry.getNode());

            if (result.isDelayed()) {
                threshold--;
                recordComponentQueue.addFirst(entry);

            } else if (!result.isEmpty() && !result.isIgnored()) {
                args[entry.getArgIndex()] = result.getValue();
            }
            if (threshold == 0) {
                break;
            }
        }

        // Record components themselves can't be delayed because
        // we need all of them at once to create a record.
        // Therefore, if a component is unavailable, the entire record is delayed.
        if (!recordComponentQueue.isEmpty()) {
            delayedNodeQueue.addRecord(node);
            return GeneratorResult.delayed();
        }

        try {
            final Object obj = RecordUtils.instantiate(node.getTargetClass(), args);
            final GeneratorResult generatorResult = GeneratorResult.create(
                    obj, Hints.afterGenerate(defaultAfterGenerate));

            delayedNodeQueue.removeRecord(node);
            return generatorResult;
        } catch (Exception ex) {
            errorHandler.conditionalFailOnError(() -> {
                throw new InstancioException("Failed creating a record for: " + node, ex);
            });
        }
        return GeneratorResult.emptyResult();
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void populateChildren(
            final List<InternalNode> children,
            final GeneratorResult generatorResult) {

        if (generatorResult.containsNull()) {
            return;
        }

        final Object parentObject = generatorResult.getValue();
        final Hints hints = generatorResult.getHints();
        final AfterGenerate action = hints.afterGenerate();

        for (final InternalNode child : children) {
            final NodeFilterResult filterResult = nodeFilter.filter(child, action, parentObject);

            if (filterResult == NodeFilterResult.GENERATE) {
                final GeneratorResult result = createObject(child);

                if (result.isDelayed()) {
                    delayedNodeQueue.addLast(new DelayedNode(child, generatorResult));
                } else {
                    assignValue(parentObject, child, result);
                }
                continue;
            }

            // Check if this field was initialised externally
            final Object childObject = ReflectionUtils.tryGetFieldValueOrElseNull(child.getField(), parentObject);

            if (childObject == null) {
                continue;
            }

            final GeneratorResult childResult = GeneratorResult.create(childObject, hints);

            // Add field value to the object store.
            // This allows fields initialised externally to work with assign()
            assigmentObjectStore.objectCreated(child, childResult);

            if (filterResult == NodeFilterResult.POPULATE) {

                if (child.is(NodeKind.POJO)) {
                    populateChildren(child.getChildren(), childResult);
                } else if (child.is(NodeKind.COLLECTION)) {
                    populateCollection(child, childResult);
                } else if (child.is(NodeKind.MAP)) {
                    populateMap(child, childResult);
                } else if (child.is(NodeKind.ARRAY)) {
                    populateArray(child, childResult);
                }
            }
        }
    }

    private GeneratorResult generateContainer(final InternalNode node) {
        GeneratorResult generatorResult = generateValue(node);

        if (generatorResult.isEmpty() || generatorResult.isIgnored()) {
            return generatorResult;
        }

        final InternalContainerHint hint = defaultIfNull(
                generatorResult.getHints().get(InternalContainerHint.class),
                InternalContainerHint.empty());

        final List<InternalNode> children = node.getChildren();

        // Creation delegated to the engine
        if (generatorResult.containsNull() && hint.createFunction() != null) {
            final Object[] args = new Object[children.size()];
            for (int i = 0; i < children.size(); i++) {
                final InternalNode childNode = children.get(i);
                final GeneratorResult childResult = createObject(childNode);

                if (childResult.isDelayed()) {
                    return GeneratorResult.delayed();
                }

                ApiValidator.validateValueIsAssignableToElementNode(
                        "error populating object due to incompatible types",
                        childResult.getValue(), node, childNode);

                args[i] = childResult.getValue();
            }

            final Object result = hint.createFunction().create(args);
            generatorResult = GeneratorResult.create(result, generatorResult.getHints());
        }

        final ContainerAddFunction<Object> addFunction = hint.addFunction();

        if (addFunction != null) {
            for (int i = 0; i < hint.generateEntries(); i++) {
                final Object[] args = new Object[children.size()];

                assigmentObjectStore.enterScope();
                for (int j = 0; j < children.size(); j++) {
                    final GeneratorResult childResult = createObject(children.get(j));
                    args[j] = childResult.getValue();
                }
                assigmentObjectStore.exitScope();
                addFunction.addTo(generatorResult.getValue(), args);
            }
        }

        if (hint.buildFunction() != null) {
            final Object builtContainer = hint.buildFunction().build(generatorResult.getValue());
            return GeneratorResult.create(builtContainer, generatorResult.getHints());
        }

        return containerFactoriesHandler.substituteResult(node, generatorResult);
    }

    private void assignValue(final Object parentResult, final InternalNode node, final GeneratorResult result) {
        if (!result.isEmpty() && !result.isIgnored()) {
            assigner.assign(node, parentResult, result.getValue());
        }
    }

    private GeneratorResult generateValue(final InternalNode node) {
        return generatorFacade.generateNodeValue(node);
    }

    private void notifyListeners(final InternalNode node, final GeneratorResult result) {
        if (result.isNormal() || result.isNull()) {
            for (GenerationListener listener : listeners) {
                listener.objectCreated(node, result);
            }
        }
    }
}
