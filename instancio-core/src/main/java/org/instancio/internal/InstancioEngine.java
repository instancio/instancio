/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.generator.hints.MapHint;
import org.instancio.internal.NodePopulationFilter.NodeFilterResult;
import org.instancio.internal.assigners.Assigner;
import org.instancio.internal.assigners.AssignerResolver;
import org.instancio.internal.assignment.AssignmentErrorUtil;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.AssignmentObjectStore;
import org.instancio.internal.generation.GenerationListener;
import org.instancio.internal.generation.GeneratorFacade;
import org.instancio.internal.generator.ContainerAddFunction;
import org.instancio.internal.generator.ContainerBuildFunction;
import org.instancio.internal.generator.ContainerCreateFunction;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.ConstructorDescriptor;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.selectors.ElementFrameStack;
import org.instancio.internal.util.ArrayUtils;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;
import org.instancio.support.Log;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.instancio.internal.util.Format.nodePathToRootBlock;
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
    private static final Hints POPULATE_ALL_HINTS = Hints.afterGenerate(AfterGenerate.POPULATE_ALL);

    private final GeneratorFacade generatorFacade;
    private final ModelContext context;
    private final InternalNode rootNode;
    private final ErrorHandler errorHandler;
    private final CallbackHandler callbackHandler;
    private final ContainerFactoriesHandler containerFactoriesHandler;
    private final GenerationListener[] listeners;
    private final Hints defaultAfterGenerateHints;
    private final NodeFilter nodeFilter;
    private final AssignerResolver assignerResolver;
    private final AssignmentObjectStore assignmentObjectStore;
    private final ElementFrameStack elementFrameStack;
    private final NullSubstitutorFacade nullSubstitutorFacade;
    private final DelayedNodeQueue delayedNodeQueue = new DelayedNodeQueue();
    private final int maxGenerationAttempts;
    private final boolean overwriteExistingValues;
    private final OnConstructorError onConstructorError;
    private final Instantiator instantiator;

    InstancioEngine(InternalModel<?> model) {
        context = model.getModelContext();
        rootNode = model.getRootNode();
        errorHandler = new ErrorHandler(context);
        callbackHandler = CallbackHandler.create(context);
        containerFactoriesHandler = new ContainerFactoriesHandler(context.getInternalExtensions());
        assignmentObjectStore = AssignmentObjectStore.create(context);
        elementFrameStack = context.getElementFrameStack();
        nullSubstitutorFacade = new NullSubstitutorFacade(context);
        instantiator = new Instantiator(
                context.getServiceProviders().getTypeInstantiators(),
                context.getSettings().get(Keys.INSTANTIATION_STRATEGIES));
        generatorFacade = new GeneratorFacade(context, nullSubstitutorFacade, assignmentObjectStore);
        defaultAfterGenerateHints = Hints.afterGenerate(context.getSettings().get(Keys.AFTER_GENERATE_HINT));
        maxGenerationAttempts = context.getSettings().get(Keys.MAX_GENERATION_ATTEMPTS);
        overwriteExistingValues = context.getSettings().get(Keys.OVERWRITE_EXISTING_VALUES);
        onConstructorError = context.getSettings().get(Keys.ON_CONSTRUCTOR_ERROR);
        nodeFilter = new NodeFilter(context);
        assignerResolver = AssignerResolver.create(context);
        listeners = new GenerationListener[]{
                callbackHandler,
                assignmentObjectStore,
                GeneratedNullValueListener.create(context),
                SetModelValidatingListener.create(context)};
    }

    @NullUnmarked
    @SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals"})
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
        context.reportWarnings(rootNode);

        if (generatorResult.isUnresolved()) {
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
            final ElementFrameStack.Frame capturedFrame = entry.getCapturedFrame();
            if (capturedFrame != null) {
                elementFrameStack.push(capturedFrame);
            }
            final GeneratorResult result;
            try {
                result = createObject(entry.getNode());
            } finally {
                if (capturedFrame != null) {
                    elementFrameStack.pop();
                }
            }

            if (result.isDelayed()) {
                i--;
                delayedNodeQueue.addLast(entry);
            } else {
                final GeneratorResult parentResult = entry.getParentResult();
                final Object parentResultValue = requireNonNull(parentResult.getValue());
                final Assigner assigner = assignerResolver.resolve(parentResult);
                assignValue(parentResultValue, entry.getNode(), result, assigner);
            }
        }

        if (failOnUnprocessed && (delayedNodeQueue.hasConstructorNodes() || !delayedNodeQueue.isEmpty())) {
            final String msg = AssignmentErrorUtil.getUnresolvedAssignmentErrorMessage(
                    generatorFacade.getUnresolvedAssignments(), delayedNodeQueue);

            throw Fail.withUnresolvedAssignment(msg);
        }
    }

    private GeneratorResult createObject(final InternalNode node, final boolean isNullable) {
        LOG.trace(" >> {}", node);

        GeneratorResult generatorResult = doCreateObject(node, isNullable);

        int retryCount = 0;

        while (!isResultAccepted(node, generatorResult)) {
            if (++retryCount > maxGenerationAttempts) {

                if (context.getSettings().get(Keys.FAIL_ON_MAX_GENERATION_ATTEMPTS_REACHED)) {
                    throw Fail.withUsageError(ErrorMessageUtils.maxGenerationAttemptsExceeded(
                            node, maxGenerationAttempts));
                } else {
                    Log.msg(Log.Category.MAX_GENERATION_ATTEMPTS,
                            "Max generation attempts ({}) reached (configurable via '{}'). " +
                                    "Using random value as fallback.",
                            maxGenerationAttempts,
                            Keys.MAX_GENERATION_ATTEMPTS.propertyKey());
                    break;
                }
            }
            generatorResult = doCreateObject(node, isNullable);
        }

        notifyListeners(node, generatorResult);

        if (assignmentObjectStore.hasNewValues()) {
            processDelayedNodes(false);
        }

        LOG.trace("<< {} : {}", node, generatorResult);

        return generatorResult;
    }

    private boolean isResultAccepted(final InternalNode node, final GeneratorResult result) {
        return result.isFromAssignment()
                ? context.isAcceptedAssignedValue(node, result.getValue())
                : context.isAccepted(node, result.getValue());
    }

    private GeneratorResult doCreateObject(final InternalNode node, final boolean isNullable) {
        if (context.getRandom().diceRoll(isNullable)) {
            return nullSubstitutorFacade.substituteNull(node);
        }

        return switch (node.getNodeKind()) {
            case JDK -> generateLeafNode(node);
            case POJO, RECORD -> generatePojoOrRecord(node);
            case COLLECTION -> generateCollection(node);
            case MAP -> generateMap(node);
            case ARRAY -> generateArray(node);
            case CONTAINER -> generateContainer(node);
        };
    }

    private GeneratorResult createObject(final InternalNode node) {
        return createObject(node, false);
    }

    private GeneratorResult generateLeafNode(final InternalNode node) {
        final GeneratorResult result = generateValue(node);
        return result.isUnresolved() ? instantiateTargetClassOf(node) : result;
    }

    private GeneratorResult generatePojoOrRecord(final InternalNode node) {
        final GeneratorResult generatorResult = generateValue(node);

        if (!generatorResult.isUnresolved()) {
            return storeAndPopulateAllChildren(node, generatorResult).applyBuildFunctionIfPresent();
        }
        if (node.isCyclic()) {
            return generatorResult;
        }

        final ConstructorDescriptor descriptor = node.getConstructorDescriptor();
        if (descriptor != null) {
            return generateViaConstructor(node, descriptor);
        }

        return storeAndPopulateAllChildren(node, instantiateTargetClassOf(node));
    }

    /**
     * Makes the object available to its own descendants as a back-reference
     * target, then populates every child. Used whenever the object exists in
     * full before any child is generated, which is the case for all but a
     * value-passing constructor.
     */
    private GeneratorResult storeAndPopulateAllChildren(
            final InternalNode node,
            final GeneratorResult generatorResult) {

        generatorFacade.storeGeneratedPojo(node, generatorResult);
        populateChildren(node.getChildren(), generatorResult);
        return generatorResult;
    }

    private void populateArray(final InternalNode node, final GeneratorResult result) {
        final InternalNode elementNode = node.getOnlyChild();
        if (elementNode.is(NodeKind.POJO)) {
            final Object[] array = (Object[]) requireNonNull(result.getValue());
            for (Object element : array) {
                final GeneratorResult elementResult = GeneratorResult.resolved(element, result.getHints());
                populateChildren(elementNode.getChildren(), elementResult);
            }
        }
    }

    private void populateCollection(final InternalNode node, final GeneratorResult result) {
        final InternalNode elementNode = node.getOnlyChild();
        if (elementNode.is(NodeKind.POJO)) {
            final Iterable<?> iterable = (Iterable<?>) requireNonNull(result.getValue());
            for (Object element : iterable) {
                final GeneratorResult elementResult = GeneratorResult.resolved(element, result.getHints());
                populateChildren(elementNode.getChildren(), elementResult);
            }
        }
    }

    private void populateMap(final InternalNode node, final GeneratorResult result) {
        final InternalNode keyNode = node.getChildren().get(0);
        final InternalNode valueNode = node.getChildren().get(1);
        final Map<?, ?> map = (Map<?, ?>) requireNonNull(result.getValue());

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            final Object k = entry.getKey();
            final Object v = entry.getValue();

            if (keyNode.is(NodeKind.POJO)) {
                final GeneratorResult keyResult = GeneratorResult.resolved(k, result.getHints());
                populateChildren(keyNode.getChildren(), keyResult);
            }
            if (valueNode.is(NodeKind.POJO)) {
                final GeneratorResult valueResult = GeneratorResult.resolved(v, result.getHints());
                populateChildren(valueNode.getChildren(), valueResult);
            }
        }
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.NPathComplexity"})
    private GeneratorResult generateMap(final InternalNode node) {
        final GeneratorResult generatorResult = generateValue(node);
        final InternalSize sizeOverride = context.getContainerSize(node);

        if (generatorResult.getValue() == null || node.getChildren().size() < 2) {
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

            populateChildren(keyNodeChildren, GeneratorResult.resolved(entry.getKey(), hints));
            populateChildren(valueNodeChildren, GeneratorResult.resolved(entry.getValue(), hints));
        }

        if (context.isEffectivelyIgnored(keyNode) || context.isEffectivelyIgnored(valueNode)) {
            return generatorResult;
        }

        final MapHint hint = defaultIfNull(hints.get(MapHint.class), MapHint.empty());
        final boolean nullableKey = hint.nullableMapKeys();
        final boolean nullableValue = hint.nullableMapValues();
        final Iterator<Object> withKeysIterator = hint.withKeys().iterator();

        final int targetSize = sizeOverride != null
                ? context.getRandom().intRange(sizeOverride.min(), sizeOverride.max())
                : hint.generateEntries();

        int remaining = targetSize;
        int failedAdditions = 0;

        while (remaining > 0) {

            assignmentObjectStore.enterScope();
            final GeneratorResult mapKeyResult = createObject(keyNode, nullableKey);
            final GeneratorResult mapValueResult = createObject(valueNode, nullableValue);
            assignmentObjectStore.exitScope();

            if (mapKeyResult.isDelayed() || mapValueResult.isDelayed()) {
                return GeneratorResult.delayedResult();
            }

            final Object mapValue = mapValueResult.getValue();

            final Object mapKey = withKeysIterator.hasNext()
                    ? withKeysIterator.next()
                    : mapKeyResult.getValue();

            // Note: map key does not support emit() null
            if ((mapKey != null || nullableKey)
                    && (mapValue != null || nullableValue || mapValueResult.isIntentionalNull())) {
                if (!map.containsKey(mapKey)) {
                    ApiValidator.validateValueIsAssignableToElementNode(
                            "error adding key to map", mapKey, node, keyNode);

                    ApiValidator.validateValueIsAssignableToElementNode(
                            "error adding value to map", mapValue, node, valueNode);

                    map.put(mapKey, mapValue);
                    remaining--;
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
                                        context, node, targetSize));
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
            "PMD.AvoidDeeplyNestedIfStmts",
            "PMD.CognitiveComplexity",
            "PMD.NcssCount",
            "PMD.NPathComplexity",
            "PMD.AvoidReassigningLoopVariables"})
    private GeneratorResult generateArray(final InternalNode node) {
        final GeneratorResult generatorResult = generateValue(node);
        final InternalSize sizeOverride = context.getContainerSize(node);

        if (generatorResult.getValue() == null || node.getChildren().isEmpty()) {
            return generatorResult;
        }

        final Hints hints = generatorResult.getHints();
        final ArrayHint hint = defaultIfNull(hints.get(ArrayHint.class), ArrayHint.empty());

        final List<?> withElements = hint.withElements();

        // Marked "used" so an empty or all-null array doesn't report these selectors as unused
        context.markElementOfAssignmentSelectorsUsedForContainer(node);

        // withElements occupy leading slots, so elementOf() index region starts after them.
        // Widen array if an index selector points past the region (max excludes them too)
        final int withCount = withElements.size();
        final Integer explicitMax = explicitMaxSize(hints, sizeOverride);

        if (sizeOverride != null && withCount > sizeOverride.max()) {
            throw Fail.withUsageError(
                    "array generator specifies more 'with()' elements (%s) than the size() override allows (%s)"
                            .formatted(withCount, sizeOverride.max()));
        }

        Object arrayObj = generatorResult.getValue();
        if (sizeOverride != null) {
            final int effectiveMin = Math.max(sizeOverride.min(), withCount);
            final int overrideLength = context.getRandom().intRange(effectiveMin, sizeOverride.max());
            arrayObj = Array.newInstance(arrayObj.getClass().getComponentType(), overrideLength);
        }

        final int currentRegion = Array.getLength(arrayObj) - withCount;
        final Integer explicitRegionMax = explicitMax == null ? null : explicitMax - withCount;
        final int requiredRegion = requiredRegionSize(node, currentRegion, explicitRegionMax);

        if (requiredRegion > currentRegion) {
            arrayObj = ArrayUtils.widenArray(arrayObj, withCount + requiredRegion);
        }

        final int arrayLength = Array.getLength(arrayObj);

        final InternalNode elementNode = node.getOnlyChild();
        int lastIndex = 0;

        // Fill-in withElements first (if any)
        for (int i = 0, j = 0; i < arrayLength && j < withElements.size(); i++) {
            final Object elementValue = Array.get(arrayObj, i);

            // Populate objects created by user within the generator
            if (elementValue != null) {
                populateChildren(elementNode.getChildren(), GeneratorResult.resolved(elementValue, hints));
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

        final AfterGenerate action = requireNonNull(hints.afterGenerate());
        final boolean isPrimitiveArray = elementNode.getRawType().isPrimitive();

        // If array elements fail to generate for any reason and null is returned,
        // terminate the loop once we reach the threshold to avoid an infinite loop.
        int failedAdditions = 0;

        List<Integer> delayedElementIndices = null;

        for (int i = lastIndex; i < arrayLength; i++) {

            // Current value at index may have been set by a custom generator
            final Object currentValue = Array.get(arrayObj, i);

            // Populate objects created by user within the generator
            if (currentValue != null) {
                populateChildren(elementNode.getChildren(), GeneratorResult.resolved(currentValue, hints));
            }

            if (nodeFilter.filter(elementNode, action, currentValue) == NodeFilterResult.SKIP) {
                continue;
            }

            // Index/size are relative to the generated region (after the leading withElements)
            final GeneratorResult elementResult = createElementInFrame(
                    node, elementNode, i - lastIndex, arrayLength - lastIndex, hint.nullableElements());

            if (elementResult.isDelayed()) {
                if (!isPrimitiveArray) {
                    // Origin not yet generated since its index is higher
                    if (delayedElementIndices == null) {
                        delayedElementIndices = new ArrayList<>();
                    }
                    delayedElementIndices.add(i);
                    continue;
                }
                return GeneratorResult.delayedResult();
            }

            Object elementValue = elementResult.getValue();

            // If elements are not nullable, keep generating until a non-null
            while (elementValue == null
                    && !hint.nullableElements()
                    && !elementResult.isIntentionalNull()
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

        if (delayedElementIndices != null) {
            final Object array = arrayObj;
            final boolean resolved = retryDelayedElements(
                    delayedElementIndices, node, elementNode,
                    lastIndex, arrayLength - lastIndex, hint.nullableElements(),
                    (index, value) -> {
                        // can't assign null values to primitive arrays
                        if (value != null) {
                            ApiValidator.validateValueIsAssignableToElementNode(
                                    "array element type mismatch", value, node, elementNode);
                            Array.set(array, index, value);
                        }
                    });
            if (!resolved) {
                return GeneratorResult.delayedResult();
            }
        }

        if (hint.shuffle()) {
            ArrayUtils.shuffle(arrayObj, context.getRandom());
        }

        assignmentObjectStore.clearCrossElementValuesFor(node);
        return GeneratorResult.resolved(arrayObj, hints);
    }

    @SuppressWarnings({
            "PMD.AvoidDeeplyNestedIfStmts",
            "PMD.CognitiveComplexity",
            "PMD.NcssCount",
            "PMD.NPathComplexity"
    })
    private GeneratorResult generateCollection(final InternalNode node) {
        final GeneratorResult generatorResult = generateValue(node);
        final InternalSize sizeOverride = context.getContainerSize(node);

        if (generatorResult.getValue() == null || node.getChildren().isEmpty()) {
            return generatorResult;
        }

        ApiValidator.validateValueIsAssignableToTargetClass(generatorResult.getValue(), Collection.class, node);

        //noinspection unchecked
        final Collection<Object> collection = (Collection<Object>) generatorResult.getValue();
        final InternalNode elementNode = node.getOnlyChild();
        final Hints hints = generatorResult.getHints();

        // Marked "used" so an empty or all-null List doesn't report these selectors as unused
        if (collection instanceof List) {
            context.markElementOfAssignmentSelectorsUsedForContainer(node);
        } else {
            // Fail fast since Sets have no element ordering, so cross-element assignment can't work
            final TargetSelector unsupported =
                    context.findFirstElementOfAssignmentSelectorForContainer(node);

            if (unsupported != null) {
                throw Fail.withUsageError(
                        ErrorMessageUtils.elementOfAssignmentNotSupportedOnNonIndexedCollection(
                                unsupported, node));
            }
        }

        // Populated objects that were created/added in the generator itself
        for (Object element : collection) {
            final List<InternalNode> elementNodeChildren = elementNode.getChildren();
            populateChildren(elementNodeChildren, GeneratorResult.resolved(element, hints));
        }

        if (elementNode.isStaticallyIgnored()) {
            return generatorResult;
        }

        final CollectionHint hint = defaultIfNull(hints.get(CollectionHint.class), CollectionHint.empty());
        final boolean nullableElements = hint.nullableElements();
        final boolean requireUnique = hint.unique();

        // withElements are appended afterwards, outside the elementOf() index space: they are
        // fixed values elementOf() must not overwrite (and frames resolve last()/range() against
        // the generated region only)
        final int elementsToGenerate = sizeOverride != null
                ? context.getRandom().intRange(sizeOverride.min(), sizeOverride.max())
                : hint.generateElements();

        final int targetSize = elementsToGenerateForElementOf(node,
                elementsToGenerate, collection.size(), hints, sizeOverride);

        final Set<Object> generated = new HashSet<>(targetSize);

        final int generatedSize = collection.size() + targetSize;
        int remaining = targetSize;
        int failedAdditions = 0;

        List<Integer> delayedElementIndices = null;

        while (remaining > 0) {
            final int currentIndex = collection.size();
            final GeneratorResult elementResult = createElementInFrame(
                    node, elementNode, currentIndex, generatedSize, nullableElements);

            if (elementResult.isDelayed()) {
                if (collection instanceof List) {
                    // Origin not yet generated (its index is higher); add a null placeholder, retry below
                    collection.add(null);
                    if (delayedElementIndices == null) delayedElementIndices = new ArrayList<>();
                    delayedElementIndices.add(currentIndex);
                    remaining--;
                    continue;
                }
                return GeneratorResult.delayedResult();
            }

            final Object elementValue = elementResult.getValue();

            if (elementValue != null || nullableElements || elementResult.isIntentionalNull()) {

                boolean canAdd = !requireUnique || !generated.contains(elementValue);

                if (requireUnique) {
                    generated.add(elementValue);
                }

                if (canAdd && collection.add(elementValue)) {
                    ApiValidator.validateValueIsAssignableToElementNode(
                            "error adding element to collection", elementValue, node, elementNode);

                    remaining--;

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
                                        context, node, targetSize));
                    });
                }
                break;
            }
        }

        if (delayedElementIndices != null) {
            final List<Object> list = (List<Object>) collection;
            final boolean resolved = retryDelayedElements(
                    delayedElementIndices, node, elementNode,
                    0, generatedSize, nullableElements, list::set);
            if (!resolved) {
                return GeneratorResult.delayedResult();
            }
        }

        if (!hint.withElements().isEmpty()) {
            collection.addAll(hint.withElements());
        }
        if (hint.shuffle()) {
            CollectionUtils.shuffle(collection, context.getRandom());
        }

        assignmentObjectStore.clearCrossElementValuesFor(node);
        return containerFactoriesHandler.substituteResult(node, generatorResult);
    }

    private int elementsToGenerateForElementOf(
            final InternalNode node,
            final int elementsToGenerate,
            final int collectionSize,
            final Hints hints,
            final @Nullable InternalSize sizeOverride) {

        final Integer explicitMax = explicitMaxSize(hints, sizeOverride);
        final int currentRegion = collectionSize + elementsToGenerate;
        final Integer explicitRegionMax = explicitMax == null ? null : explicitMax + collectionSize;
        final int requiredRegion = requiredRegionSize(node, currentRegion, explicitRegionMax);
        return requiredRegion - collectionSize;
    }

    private int requiredRegionSize(
            final InternalNode node,
            final int currentRegionSize,
            final @Nullable Integer explicitRegionMax) {

        final int requiredMin = context.getElementOfMinRequiredSize(node);
        if (requiredMin == 0 || requiredMin <= currentRegionSize) {
            return currentRegionSize;
        }
        if (explicitRegionMax != null && requiredMin > explicitRegionMax) {
            throw elementOfSizeExceedsExplicitSize(node, requiredMin, explicitRegionMax);
        }
        return requiredMin;
    }

    @Nullable
    private static Integer explicitMaxSize(final Hints hints, final @Nullable InternalSize sizeOverride) {
        if (sizeOverride != null) {
            return sizeOverride.max();
        }
        final InternalGeneratorHint hint = hints.get(InternalGeneratorHint.class);
        return hint == null ? null : hint.explicitMaxSize();
    }

    @FunctionalInterface
    private interface DelayedElementSetter {
        void set(int index, @Nullable Object value);
    }

    private boolean retryDelayedElements(
            final List<Integer> delayedIndices,
            final InternalNode containerNode,
            final InternalNode elementNode,
            final int regionOffset,
            final int regionSize,
            final boolean nullableElements,
            final DelayedElementSetter setter) {

        for (int delayedIndex : delayedIndices) {
            final GeneratorResult retryResult = createElementInFrame(
                    containerNode, elementNode, delayedIndex - regionOffset, regionSize, nullableElements);

            if (retryResult.isDelayed()) {
                return false;
            }
            setter.set(delayedIndex, retryResult.getValue());
        }
        return true;
    }

    private GeneratorResult createElementInFrame(
            final InternalNode containerNode,
            final InternalNode elementNode,
            final int index,
            final int containerSize,
            final boolean nullableElements) {

        elementFrameStack.push(containerNode, index, containerSize);
        assignmentObjectStore.enterScope();
        try {
            return createObject(elementNode, nullableElements);
        } finally {
            assignmentObjectStore.exitScope();
            elementFrameStack.pop();
        }
    }

    private InstancioApiException elementOfSizeExceedsExplicitSize(
            final InternalNode node, final int requiredMin, final int actualSize) {

        return Fail.withUsageError(
                """
                        elementOf() selector at index %s requires at least %s elements, but an explicit size of %s was set.
                        
                        %s
                        
                        To resolve this error:
                        
                         -> use a smaller index in the elementOf() selector
                         -> increase the size\
                        """,
                requiredMin - 1, requiredMin, actualSize, nodePathToRootBlock(node));
    }

    private GeneratorResult generateViaConstructor(
            final InternalNode node,
            final ConstructorDescriptor descriptor) {

        final Object spiInstance = instantiator.instantiateViaSpi(node.getTargetClass());
        if (spiInstance != null) {
            return storeAndPopulateAllChildren(
                    node, GeneratorResult.resolved(spiInstance, POPULATE_ALL_HINTS));
        }

        final @Nullable Object[] args = new Object[descriptor.getConstructorParameterNodes().size()];

        final Deque<DelayedConstructorComponentNode> delayedArgQueue = generateConstructorArguments(descriptor, args);

        // Only non-parameter entries are written to this map, and none can be
        // in the queue until preGenerateNonParameterChildren() has added them
        final Map<InternalNode, GeneratorResult> preGenerated = new IdentityHashMap<>();

        resolveDelayedComponents(delayedArgQueue, args, preGenerated);

        if (!delayedArgQueue.isEmpty()
                && !descriptor.getNonParameterChildren().isEmpty()
                && overwriteExistingValues) {

            preGenerateNonParameterChildren(descriptor.getNonParameterChildren(), delayedArgQueue, preGenerated);
            resolveDelayedComponents(delayedArgQueue, args, preGenerated);
        }

        // Constructor arguments themselves can't be delayed because
        // we need all of them at once to invoke the constructor.
        // Therefore, if an argument is unavailable, the entire node is delayed.
        if (!delayedArgQueue.isEmpty()) {
            delayedNodeQueue.addConstructorNode(node);
            return GeneratorResult.delayedResult();
        }

        delayedNodeQueue.removeConstructorNode(node);
        ObjectUtils.replaceNullArgsOfPrimitiveParameters(args, descriptor.getParameterTypes());
        final Object obj = invokeConstructor(node, descriptor, args);

        if (obj == null) {
            return generateViaInstantiator(node);
        }

        final Hints hints = node.is(NodeKind.RECORD) ? defaultAfterGenerateHints : POPULATE_ALL_HINTS;
        final GeneratorResult generatorResult = GeneratorResult.resolved(obj, hints);

        // A no-argument constructor produces a fully-formed object before any child
        // is populated, so it can be referenced by its own descendants. With a
        // value-passing constructor the object does not exist until its arguments
        // have been generated, therefore back-references to it are not possible.
        if (descriptor.getConstructorParameterNodes().isEmpty()) {
            generatorFacade.storeGeneratedPojo(node, generatorResult);
        }

        populateChildren(descriptor.getNonParameterChildren(), generatorResult, preGenerated);

        return generatorResult;
    }

    @SuppressWarnings("PMD.UseVarargs")
    private Deque<DelayedConstructorComponentNode> generateConstructorArguments(
            final ConstructorDescriptor descriptor,
            final @Nullable Object[] args) {

        final List<InternalNode> parameterNodes = descriptor.getConstructorParameterNodes();
        final Deque<DelayedConstructorComponentNode> delayedArgQueue = new ArrayDeque<>();

        for (int i = 0; i < args.length; i++) {
            final InternalNode parameterNode = parameterNodes.get(i);

            // An ignored parameter is left null; if it is primitive,
            // the null is replaced with a default value before invocation
            if (context.isEffectivelyIgnored(parameterNode)) {
                continue;
            }

            final GeneratorResult result = createObject(parameterNode);

            if (result.isDelayed()) {
                LOG.trace("Delayed constructor arg: {}", parameterNode);
                delayedArgQueue.add(new DelayedConstructorComponentNode(parameterNode, i));
            } else {
                args[i] = result.getValue();
            }
        }
        return delayedArgQueue;
    }

    private void resolveDelayedComponents(
            final Deque<DelayedConstructorComponentNode> delayedComponentQueue,
            final @Nullable Object[] args,
            final Map<InternalNode, GeneratorResult> preGenerated) {

        int threshold = delayedComponentQueue.size();

        while (!delayedComponentQueue.isEmpty()) {
            final DelayedConstructorComponentNode entry = delayedComponentQueue.removeLast();
            final GeneratorResult result = createObject(entry.node());

            LOG.trace("Attempt to create delayed constructor component: {}", entry.node());

            if (result.isDelayed()) {
                threshold--;
                delayedComponentQueue.addFirst(entry);

            } else if (!result.isUnresolved()) {
                if (entry.isNonParameterField()) {
                    preGenerated.put(entry.node(), result);
                } else {
                    args[entry.argIndex()] = result.getValue();
                }
            }
            if (threshold == 0) {
                break;
            }
        }
    }

    private void preGenerateNonParameterChildren(
            final List<InternalNode> nonParameterChildren,
            final Deque<DelayedConstructorComponentNode> delayedComponentQueue,
            final Map<InternalNode, GeneratorResult> preGenerated) {

        for (InternalNode child : nonParameterChildren) {
            // Caller guarantees overwriteExistingValues is true,
            // so NodeFilter skips its existing-value check.
            // The owner is never read and is null since it doesn't exist yet
            final NodeFilterResult filterResult = nodeFilter.filter(
                    child, AfterGenerate.POPULATE_ALL, /*owner=*/ null);

            if (filterResult != NodeFilterResult.GENERATE || context.isEffectivelyIgnored(child)) {
                continue;
            }

            final GeneratorResult result = createObject(child);

            if (result.isDelayed()) {
                LOG.trace("Delayed non-parameter field: {}", child);
                delayedComponentQueue.add(DelayedConstructorComponentNode.forNonParameterField(child));
            } else if (!result.isUnresolved()) {
                preGenerated.put(child, result);
            }
        }
    }

    @Nullable
    @SuppressWarnings("PMD.UseVarargs")
    private Object invokeConstructor(
            final InternalNode node,
            final ConstructorDescriptor descriptor,
            final @Nullable Object[] args) {

        try {
            return descriptor.getConstructor().newInstance(args);
        } catch (Exception ex) {
            // Wrong type is being passed to a constructor parameter.
            // Always propagate type mismatch errors as it's most likely a user error.
            // Without this check, the error would either be reported as an internal
            // error (for records) or silently swallowed by the constructor fallback.
            failIfArgumentTypeMismatch(descriptor, args, ex);

            if (node.is(NodeKind.RECORD)) {
                throw Fail.withInternalError("Error instantiating: %s", node, ex);
            }
            if (onConstructorError == OnConstructorError.FAIL) {
                throw Fail.withUsageError(ErrorMessageUtils.errorInvokingConstructor(
                        node, descriptor.getConstructor()), ex);
            }
            Log.msg(Log.Category.CONSTRUCTOR_FALLBACK,
                    "Error instantiating {} via constructor (configurable via '{}'). "
                            + "Falling back to creating the object without a constructor.",
                    node.getTargetClass(), Keys.ON_CONSTRUCTOR_ERROR.propertyKey());
            return null;
        }
    }

    /**
     * Throws a usage error if any of the {@code args} cannot be assigned
     * to the corresponding constructor parameter. Does nothing if all the
     * arguments are compatible, in which case the constructor must have
     * failed for some other reason.
     */
    private static void failIfArgumentTypeMismatch(
            final ConstructorDescriptor descriptor,
            final @Nullable Object[] args,
            final Exception ex) {

        final List<Class<?>> parameterTypes = descriptor.getParameterTypes();
        final List<InternalNode> parameterNodes = descriptor.getConstructorParameterNodes();

        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];

            if (arg != null && !PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(
                    parameterTypes.get(i), arg.getClass())) {

                final String msg = ErrorMessageUtils.getTypeMismatchErrorMessage(arg, parameterNodes.get(i), ex);
                throw Fail.withUsageError(msg, ex);
            }
        }
    }

    private GeneratorResult generateViaInstantiator(final InternalNode node) {
        final GeneratorResult generatorResult = instantiateTargetClassOf(node);
        populateChildren(node.getChildren(), generatorResult);
        return generatorResult;
    }

    private GeneratorResult instantiateTargetClassOf(final InternalNode node) {
        if (node.is(NodeKind.RECORD) && !node.getChildren().isEmpty()) {
            LOG.trace("{} has no resolved constructor - returning a null result", node);
            return GeneratorResult.nullResult();
        }

        final Class<?> targetClass = node.getTargetClass();

        if (ReflectionUtils.isArrayOrConcrete(targetClass)) {
            final Object object = instantiator.instantiate(targetClass);
            return GeneratorResult.resolved(object, POPULATE_ALL_HINTS);
        }

        return GeneratorResult.unresolvedResult();
    }

    private void populateChildren(
            final List<InternalNode> children,
            final GeneratorResult generatorResult) {

        populateChildren(children, generatorResult, Collections.emptyMap());
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void populateChildren(
            final List<InternalNode> children,
            final GeneratorResult generatorResult,
            final Map<InternalNode, GeneratorResult> preGenerated) {

        if (generatorResult.getValue() == null || children.isEmpty()) {
            return;
        }

        final Object parentObject = generatorResult.getValue();
        final Hints hints = generatorResult.getHints();
        final AfterGenerate action = requireNonNull(hints.afterGenerate());
        final Assigner assigner = assignerResolver.resolve(generatorResult);

        for (final InternalNode child : children) {
            if (context.isEffectivelyIgnored(child)) {
                continue;
            }

            final NodeFilterResult filterResult = nodeFilter.filter(child, action, parentObject);

            if (filterResult == NodeFilterResult.GENERATE) {
                final GeneratorResult preGeneratedResult = preGenerated.get(child);

                final GeneratorResult result = preGeneratedResult == null
                        ? createObject(child)
                        : preGeneratedResult;

                if (result.isDelayed()) {
                    delayedNodeQueue.addLast(new DelayedNode(child, generatorResult, elementFrameStack.peek()));
                } else {
                    assignValue(parentObject, child, result, assigner);
                }
                continue;
            }

            // Check if this field was initialised externally
            final Object childObject = ReflectionUtils.tryGetFieldValueOrElseNull(child.getField(), parentObject);

            if (childObject == null) {
                continue;
            }

            final GeneratorResult childResult = GeneratorResult.resolved(childObject, hints);

            // Add field value to the object store.
            // This allows fields initialised externally to work with assign()
            assignmentObjectStore.objectCreated(child, childResult);

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

        if (generatorResult.isUnresolved()) {
            return generatorResult;
        }

        final InternalContainerHint hint = defaultIfNull(
                generatorResult.getHints().get(InternalContainerHint.class),
                InternalContainerHint.empty());

        final List<InternalNode> children = node.getChildren();

        final ContainerCreateFunction<Object> createFunction = hint.createFunction();

        // Creation delegated to the engine
        if (generatorResult.getValue() == null && createFunction != null) {
            final @Nullable Object[] args = new Object[children.size()];
            for (int i = 0; i < children.size(); i++) {
                final InternalNode childNode = children.get(i);
                final GeneratorResult childResult = createObject(childNode);

                if (childResult.isDelayed()) {
                    return GeneratorResult.delayedResult();
                }

                ApiValidator.validateValueIsAssignableToElementNode(
                        "error populating object due to incompatible types",
                        childResult.getValue(), node, childNode);

                args[i] = childResult.getValue();
            }

            final Object result = createFunction.create(args);
            generatorResult = GeneratorResult.resolved(result, generatorResult.getHints());
        }

        if (generatorResult.getValue() == null) {
            return generatorResult;
        }

        final InternalSize sizeOverride = context.getContainerSize(node);
        final ContainerAddFunction<Object> addFunction = hint.addFunction();

        if (addFunction != null) {
            int remaining = sizeOverride != null
                    ? context.getRandom().intRange(sizeOverride.min(), sizeOverride.max())
                    : hint.generateEntries();

            for (int i = 0; i < remaining; i++) {
                final @Nullable Object[] args = new Object[children.size()];

                assignmentObjectStore.enterScope();
                for (int j = 0; j < children.size(); j++) {
                    final GeneratorResult childResult = createObject(children.get(j));
                    args[j] = childResult.getValue();
                }
                assignmentObjectStore.exitScope();
                addFunction.addTo(generatorResult.getValue(), args);
            }
        }

        final ContainerBuildFunction<Object, Object> buildFunction = hint.buildFunction();
        if (buildFunction != null) {
            final Object builtContainer = buildFunction.build(generatorResult.getValue());
            return GeneratorResult.resolved(builtContainer, generatorResult.getHints());
        }

        return containerFactoriesHandler.substituteResult(node, generatorResult);
    }

    private static void assignValue(
            final Object parentResult,
            final InternalNode node,
            final GeneratorResult result,
            final Assigner assigner) {

        if (!result.isUnresolved()) {
            assigner.assign(node, parentResult, result.getValue());
        }
    }

    private GeneratorResult generateValue(final InternalNode node) {
        return generatorFacade.generateNodeValue(node);
    }

    private void notifyListeners(final InternalNode node, final GeneratorResult result) {
        if (result.isResolved() || result.isNull()) {
            for (GenerationListener listener : listeners) {
                listener.objectCreated(node, result);
            }
        }
    }
}
