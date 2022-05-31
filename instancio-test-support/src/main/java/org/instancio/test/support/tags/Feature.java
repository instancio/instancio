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
package org.instancio.test.support.tags;

/**
 * An enum for the {@link FeatureTag}. Used for tracking (untested) features.
 */
public enum Feature {
    ARRAY_GENERATOR_MIN_LENGTH,
    ARRAY_GENERATOR_MAX_LENGTH,
    ARRAY_GENERATOR_LENGTH,
    ARRAY_GENERATOR_TYPE,
    ARRAY_GENERATOR_NULLABLE,
    ARRAY_GENERATOR_NULLABLE_ELEMENT,
    ARRAY_GENERATOR_WITH,

    COLLECTION_GENERATOR_MIN_SIZE,
    COLLECTION_GENERATOR_MAX_SIZE,
    COLLECTION_GENERATOR_SIZE,
    COLLECTION_GENERATOR_TYPE,
    COLLECTION_GENERATOR_NULLABLE,
    COLLECTION_GENERATOR_NULLABLE_ELEMENT,
    COLLECTION_GENERATOR_WITH,

    MAP_GENERATOR_MIN_SIZE,
    MAP_GENERATOR_MAX_SIZE,
    MAP_GENERATOR_SIZE,
    MAP_GENERATOR_TYPE,
    MAP_GENERATOR_NULLABLE,
    MAP_GENERATOR_NULLABLE_KEY,
    MAP_GENERATOR_NULLABLE_VALUE,

    ONE_OF_ARRAY_GENERATOR,
    ONE_OF_COLLECTION_GENERATOR,
    STRING_GENERATOR,
    ATOMIC_GENERATOR,
    MATH_GENERATOR,
    TEMPORAL_GENERATOR,
    TEXT_PATTERN_GENERATOR,

    SELECTOR,
    SCOPE,

    WITH_SEED,
    WITH_SEED_ANNOTATION,

    WITH_SETTINGS,
    WITH_SETTINGS_ANNOTATION,

    IGNORE,
    NULLABLE,

    SET,
    SUPPLY,
    SUPPLY_WITH_RANDOM,
    GENERATE,
    ON_COMPLETE,
    SUBTYPE,

    CYCLIC,
    INHERITANCE,

    MODE,
    VALIDATION,

    MODEL,
    METAMODEL,

    STREAM,

    UNSUPPORTED
}