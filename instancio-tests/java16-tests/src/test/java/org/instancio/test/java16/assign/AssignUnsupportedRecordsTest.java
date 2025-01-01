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
package org.instancio.test.java16.assign;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.cyclic.ChildRecord;
import org.instancio.test.support.java16.record.cyclic.ParentRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.root;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignUnsupportedRecordsTest {

    /**
     * Parent record cannot be fully constructed until all its children are.
     * The children, in turn, need a reference to the parent object
     * to set the back-reference. This creates a circular dependency,
     * therefore the assignment cannot be resolved.
     */
    @Test
    @FeatureTag(Feature.UNSUPPORTED)
    void cyclicParentChildWithBackReference() {
        final InstancioApi<ParentRecord> api = Instancio.of(ParentRecord.class)
                .assign(valueOf(root()).to(ChildRecord::parent));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class);
    }
}
