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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.MultipleClassesWithId;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.field;

@FeatureTag({Feature.ASSIGN, Feature.SELECTOR})
@ExtendWith(InstancioExtension.class)
class AssignWithRegularSelectorTest {

    @Test
    void fieldSelectorWithoutClass() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(field(StringFields::getTwo), "2")
                // using field(String) selector (without specifying the target class)
                .assign(Assign.given(field("two")).is("2").set(field("one"), "1"))
                .create();

        assertThat(result.getOne()).isEqualTo("1");
        assertThat(result.getTwo()).isEqualTo("2");
    }

    /**
     * @see MultipleClassesWithId
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void fieldSelectorWithDepth() {
        final TargetSelector origin = field(MultipleClassesWithId.ID.class, "value").atDepth(3);
        final TargetSelector destination = field(MultipleClassesWithId.ID.class, "value").atDepth(4);

        final String originValue1 = "O1";
        final String originValue2 = "O2";
        final String originValue3 = "O3"; // no conditional defined (i.e. expect random dest values)
        final String destValue1 = "D1";
        final String destValue2 = "D2";

        final MultipleClassesWithId<String> result = Instancio.of(new TypeToken<MultipleClassesWithId<String>>() {})
                .generate(origin, gen -> gen.oneOf(originValue1, originValue2, originValue3))
                .assign(Assign.given(origin).is(originValue1).set(destination, destValue1))
                .assign(Assign.given(origin).is(originValue2).set(destination, destValue2))
                .create();

        final String originValue = result.getA().getId().getValue();
        final String destB = result.getA().getB().getId().getValue();
        final String destC = result.getA().getC().getId().getValue();

        if (originValue1.equals(originValue)) {
            assertThat(destB).isEqualTo(destC).isEqualTo(destValue1);
        } else if (originValue2.equals(originValue)) {
            assertThat(destB).isEqualTo(destC).isEqualTo(destValue2);
        } else if (originValue3.equals(originValue)) {
            // random values
            assertThat(destB).isNotIn(destValue1, destValue2);
            assertThat(destC).isNotIn(destValue1, destValue2);
        } else {
            fail("Unexpected origin value: '%s'", originValue);
        }

        // random ID values expected fro values at depth greater than 4
        assertThat(result.getA().getC().getB().getId().getValue()).isNotIn(destValue1, destValue2);
        assertThat(result.getA().getC().getD().getId().getValue()).isNotIn(destValue1, destValue2);
    }
}
