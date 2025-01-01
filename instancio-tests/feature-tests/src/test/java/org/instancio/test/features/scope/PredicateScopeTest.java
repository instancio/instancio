/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.scope;

import lombok.Data;
import org.instancio.GroupableSelector;
import org.instancio.Instancio;
import org.instancio.Scope;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.DEPTH_SELECTOR,
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.SELECT_GROUP,
        Feature.SCOPE,
        Feature.TO_SCOPE
})
@ExtendWith(InstancioExtension.class)
class PredicateScopeTest {

    private static final String FOO = "_foo_";

    private static @Data class Outer {
        String outerString;
        Mid mid;
    }

    private static @Data class Mid {
        String midString;
        Inner inner1;
        Inner inner2;
    }

    private static @Data class Inner {
        String innerString1;
        String innerString2;
    }

    @Test
    void predicateByFieldNameIgnoringFieldType() {
        final Scope scope = scope(fields(f -> f.getName().contains("1")));

        final Outer result = Instancio.of(Outer.class)
                .set(allStrings().within(scope), FOO)
                .create();

        assertThat(result.outerString).isNotEqualTo(FOO);
        assertThat(result.mid.midString).isNotEqualTo(FOO);
        assertThat(result.mid.inner1.innerString1).isEqualTo(FOO); // matches inner1
        assertThat(result.mid.inner1.innerString2).isEqualTo(FOO); // matches inner1
        assertThat(result.mid.inner2.innerString1).isEqualTo(FOO); // matches innerString1
        assertThat(result.mid.inner2.innerString2).isNotEqualTo(FOO);
    }

    @Test
    void predicateByFieldNameAndFieldType() {
        final Scope scope = scope(fields(f -> f.getType() == String.class && f.getName().contains("1")));

        final Outer result = Instancio.of(Outer.class)
                .set(allStrings().within(scope), FOO)
                .create();

        assertThat(result.outerString).isNotEqualTo(FOO);
        assertThat(result.mid.midString).isNotEqualTo(FOO);
        assertThat(result.mid.inner1.innerString1).isEqualTo(FOO);
        assertThat(result.mid.inner1.innerString2).isNotEqualTo(FOO);
        assertThat(result.mid.inner2.innerString1).isEqualTo(FOO);
        assertThat(result.mid.inner2.innerString2).isNotEqualTo(FOO);
    }

    @Test
    void multipleScopes() {
        final Scope scope1 = scope(types(t -> t == Outer.class));
        final Scope scope2 = scope(Mid.class);
        final Scope scope3 = scope(fields(f -> f.getName().equals("inner1")));
        final Scope scope4 = scope(fields(f -> f.getType() == String.class && f.getName().contains("1")));

        final Outer result = Instancio.of(Outer.class)
                .set(allStrings().within(scope1, scope2, scope3, scope4), FOO)
                .create();

        assertThat(result.outerString).isNotEqualTo(FOO);
        assertThat(result.mid.midString).isNotEqualTo(FOO);
        assertThat(result.mid.inner1.innerString1).isEqualTo(FOO);
        assertThat(result.mid.inner1.innerString2).isNotEqualTo(FOO);
        // shouldn't match "inner2"
        assertThat(result.mid.inner2.innerString1).isNotEqualTo(FOO);
        assertThat(result.mid.inner2.innerString2).isNotEqualTo(FOO);
    }

    @Test
    void withinGroup() {
        final GroupableSelector withinInner1 = allStrings().within(scope(fields(f -> f.getName().equals("inner1"))));
        final GroupableSelector withinInner2 = allStrings().within(scope(fields(f -> f.getName().equals("inner2"))));

        final Outer result = Instancio.of(Outer.class)
                .set(all(withinInner1, withinInner2), FOO)
                .create();

        assertOnlyInnerStringsAreEqualToFoo(result);
    }

    @Test
    void withAtDepth() {
        final Scope scope = fields(f -> f.getType() == String.class).atDepth(d -> d > 2).toScope();

        final Outer result = Instancio.of(Outer.class)
                .set(allStrings().within(scope), FOO)
                .create();

        assertOnlyInnerStringsAreEqualToFoo(result);
    }

    @Test
    void predicateSelectorWithPredicateScope() {
        final Scope scope1 = scope(Outer.class);
        final Scope scope2 = types().of(Mid.class).toScope();
        final Scope scope3 = fields().matching("^inner[12]$").toScope();

        final Outer result = Instancio.of(Outer.class)
                .set(types(t -> t == String.class).within(scope1, scope2, scope3), FOO)
                .create();

        assertOnlyInnerStringsAreEqualToFoo(result);
    }

    @Nested
    class FieldSelectorBuilderSCopeTest {

        @Test
        void toScope() {
            final Scope scope = fields().ofType(String.class).atDepth(d -> d > 2).toScope();

            final Outer result = Instancio.of(Outer.class)
                    .set(all(allStrings().within(scope)), FOO)
                    .create();

            assertOnlyInnerStringsAreEqualToFoo(result);
        }

        @Test
        void fieldsWithin() {
            final Scope scope = types().of(Inner.class).toScope();

            final Outer result = Instancio.of(Outer.class)
                    .set(fields().ofType(String.class).within(scope), FOO)
                    .create();

            assertOnlyInnerStringsAreEqualToFoo(result);
        }
    }

    @Nested
    class TypeSelectorBuilderSCopeTest {

        @Test
        void toScope() {
            final Scope scope = types().of(Inner.class).toScope();

            final Outer result = Instancio.of(Outer.class)
                    .set(all(allStrings().within(scope)), FOO)
                    .create();

            assertOnlyInnerStringsAreEqualToFoo(result);
        }

        @Test
        void typesWithin() {
            final Scope scope = types().of(Inner.class).toScope();

            final Outer result = Instancio.of(Outer.class)
                    .set(types().of(String.class).within(scope), FOO)
                    .create();

            assertOnlyInnerStringsAreEqualToFoo(result);
        }
    }

    private static void assertOnlyInnerStringsAreEqualToFoo(final Outer result) {
        assertThat(result.outerString).isNotEqualTo(FOO);
        assertThat(result.mid.midString).isNotEqualTo(FOO);
        assertThat(result.mid.inner1.innerString1).isEqualTo(FOO);
        assertThat(result.mid.inner1.innerString2).isEqualTo(FOO);
        assertThat(result.mid.inner2.innerString1).isEqualTo(FOO);
        assertThat(result.mid.inner2.innerString2).isEqualTo(FOO);
    }

}
