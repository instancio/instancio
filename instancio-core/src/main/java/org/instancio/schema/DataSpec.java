/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.schema;

import org.instancio.documentation.ExperimentalApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows mapping a property name from an external
 * data source to a spec method in a {@link Schema}.
 *
 * @since 5.0.0
 */
@ExperimentalApi
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSpec {

    /**
     * Specifies the name of a property that a spec method
     * in a {@link Schema} should map to.
     *
     * @return property name defined in an external data source
     * @since 5.0.0
     */
    @ExperimentalApi
    String propertyName();
}
