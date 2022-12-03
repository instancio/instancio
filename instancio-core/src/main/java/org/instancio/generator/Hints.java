/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator;

import org.instancio.internal.ApiValidator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

/**
 * Hints are provided by {@link Generator} implementations to the engine to
 * support more flexibility in how objects are populated.
 *
 * @see Generator
 * @see PopulateAction
 * @since 1.7.0
 */
public final class Hints {

    private static final Hints DEFAULT_HINTS = Hints.builder()
            .populateAction(PopulateAction.APPLY_SELECTORS)
            .build();

    private final PopulateAction populateAction;
    private final Map<Class<?>, Object> hintMap;

    private Hints(final Builder builder) {
        populateAction = builder.populateAction;
        hintMap = defaultIfNull(builder.hintMap, Collections.emptyMap());
    }

    /**
     * Returns an instance of default hints that contain {@link #populateAction}
     * set to {@link PopulateAction#APPLY_SELECTORS}.
     *
     * @return an instance containing a single hint {@link PopulateAction#APPLY_SELECTORS}
     * @see PopulateAction
     * @since 1.7.0
     */
    public static Hints defaultHints() {
        return DEFAULT_HINTS;
    }

    /**
     * Returns an instance of hints containing the specified action.
     *
     * @param populateAction the populate action
     * @return an instance containing a single hint {@link PopulateAction#NULLS}
     * @see PopulateAction
     * @since 1.7.0
     */
    public static Hints withPopulateAction(final PopulateAction populateAction) {
        return Hints.builder().populateAction(populateAction).build();
    }

    public static Builder builder(final Hints copy) {
        Builder builder = new Builder();
        builder.populateAction = copy.populateAction;
        builder.hintMap = copy.hintMap;
        return builder;
    }

    /**
     * Returns the action to be performed by the engine.
     *
     * @return populate action to perform
     * @see PopulateAction
     * @since 1.7.0
     */
    public PopulateAction populateAction() {
        return populateAction;
    }

    /**
     * Returns a hint with the specified type.
     *
     * @param hintType type of the hint
     * @param <T>      hint type
     * @return hint with the specified type, or {@code null} if none found
     * @since 1.7.0
     */
    public <T extends Hint<T>> T get(final Class<T> hintType) {
        ApiValidator.notNull(hintType, "Hint type must not be null");
        return hintType.cast(hintMap.get(hintType));
    }

    /**
     * Returns an instance of the builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing {@link Hints}.
     */
    public static final class Builder {
        private PopulateAction populateAction;
        private Map<Class<?>, Object> hintMap;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Adds specified hint object.
         *
         * <p>The hint's type acts as the key, therefore each hint type
         * can be added only once. Adding another hint with same type
         * will replace the previous hint.</p>
         *
         * @param hint to add
         * @param <T>  hint type
         * @return builder instance
         * @see Hint
         * @since 1.7.0
         */
        public <T extends Hint<T>> Builder hint(final T hint) {
            ApiValidator.notNull(hint, "Hint must not be null");
            if (hintMap == null) {
                hintMap = new HashMap<>();
            }
            hintMap.put(hint.type(), hint);
            return this;
        }

        /**
         * A hint indicating what should be done with the object created by the
         * generator. If not specified, the default action is
         * {@link PopulateAction#APPLY_SELECTORS}, which implies the engine
         * will not modify the created instance.
         *
         * <p>For most common use cases, custom generators
         * will use one of the following:</p>
         *
         * <ul>
         *   <li>{@link PopulateAction#APPLY_SELECTORS}</li>
         *   <li>{@link PopulateAction#NULLS}</li>
         *   <li>{@link PopulateAction#NULLS_AND_DEFAULT_PRIMITIVES}</li>
         * </ul>
         *
         * <p><b>Note:</b> the populate action hint is not applicable to
         * generators that produce {@code java.lang.Record} objects. Since records
         * are immutable, they cannot be modified after instantiation.</p>
         *
         * @param populateAction action to be taken by the engine after
         *                       object has been created by the generator
         * @return builder instance
         * @see PopulateAction
         * @since 1.7.0
         */
        public Builder populateAction(final PopulateAction populateAction) {
            this.populateAction = ApiValidator.notNull(
                    populateAction, "Populate action must not be null");
            return this;
        }

        public Builder hintMap(final Map<Class<?>, Object> hintMap) {
            this.hintMap = hintMap;
            return this;
        }

        /**
         * Builds the object.
         *
         * @return the built instance.
         */
        public Hints build() {
            return new Hints(this);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Hints[", "]")
                .add("populateAction=" + populateAction)
                .add("hints=" + hintMap)
                .toString();
    }
}
