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
package org.instancio.test.java16.applyfeedprovider;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.PhoneRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class ApplyFeedProviderRecordTest {

    @Test
    void applyFeed() {
        final PhoneRecord result = Instancio.of(PhoneRecord.class)
                .applyFeed(root(), feed -> feed.ofString("""
                        countryCode,number
                        +1,1234567
                        """))
                .create();

        assertThat(result.countryCode()).isEqualTo("+1");
        assertThat(result.number()).isEqualTo("1234567");
    }

    @Test
    void applyFeed_overrideValueUsingSet() {
        final PhoneRecord result = Instancio.of(PhoneRecord.class)
                .applyFeed(root(), feed -> feed.ofString("""
                        countryCode,number
                        +1,1234567
                        """))
                // override feed data with a custom value
                .set(field(PhoneRecord::countryCode), "+2")
                .create();

        assertThat(result.countryCode()).isEqualTo("+2");
        assertThat(result.number()).isEqualTo("1234567");
    }
}
