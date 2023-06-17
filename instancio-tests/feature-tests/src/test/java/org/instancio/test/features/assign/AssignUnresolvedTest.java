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
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignUnresolvedTest {

    @Test
    void unresolvedCollectionElement() {
        @Data
        class Record {
            Collection<IntegerHolder> collection;
            String a, b;
        }

        final InstancioApi<Record> api = Instancio.of(Record.class)
                .assign(given(Record::getA)
                        .satisfies(o -> true)
                        .supply(all(IntegerHolder.class), IntegerHolder::new))
                .assign(valueOf(Record::getA).to(Record::getB))
                .assign(valueOf(Record::getB).to(Record::getA));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class);
    }

}
