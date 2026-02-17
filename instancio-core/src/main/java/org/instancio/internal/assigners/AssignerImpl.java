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
package org.instancio.internal.assigners;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Verify;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

public class AssignerImpl implements Assigner {

    private final Assigner delegate;

    public AssignerImpl(final ModelContext context) {
        this.delegate = resolveAssigner(context);
    }

    @Override
    public void assign(final InternalNode node, final Object target, final @Nullable Object value) {
        delegate.assign(node, target, value);
    }

    private static Assigner resolveAssigner(final ModelContext context) {
        final Settings settings = context.getSettings();
        final AssignmentType assignment = Verify.notNull(settings.get(Keys.ASSIGNMENT_TYPE), "assignmentType is null");

        if (assignment == AssignmentType.FIELD) {
            return new FieldAssigner(settings);
        }
        if (assignment == AssignmentType.METHOD) {
            return new MethodAssigner(context);
        }
        throw Fail.withFataInternalError("Invalid assignment type: %s", assignment);
    }
}
