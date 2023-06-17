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
package org.instancio.test.features.assign;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

/**
 * This test is disabled and is for documentation purposes only.
 */
@Disabled("Unsupported")
@FeatureTag({Feature.ASSIGN, Feature.UNSUPPORTED})
@ExtendWith(InstancioExtension.class)
class AssignUnsupportedTest {

    //@formatter:off
    private static @Data class Order { List<OrderItem> items; }
    private static @Data class OrderItem { Order order; }
    //@formatter:on

    /**
     * Currently unsupported because {@code OrderItem.order} is set
     * to {@code null} to terminate the cycle. Will probably require
     * changes to the {@link NodeFactory} to support this use case.
     */
    @Test
    void assignRoot() {
        final Order order = Instancio.of(Order.class)
                .assign(valueOf(root()).to(OrderItem::getOrder))
                .create();

        assertThat(order.getItems()).isNotEmpty().allSatisfy(item ->
                assertThat(item.getOrder()).isSameAs(order));
    }


    /**
     * What happens when the origin selector matches multiple targets?
     * The targets may have different values. Some, all, or none of the values
     * may satisfy the conditional. In such situations, the outcome is undefined.
     *
     * <p>This test illustrates a sample use case:
     * given a list of Phones, if generated country code is +1,
     * set the singular country field to Canada, otherwise generate a random country.
     *
     * <p>With current implementation, this seems to work only if:
     * <ul>
     *     <li>+1 is not generated at all, therefore all countries are random values</li>
     *     <li>+1 is generated for the last Phone element in the list</li>
     * </ul>
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void whenPhoneThenCountry() {
        final String canada = "Canada";
        final AtomicBoolean hasCanadianCountryCode = new AtomicBoolean();

        final Address result = Instancio.of(Address.class)
                .generate(field(Phone::getCountryCode), gen -> gen.oneOf("+1", "+2", "+3"))
                .assign(Assign.given(Phone::getCountryCode).is("+1").set(field(Address::getCountry), canada))
                .onComplete(all(Phone.class), (Phone phone) -> {
                    if (phone.getCountryCode().equals("+1")) {
                        hasCanadianCountryCode.set(true);
                    }
                })
                .create();

        if (hasCanadianCountryCode.get()) {
            assertThat(result.getCountry()).isEqualTo(canada);
        } else {
            assertThat(result.getCountry()).isNotEqualTo(canada);
        }
    }
}
