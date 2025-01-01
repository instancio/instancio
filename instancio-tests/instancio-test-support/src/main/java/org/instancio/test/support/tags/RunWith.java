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
package org.instancio.test.support.tags;

import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * These annotations are for tests that must be run using
 * either FIELD or METHOD assignment, but not both.
 *
 * <p>The feature-tests suite is executed twice: using field and method
 * assignments. This is done by setting assignment type via a system property.
 * There are certain tests, however, that can only be executed using either
 * field or method assignment, but not both.
 */
public interface RunWith {

    @DisabledIfSystemProperty(named = "instancio.assignmentType", matches = "FIELD")
    @Retention(RetentionPolicy.RUNTIME)
    @interface MethodAssignmentOnly {
    }

    @DisabledIfSystemProperty(named = "instancio.assignmentType", matches = "METHOD")
    @Retention(RetentionPolicy.RUNTIME)
    @interface FieldAssignmentOnly {
    }

}
