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
package org.instancio.test.contract.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

public class UnconditionalModuleExportCondition extends ArchCondition<JavaClass> {
    public UnconditionalModuleExportCondition() {
        super("be unconditionally exported");
    }

    @Override
    public void check(final JavaClass javaClass, final ConditionEvents events) {
        final Class<?> actualClass = javaClass.reflect();
        final Module module = actualClass.getModule();

        if (module.isExported(actualClass.getPackageName())) {
            events.add(SimpleConditionEvent.satisfied(javaClass, "%s is unconditionally exported".formatted(javaClass.getName())));
        } else {
            events.add(SimpleConditionEvent.violated(javaClass, "%s is not unconditionally exported".formatted(javaClass.getName())));
        }
    }
}
