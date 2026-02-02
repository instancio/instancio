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
package org.instancio.test.features.assign;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@FeatureTag({Feature.ASSIGN, Feature.SCOPE})
@ExtendWith(InstancioExtension.class)
class AssignWithScopeTest {

    private static final String ID_1 = "ID-1";
    private static final String ID_2 = "ID-2";
    private static final String ROOT_1 = "Root-1";
    private static final String ROOT_2 = "Root-2";

    // @formatter:off
    private static @Data class Id { String val; }
    private static @Data class A { Id id; String val; }
    private static @Data class B { Id id; String val; }
    private static @Data class Root { String val; A a; B b; }
    // @formatter:on

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void whenBIdVal_thenRootVal() {
        // Since multiple values would match for field(Id::getVal),
        // scope must be used to narrow down which the desired one
        final TargetSelector origin = field(Id::getVal).within(scope(B.class));

        final Root root = Instancio.of(Root.class)
                .generate(field(Id::getVal), gen -> gen.oneOf(ID_1, ID_2))
                .assign(Assign.given(origin).is(ID_1).set(field(Root::getVal), ROOT_1))
                .assign(Assign.given(origin).is(ID_2).set(field(Root::getVal), ROOT_2))
                .create();

        final String expectedRootVal = ID_1.equals(root.b.id.val) ? ROOT_1 : ROOT_2;

        assertThat(root.b.id.val).isIn(ID_1, ID_2);
        assertThat(root.val).isEqualTo(expectedRootVal);
    }
}
