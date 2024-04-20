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
package org.instancio.test.features.assign.adhoc;

import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.fields;

/**
 * This class defines lenient selectors because when multiple
 * predicate selectors match a given target, the last selector
 * wins and the other selector remains unused.
 */
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignAbcPredicateSelectorTest extends AssignAbcBaseTest {

    @Override
    TargetSelector selectA() {
        return fields().named("a").declaredIn(StringsAbc.class);
    }

    @Override
    TargetSelector selectB() {
        return fields().named("b").declaredIn(StringsAbc.class).lenient();
    }

    @Override
    TargetSelector selectC() {
        return fields().named("c").declaredIn(StringsAbc.class).lenient();
    }
}
