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
package org.external.errorhandling;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.misc.getters.NonMatchingGetter;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import static org.instancio.Select.field;

@FeatureTag(Feature.METHOD_REFERENCE_SELECTOR)
class MethodReferenceSelectorUnresolvedFieldTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(NonMatchingGetter.class)
                .ignore(field(NonMatchingGetter::getBar));
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.MethodReferenceSelectorUnresolvedFieldTest.methodUnderTest(MethodReferenceSelectorUnresolvedFieldTest.java:31)

                Reason:\s

                Unable to resolve the field from method reference:
                -> NonMatchingGetter::getBar
                   at org.external.errorhandling.MethodReferenceSelectorUnresolvedFieldTest.methodUnderTest(MethodReferenceSelectorUnresolvedFieldTest.java:31)

                Potential causes:
                -> The method and the corresponding field do not follow expected naming conventions
                   See: https://www.instancio.org/user-guide/#method-reference-selector
                -> The method is abstract, declared in a superclass, and the field is declared in a subclass
                -> The method reference is expressed as a lambda function
                   Example:     field((SamplePojo pojo) -> pojo.getValue())
                   Instead of:  field(SamplePojo::getValue)
                -> You are using Kotlin and passing a method reference of a Kotlin class

                Possible solutions:
                -> Resolve the above issues, if applicable
                -> Specify the field name explicitly, e.g.
                   field(Example.class, "someField")
                -> If using Kotlin, consider creating a 'KSelect' utility class, for example:

                   class KSelect {
                       companion object {
                           fun <T, V> field(property: KProperty1<T, V>): TargetSelector {
                               val field = property.javaField!!
                               return Select.field(field.declaringClass, field.name)
                           }
                       }
                   }

                   Usage: KSelect.field(SamplePojo::value)


                """;
    }
}
