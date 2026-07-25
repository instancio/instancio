/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.instantiation;

import org.instancio.settings.InstantiationStrategies;

import static org.instancio.settings.InstantiationStrategy.ALL_ARGS;
import static org.instancio.settings.InstantiationStrategy.BYPASS_CONSTRUCTOR;
import static org.instancio.settings.InstantiationStrategy.NO_ARGS;

/**
 * Constants shared by the instantiation tests in this package.
 */
final class ConstructorTestSupport {

    static final String CTOR_PREFIX = "CTOR_";
    static final String SETTER_PREFIX = "SETTER_";

    /**
     * Every strategy, with the value-passing one first, so a value-passing
     * constructor is used whenever one can be resolved at all.
     */
    static final InstantiationStrategies ALWAYS = InstantiationStrategies.of(
            ALL_ARGS, NO_ARGS, BYPASS_CONSTRUCTOR);

    /**
     * No value-passing strategy, so generated values are never passed
     * to a constructor.
     */
    static final InstantiationStrategies NEVER = InstantiationStrategies.of(
            NO_ARGS, BYPASS_CONSTRUCTOR);

    private ConstructorTestSupport() {
        // non-instantiable
    }
}
