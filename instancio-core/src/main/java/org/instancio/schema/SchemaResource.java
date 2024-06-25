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

import org.instancio.InstancioOfSchemaApi;
import org.instancio.documentation.ExperimentalApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for specifying data for a {@link Schema}.
 *
 * <p>The sources of data can be (in order of precedence
 * starting from highest):
 *
 * <ul>
 *   <li>inline data specified via {@link SchemaResource#data()}</li>
 *   <li>custom {@link DataSource} specified via the
 *      {@link InstancioOfSchemaApi#withDataSource(DataSource)}
 *   </li>
 *   <li>file specified via {@link SchemaResource#path()}</li>
 * </ul>
 *
 * @since 5.0.0
 */
@ExperimentalApi
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SchemaResource {

    /**
     * Specifies inline data for a schema.
     *
     * @return the data for a schema as a string
     * @since 5.0.0
     */
    @ExperimentalApi
    String data() default "";

    /**
     * Specifies a file containing the data for a schema.
     *
     * @return path to a data file
     * @since 5.0.0
     */
    @ExperimentalApi
    String path() default "";

    /**
     * Specifies the name of a property to use as the tag key.
     *
     * @return property that acts as a tag.
     * @since 5.0.0
     */
    @ExperimentalApi
    String tagKey() default "";
}
