/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public final class HibernateValidatorUtil {

    @SuppressWarnings("resource")
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void assertValid(final Object obj) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        assertThat(violations)
                .as("Object '%s' has validation errors: %s", obj, violations)
                .isEmpty();
    }

    private HibernateValidatorUtil() {
        // non-instantiable
    }
}
