/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.generator.GeneratedHints;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class GeneratedHintsAssert extends AbstractAssert<GeneratedHintsAssert, GeneratedHints> {

    private GeneratedHintsAssert(GeneratedHints actual) {
        super(actual, GeneratedHintsAssert.class);
        assertThat(actual).isNotNull();
    }

    public static GeneratedHintsAssert assertHints(GeneratedHints actual) {
        return new GeneratedHintsAssert(actual);
    }

    public GeneratedHintsAssert dataStructureSize(int expected) {
        assertThat(actual.getDataStructureSize()).isEqualTo(expected);
        return this;
    }

    public GeneratedHintsAssert dataStructureSizeBetween(int min, int max) {
        assertThat(actual.getDataStructureSize()).isBetween(min, max);
        return this;
    }

    public GeneratedHintsAssert ignoreChildren(boolean expected) {
        assertThat(actual.ignoreChildren()).isEqualTo(expected);
        return this;
    }

    public GeneratedHintsAssert nullableResult(boolean expected) {
        assertThat(actual.nullableResult()).isEqualTo(expected);
        return this;
    }

    public GeneratedHintsAssert nullableElements(boolean expected) {
        assertThat(actual.nullableElements()).isEqualTo(expected);
        return this;
    }

    public GeneratedHintsAssert nullableMapKeys(boolean expected) {
        assertThat(actual.nullableMapKeys()).isEqualTo(expected);
        return this;
    }

    public GeneratedHintsAssert nullableMapValues(boolean expected) {
        assertThat(actual.nullableMapValues()).isEqualTo(expected);
        return this;
    }
}