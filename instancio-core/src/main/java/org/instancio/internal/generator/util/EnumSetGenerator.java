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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.specs.EnumSetGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.Sonar;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class EnumSetGenerator<E extends Enum<E>> extends AbstractGenerator<Set<E>> implements EnumSetGeneratorSpec<E> {

    private final Class<E> enumClass;
    private final int generateEntriesHint;
    private Integer minSize = 1;
    private Integer maxSize;
    private Set<E> including;
    private Set<E> excluding;

    public EnumSetGenerator(final GeneratorContext context, final Class<E> enumClass) {
        super(context);
        this.enumClass = ApiValidator.notNull(enumClass, "Enum class must not be null");
        // engine should not add elements
        this.generateEntriesHint = 0;
    }

    @Override
    public String apiMethod() {
        return "enumSet()";
    }

    public EnumSetGenerator(final GeneratorContext context) {
        super(context);
        this.enumClass = null;
        // Without knowing the enum class size cannot be determined, so just default to 1
        this.generateEntriesHint = 1;
    }

    @Override
    public EnumSetGeneratorSpec<E> size(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = size;
        return this;
    }

    @Override
    public EnumSetGeneratorSpec<E> minSize(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = NumberUtils.calculateNewMaxSize(maxSize, minSize);
        return this;
    }

    @Override
    public EnumSetGeneratorSpec<E> maxSize(final int size) {
        this.maxSize = ApiValidator.validateSize(size);
        this.minSize = NumberUtils.calculateNewMinSize(minSize, maxSize);
        return this;
    }

    @SafeVarargs
    @Override
    public final EnumSetGeneratorSpec<E> of(final E... elements) {
        this.including = EnumSet.copyOf(Arrays.asList(elements));
        return this;
    }

    @Override
    @SafeVarargs
    public final EnumSetGeneratorSpec<E> excluding(final E... elements) {
        this.excluding = EnumSet.copyOf(Arrays.asList(elements));
        return this;
    }

    @Override
    @SuppressWarnings({"PMD.ReturnEmptyCollectionRatherThanNull", Sonar.RETURN_EMPTY_COLLECTION})
    protected Set<E> tryGenerateNonNull(final Random random) {
        // If enum class is known at this time (i.e. it was supplied by the user via the spec)
        // then generate an EnumSet internally.
        if (enumClass != null) {
            return createEnumSet(enumClass, random);
        }

        // Return null in order to delegate creating an EnumSet to the engine.
        return null;
    }

    private Set<E> createEnumSet(final Class<E> targetClass, final Random random) {

        if (CollectionUtils.isNullOrEmpty(including) && CollectionUtils.isNullOrEmpty(excluding)) {
            final Set<E> choices = EnumSet.allOf(enumClass);
            final int min = ObjectUtils.defaultIfNull(minSize, 1);
            final int max = ObjectUtils.defaultIfNull(maxSize, choices.size());
            final int size = random.intRange(min, max);
            final Set<E> result = EnumSet.noneOf(targetClass);

            while (result.size() < size && !choices.isEmpty()) {
                final E next = random.oneOf(choices);
                result.add(next);
                choices.remove(next);
            }
            return result;
        }

        if (!CollectionUtils.isNullOrEmpty(including)) {
            final int min = ObjectUtils.defaultIfNull(minSize, 1);
            final int max = ObjectUtils.defaultIfNull(maxSize, including.size());
            final int size = random.intRange(min, max);

            final Set<E> result = EnumSet.noneOf(targetClass);
            final Set<E> choices = EnumSet.copyOf(including);

            while (result.size() < size && !choices.isEmpty()) {
                final E next = random.oneOf(choices);
                result.add(next);
                choices.remove(next);
            }
            return result;
        }

        Set<E> result = EnumSet.allOf(targetClass);

        if (excluding != null) {
            excluding.forEach(result::remove);
        }

        while (maxSize != null && result.size() > maxSize) {
            result.remove(result.iterator().next());
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Hints hints() {
        return Hints.builder()
                .with(InternalContainerHint.builder()
                        .generateEntries(generateEntriesHint)
                        .createFunction(args -> EnumSet.noneOf((Class<E>) args[0].getClass()))
                        .addFunction((Set<E> enumSet, Object... args) -> enumSet.add((E) args[0]))
                        .build())
                .build();
    }

}
