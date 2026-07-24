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
package org.instancio.test.parameternames;

/**
 * Fixtures compiled without debug information, so that their constructor
 * parameter names cannot be resolved.
 *
 * <p>Each nested class isolates one constructor shape. They are public because
 * they are compiled into {@code src/main/java} and used from the test sources.
 *
 * <p>See this module's {@code pom.xml} for how the absence of parameter names
 * is arranged, and {@code ParameterNamesAreMissingTest} for the guard that
 * verifies it is still in effect.
 */
@SuppressWarnings("all")
public final class NoDebugInfoPojos {

    private NoDebugInfoPojos() {
        // non-instantiable
    }

    /**
     * Every parameter would map to a field by name, if the names were available.
     */
    public static class AllArgsCtor {
        private String value;
        private int number;

        public AllArgsCtor(final String value, final int number) {
            this.value = value;
            this.number = number;
        }

        public String getValue() {
            return value;
        }

        public int getNumber() {
            return number;
        }
    }

    /**
     * One parameter maps to a field, one maps to none. Even when names
     * <i>are</i> available this constructor cannot be used, since ALL_ARGS
     * requires every parameter to map to a field; without names, nothing
     * maps at all.
     */
    public static class PartialArgsCtor {
        private String value;

        public PartialArgsCtor(final String prefix, final String value) {
            this.value = prefix + value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Declares both a value-passing and a no-argument constructor, so the
     * fallback has something to fall back to.
     */
    public static class AllArgsAndNoArgsCtor {
        private String value;

        public AllArgsAndNoArgsCtor() {
            // no-op
        }

        public AllArgsAndNoArgsCtor(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * No constructor with parameters, so no candidate constructor exists
     * and parameter names are never needed.
     */
    public static class NoArgsOnly {
        private String value;

        public String getValue() {
            return value;
        }
    }

    /**
     * Records match their canonical constructor positionally and never
     * require parameter names, so they are unaffected.
     */
    public record PersonRecord(String name, int age) {}
}
