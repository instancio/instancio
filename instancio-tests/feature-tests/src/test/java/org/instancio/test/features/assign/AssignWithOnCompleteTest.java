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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.field;

@FeatureTag({Feature.ASSIGN, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class AssignWithOnCompleteTest {

    private static final String EXPECTED = "foo";

    @Test
    void destinationSelectorOnComplete() {
        final AtomicBoolean callbackInvoked = new AtomicBoolean();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(Assign.given(field(StringsAbc::getA))
                        .satisfies(val -> true)
                        .generate(field(StringsAbc::getB), gen -> gen.text().pattern(EXPECTED)))
                .onComplete(field(StringsAbc::getB), b -> {
                    callbackInvoked.set(true);
                    assertThat(b).isEqualTo(EXPECTED);
                })
                .create();

        assertThat(callbackInvoked).isTrue();
        assertThat(result.b).isEqualTo(EXPECTED);
    }

    @Test
    void assignValueOfTo_withOnCompleteDestination() {
        final Set<String> onCompleteValues = new HashSet<>();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(StringsAbc.class)
                        .to(StringsAbc::getA)
                        .as((StringsAbc abc) -> abc.getB() + " " + abc.getC()))
                .onComplete(field(StringsAbc::getA), onCompleteValues::add)
                .create();

        assertThat(onCompleteValues).containsOnly(result.getB() + " " + result.getC());
    }

    @FieldSource("args")
    @ParameterizedTest
    void callbackNotInvokedOnSupply(final Assignment assignment) {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(assignment)
                .onComplete(field(StringsAbc::getA), val ->
                        fail("callback should not be invoked on objects provided via set(Object) or supply(Supplier)"))
                .lenient()
                .create();

        assertThat(result.a).isEqualTo(EXPECTED);
    }

    private static final List<Assignment> args = Arrays.asList(
            Assign.valueOf(field(StringsAbc::getA)).set(EXPECTED),
            Assign.valueOf(field(StringsAbc::getA)).supply(() -> EXPECTED));

}
