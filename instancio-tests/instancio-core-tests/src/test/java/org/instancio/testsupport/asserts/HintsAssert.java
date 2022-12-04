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
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.generator.hints.DataStructureHint;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class HintsAssert extends AbstractAssert<HintsAssert, Hints> {

    private HintsAssert(Hints actual) {
        super(actual, HintsAssert.class);
    }

    public static HintsAssert assertHints(Hints actual) {
        return new HintsAssert(actual);
    }

    public HintsAssert dataStructureSize(int expected) {
        assertThat(actual.get(DataStructureHint.class).dataStructureSize()).isEqualTo(expected);
        return this;
    }

    public HintsAssert dataStructureSizeBetween(int min, int max) {
        assertThat(actual.get(DataStructureHint.class).dataStructureSize()).isBetween(min, max);
        return this;
    }

    public HintsAssert populateAction(PopulateAction expected) {
        assertThat(actual.populateAction()).isEqualTo(expected);
        return this;
    }

    public HintsAssert populateActionIsNone() {
        return populateAction(PopulateAction.NONE);
    }

    public HintsAssert nullableElements(boolean expected) {
        assertThat(actual.get(DataStructureHint.class).nullableElements()).isEqualTo(expected);
        return this;
    }

    public HintsAssert nullableMapKeys(boolean expected) {
        assertThat(actual.get(DataStructureHint.class).nullableMapKeys()).isEqualTo(expected);
        return this;
    }

    public HintsAssert nullableMapValues(boolean expected) {
        assertThat(actual.get(DataStructureHint.class).nullableMapValues()).isEqualTo(expected);
        return this;
    }
}