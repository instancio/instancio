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
package org.instancio.test.features.feed.applyfeed;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedWithoutCustomFeedInterfaceTest {

    /**
     * Since there is no custom Feed interface defined
     * (and no {@link FeedSpec} methods that map to properties),
     * this should simply get values from the data source by key
     * using {@link Feed#spec(String, Class)}.
     */
    @RepeatedTest(5)
    void shouldUseFeedWithoutExplicitSpecMethods() {
        final Feed feed = Instancio.ofFeed(Feed.class)
                .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                .withDataSource(source -> source.ofInputStream(new ByteArrayInputStream(
                        "countryCode,number\nc1,n1\nc2,n2\nc3,n3\n".getBytes())))
                .create();

        final int numPersons = 2;
        final int numPhonesPerPerson = 3;

        final List<Person> results = Instancio.ofList(Person.class)
                .size(numPersons)
                .generate(field(Address::getPhoneNumbers), gen -> gen.collection().size(numPhonesPerPerson))
                .applyFeed(all(Phone.class), feed)
                .create();

        final List<Phone> phones = results.stream()
                .flatMap(p -> p.getAddress().getPhoneNumbers().stream())
                .collect(Collectors.toList());

        assertThat(phones)
                .hasSize(numPersons * numPhonesPerPerson)
                .extracting(Phone::getCountryCode)
                .containsExactly("c1", "c2", "c3", "c1", "c2", "c3");

        assertThat(phones)
                .extracting(Phone::getNumber)
                .containsExactly("n1", "n2", "n3", "n1", "n2", "n3");

        // other fields should not be random values without digits
        assertThat(results).allSatisfy(person -> {
            assertThat(person.getName()).isAlphabetic();
            assertThat(person.getAddress().getCity()).isAlphabetic();
        });
    }
}
