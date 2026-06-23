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
package org.instancio.test.features.oncomplete;

import lombok.Data;
import org.instancio.InstancioApi;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.Api;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * See: https://github.com/instancio/instancio/issues/1736
 */
@FeatureTag(Feature.ON_COMPLETE)
@ExtendWith(InstancioExtension.class)
class OnCompleteCallbackOrderTest {

    //@formatter:off
    private static @Data class Root0 { Depth1 child; }
    private static @Data class Depth1 { Depth2 depth2; }
    private static @Data class Depth2 { Depth3 depth3; }
    private static @Data class Depth3 { String leaf4; }
    //@formatter:on

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void callbacksAreInvokedBottomUp_regardlessOfApiInvocationOrder() {
        final List<String> order = new ArrayList<>();

        final InstancioApi<Root0> api = Api.shuffleApiOrder(Root0.class,
                a -> a.onComplete(all(Root0.class), o -> order.add("root0")),
                a -> a.onComplete(all(Depth1.class), o -> order.add("root0.depth1")),
                a -> a.onComplete(all(Depth2.class), o -> order.add("root0.depth1.depth2")),
                a -> a.onComplete(all(Depth3.class), o -> order.add("root0.depth1.depth2.depth3")),
                a -> a.onComplete(field(Depth3::getLeaf4), o -> order.add("root0.depth1.depth2.depth3.leaf4"))
        );

        api.create();

        assertThat(order).containsExactly(
                "root0.depth1.depth2.depth3.leaf4",
                "root0.depth1.depth2.depth3",
                "root0.depth1.depth2",
                "root0.depth1",
                "root0"
        );
    }
}
