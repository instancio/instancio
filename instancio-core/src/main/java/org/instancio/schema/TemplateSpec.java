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
 * A template spec is for producing string values
 * based on a string template with inputs provided by other specs.
 *
 * @see Schema
 * @since 5.0.0
 */
@ExperimentalApi
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateSpec {

    /**
     * Specifies a string template that uses <b>{@code ${}}</b>
     * syntax for placeholders. The placeholder refers to
     * spec properties defined by a schema.
     *
     * <p>Example:
     * <pre>{@code
     * @SchemaResource(path = "sample.csv")
     * interface SampleSchema extends Schema {
     *
     *     ValueSpec<String> firstName();
     *
     *     ValueSpec<String> lastName();
     *
     *     @TemplateSpec("Hello ${firstName} ${lastName}!")
     *     ValueSpec<String> greeting();
     * }
     * }</pre>
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    String value();
}
