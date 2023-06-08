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
package org.instancio.test.features.conditional;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.When.valueOf;

@FeatureTag({Feature.CONDITIONAL, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class ConditionalWithOnCompleteTest {

    @Test
    void destinationSelectorOnComplete() {
        final AtomicBoolean callbackInvoked = new AtomicBoolean();
        final String expected = "foo";

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .when(valueOf(field(StringsAbc::getA))
                        .satisfies(val -> true)
                        .set(field(StringsAbc::getB), expected))
                .onComplete(field(StringsAbc::getB), b -> {
                    callbackInvoked.set(true);
                    assertThat(b).isEqualTo(expected);
                })
                .create();

        assertThat(callbackInvoked).isTrue();
        assertThat(result.b).isEqualTo(expected);
    }
}
