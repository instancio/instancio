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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.PREDICATE_SELECTOR)
@ExtendWith(InstancioExtension.class)
class CustomPredicateNodeSelectorTest {

    @Test
    @DisplayName("Should select all fields where node.parent.targetClass is Address")
    void shouldSetStringsWithinAddress() {
        final Person result = Instancio.of(Person.class)
                .set(addressStringSelector(), null)
                .create();

        assertThat(result).hasNoNullFieldsOrProperties();

        assertThat(result.getAddress())
                .hasAllNullFieldsOrPropertiesExcept("phoneNumbers");

        assertThat(result.getAddress().getPhoneNumbers())
                .allSatisfy(phone -> assertThat(phone).hasNoNullFieldsOrProperties());
    }

    private static TargetSelector addressStringSelector() {
        final Predicate<InternalNode> predicate = n -> n.getTargetClass() == String.class
                && n.getParent().getTargetClass() == Address.class;

        return new AddressStringSelector(predicate, "addressStringsSelector()");
    }

    private static class AddressStringSelector extends PredicateSelectorImpl {
        private static final int PRIORITY = Integer.MAX_VALUE; // lowest priority

        AddressStringSelector(final Predicate<InternalNode> nodePredicate, final String apiInvocationDescription) {
            super(PRIORITY,
                    nodePredicate,
                    Collections.emptyList(),
                    /* depth = */ null,
                    /* isLenient = */ false,
                    apiInvocationDescription,
                    new Throwable());
        }
    }
}
