/*
 *  Copyright 2022-2023 the original author or authors.
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
    AFTER_GENERATE,
    ARRAY_GENERATOR_LENGTH,
    ARRAY_GENERATOR_MAX_LENGTH,
    ARRAY_GENERATOR_MIN_LENGTH,
    ARRAY_GENERATOR_NULLABLE,
    ARRAY_GENERATOR_NULLABLE_ELEMENT,
    ARRAY_GENERATOR_SHUFFLE,
    ARRAY_GENERATOR_SUBTYPE,
    ARRAY_GENERATOR_WITH,
    ASSIGNMENT,
    AS_STRING_GENERATOR_SPEC,
    ATOMIC_GENERATOR,
    COLLECTION_GENERATOR_MAX_SIZE,
    COLLECTION_GENERATOR_MIN_SIZE,
    COLLECTION_GENERATOR_NULLABLE,
    COLLECTION_GENERATOR_NULLABLE_ELEMENT,
    COLLECTION_GENERATOR_SHUFFLE,
    COLLECTION_GENERATOR_SIZE,
    COLLECTION_GENERATOR_SUBTYPE,
    COLLECTION_GENERATOR_WITH_ELEMENTS,
    CONTAINER_GENERATOR,
    CYCLIC,
    ENUM_GENERATOR,
    FILE_GENERATOR,
    GENERATE,  // API generate() method
    GENERATOR,
    GLOBAL_SEED,
    IGNORE,
    INHERITANCE,
    LOREM_IPSUM_GENERATOR,
    MAP_GENERATOR_MAX_SIZE,
    MAP_GENERATOR_MIN_SIZE,
    MAP_GENERATOR_NULLABLE,
    MAP_GENERATOR_NULLABLE_KEY,
    MAP_GENERATOR_NULLABLE_VALUE,
    MAP_GENERATOR_SIZE,
    MAP_GENERATOR_SUBTYPE,
    MAP_GENERATOR_WITH_ENTRIES,
    MAP_GENERATOR_WITH_KEYS,
    MATH_GENERATOR,
    METAMODEL,
    MODE,
    MODEL,
    NULLABLE,
    OF_LIST,
    OF_MAP,
    OF_SET,
    ONE_OF_ARRAY_GENERATOR,
    ONE_OF_COLLECTION_GENERATOR,
    ON_COMPLETE,
    OVERWRITE_EXISTING_VALUES,
    PATH_GENERATOR,
    PREDICATE_SELECTOR,
    ROOT_SELECTOR,
    SCOPE,
    SELECTOR,
    SELECTOR_PRECEDENCE,
    SET,
    SETTINGS,
    STREAM,
    STRING_GENERATOR,
    SUBTYPE,
    SUPPLY,
    TEMPORAL_GENERATOR,
    TEXT_PATTERN_GENERATOR,
    TO_SCOPE,
    UNSUPPORTED,
    UUID_STRING_GENERATOR,
    VALIDATION,
    WITH_SEED,
    WITH_SEED_ANNOTATION,
    WITH_SETTINGS_ANNOTATION,
    WITH_TYPE_PARAMETERS
}