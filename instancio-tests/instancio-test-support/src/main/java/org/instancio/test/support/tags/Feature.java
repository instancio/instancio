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
    ARRAY_GENERATOR_NULLABLE_ELEMENTS,
    ARRAY_GENERATOR_SHUFFLE,
    ARRAY_GENERATOR_SUBTYPE,
    ARRAY_GENERATOR_WITH,
    /**
     * Tests for {@code assign()} API.
     */
    ASSIGN,
    /**
     * Tests for {@code AssignmentType} setting.
     */
    ASSIGNMENT_TYPE,
    AS_GENERATOR_SPEC,
    AS_STRING_GENERATOR_SPEC,
    ATOMIC_GENERATOR,
    BEAN_VALIDATION, // JSR-380
    COLLECTION_GENERATOR_MAX_SIZE,
    COLLECTION_GENERATOR_MIN_SIZE,
    COLLECTION_GENERATOR_NULLABLE,
    COLLECTION_GENERATOR_NULLABLE_ELEMENTS,
    COLLECTION_GENERATOR_SHUFFLE,
    COLLECTION_GENERATOR_SIZE,
    COLLECTION_GENERATOR_SUBTYPE,
    COLLECTION_GENERATOR_UNIQUE,
    COLLECTION_GENERATOR_WITH_ELEMENTS,
    CONTAINER_GENERATOR,
    CSV_GENERATOR,
    CYCLIC,
    EMIT_GENERATOR,
    ENUM_GENERATOR,
    FILE_GENERATOR,
    GENERATE,  // API generate() method
    GENERATOR,
    GENERATOR_SPEC_NULLABLE,
    GLOBAL_SEED,
    IGNORE,
    INHERITANCE,
    LOREM_IPSUM_GENERATOR,
    MAP_GENERATOR_MAX_SIZE,
    MAP_GENERATOR_MIN_SIZE,
    MAP_GENERATOR_NULLABLE,
    MAP_GENERATOR_NULLABLE_KEYS,
    MAP_GENERATOR_NULLABLE_VALUES,
    MAP_GENERATOR_SIZE,
    MAP_GENERATOR_SUBTYPE,
    MAP_GENERATOR_WITH_ENTRIES,
    MAP_GENERATOR_WITH_KEYS,
    MATH_GENERATOR,
    MAX_DEPTH,
    METHOD_REFERENCE_SELECTOR,
    MODE,
    MODEL,
    NULLABILITY, // catch-all for all nullability rules
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
    DEPTH_SELECTOR,
    SELECTOR_PRECEDENCE,
    SET,
    SETTINGS,
    STREAM,
    STRING_FIELD_PREFIX,
    STRING_GENERATOR,
    SUBTYPE,
    SUPPLY,
    TEMPORAL_GENERATOR,
    TEXT_PATTERN_GENERATOR,
    TO_SCOPE,
    UNSUPPORTED,
    URI_GENERATOR,
    URL_GENERATOR,
    UUID_STRING_GENERATOR,
    VALIDATION,
    VALUE_SPEC,
    WITH_NULLABLE,
    WITH_SEED,
    WITH_SEED_ANNOTATION,
    WITH_SETTINGS_ANNOTATION,
    WITH_TYPE_PARAMETERS
}