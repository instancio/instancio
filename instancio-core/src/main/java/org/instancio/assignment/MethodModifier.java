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
package org.instancio.assignment;

import org.instancio.documentation.ExperimentalApi;

import java.lang.reflect.Modifier;

/**
 * A class containing modifier constants.
 *
 * <p>This class contains a subset of method modifiers from {@link Modifier},
 * with the addition of a custom {@link #PACKAGE_PRIVATE} modifier
 * to represent package-private methods.
 *
 * @since 2.16.0
 */
@ExperimentalApi
public final class MethodModifier {

    /**
     * Modifier for package-private method.
     *
     * <p>This is a custom value specific to Instancio.
     * It is not defined in the {@link Modifier} class.
     */
    public static final int PACKAGE_PRIVATE = 0x00008000;

    /**
     * Modifier for {@code private} method.
     */
    public static final int PRIVATE = Modifier.PRIVATE;

    /**
     * Modifier for {@code protected} method.
     */
    public static final int PROTECTED = Modifier.PROTECTED;

    /**
     * Modifier for {@code public} method.
     */
    public static final int PUBLIC = Modifier.PUBLIC;

    /**
     * Modifier for {@code static} method.
     */
    public static final int STATIC = Modifier.STATIC;

    private MethodModifier() {
        // non-instantiable
    }
}
