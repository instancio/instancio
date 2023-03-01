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
package org.other.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioOfClassApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.misc.getters.NonMatchingGetter;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag(Feature.METHOD_REFERENCE_SELECTOR)
class MethodReferenceSelectorUnresolvedFieldTest {

    @Test
    void resolvingFieldFails() {
        final InstancioOfClassApi<NonMatchingGetter> api = Instancio.of(NonMatchingGetter.class);
        assertThatThrownBy(() -> api.ignore(field(NonMatchingGetter::getBar)))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage(String.format("%n" +
                        "%n" +
                        "Unable to resolve the field from method reference:%n" +
                        "-> NonMatchingGetter::getBar%n" +
                        "   at org.other.test.features.mode.MethodReferenceSelectorUnresolvedFieldTest.lambda$resolvingFieldFails$0(MethodReferenceSelectorUnresolvedFieldTest.java:35)%n" +
                        "%n" +
                        "Potential causes:%n" +
                        "-> The method and the corresponding field do not follow expected naming conventions%n" +
                        "   See: https://www.instancio.org/user-guide/#method-reference-selector%n" +
                        "-> The method is abstract, declared in a superclass, and the field is declared in a subclass%n" +
                        "-> The method reference is expressed as a lambda function%n" +
                        "   Example:     field((SamplePojo pojo) -> pojo.getValue())%n" +
                        "   Instead of:  field(SamplePojo::getValue)%n" +
                        "-> You are using Kotlin and passing a method reference of a Kotlin class%n" +
                        "%n" +
                        "Possible solutions:%n" +
                        "-> Resolve the above issues, if applicable%n" +
                        "-> Specify the field name explicitly, e.g.%n" +
                        "   field(Example.class, \"someField\")%n" +
                        "-> If using Kotlin, consider creating a 'KSelect' utility class, for example:%n" +
                        "%n" +
                        "   class KSelect {%n" +
                        "       companion object {%n" +
                        "           fun <T, V> field(property: KProperty1<T, V>): TargetSelector {%n" +
                        "               val field = property.javaField!!%n" +
                        "               return Select.field(field.declaringClass, field.name)%n" +
                        "           }%n" +
                        "       }%n" +
                        "   }%n" +
                        "%n" +
                        "   Usage: KSelect.field(SamplePojo::value)"));
    }
}
