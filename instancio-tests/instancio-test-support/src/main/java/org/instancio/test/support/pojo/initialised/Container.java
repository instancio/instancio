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
package org.instancio.test.support.pojo.initialised;

import lombok.Data;
import org.assertj.core.api.Condition;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.conditions.Conditions.RANDOM_INTEGER;
import static org.instancio.test.support.conditions.Conditions.RANDOM_STRING;

/**
 * Defines POJOs with initialised fields using the following naming convention:
 * <p>
 * {@code *1} uninitialised fields
 * {@code *2} initialised fields
 */
public class Container {

    public interface PojoGetters {
        int getN1();

        int getN2();

        String getS1();

        String getS2();
    }

    /**
     * Field names starting with "_initial" prefix should be added to {@code ignore()}
     * to ensure they are not modified by Instancio. These fields are used as a reference
     * for verifying a corresponding initialised field has not been reassigned.
     */
    public static final String REFERENCE_FIELD_REGEX = "^_initial.*";
    public static final int INITIAL_INT = -100;
    public static final int SELECTOR_INT = -200;
    public static final String INITIAL_STRING = "initial";
    public static final String SELECTOR_STRING = "overwrite";

    public static final Condition<PojoGetters> RANDOM_POJO = new Condition<>(pojo -> {
        assertThat(pojo.getN1()).is(RANDOM_INTEGER);
        assertThat(pojo.getN2()).is(RANDOM_INTEGER);
        assertThat(pojo.getS1()).is(RANDOM_STRING);
        assertThat(pojo.getS2()).is(RANDOM_STRING);
        return true;
    }, "random POJO");

    public static final Condition<PojoGetters> OVERWRITTEN_BY_SELECTOR = new Condition<>(pojo -> {
        assertThat(pojo.getN1()).isEqualTo(SELECTOR_INT);
        assertThat(pojo.getN2()).isEqualTo(SELECTOR_INT);
        assertThat(pojo.getS1()).isEqualTo(SELECTOR_STRING);
        assertThat(pojo.getS2()).isEqualTo(SELECTOR_STRING);
        return true;
    }, "overwritten by selector");

    public static final Condition<PojoGetters> UNMODIFIED_POJO = new Condition<>(pojo -> {
        assertThat(pojo.getN1()).isZero();
        assertThat(pojo.getN2()).isEqualTo(INITIAL_INT);
        assertThat(pojo.getS1()).isNull();
        assertThat(pojo.getS2()).isEqualTo(INITIAL_STRING);
        return true;
    }, "unmodified POJO");

    public static @Data class OuterPojo implements PojoGetters {

        private final InnerPojo _initialP2 = new InnerPojo();
        private final List<InnerPojo> _initialList2 = Arrays.asList(_initialP2);

        private int n1;
        private int n2;
        private String s1;
        private String s2;
        private InnerPojo p1;
        private InnerPojo p2;
        private List<InnerPojo> list1;
        private List<InnerPojo> list2;

        public OuterPojo() {
            n2 = INITIAL_INT;
            s2 = INITIAL_STRING;
            list2 = _initialList2;

            p2 = _initialP2;
            p2.n2 = INITIAL_INT;
            p2.s2 = INITIAL_STRING;
        }

        public static @Data class InnerPojo implements PojoGetters {
            private int n1;
            private int n2;
            private String s1;
            private String s2;
        }
    }
}