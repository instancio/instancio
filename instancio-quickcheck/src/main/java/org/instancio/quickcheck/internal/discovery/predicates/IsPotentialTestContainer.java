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

package org.instancio.quickcheck.internal.discovery.predicates;

import static org.junit.platform.commons.util.ReflectionUtils.isAbstract;
import static org.junit.platform.commons.util.ReflectionUtils.isInnerClass;
import static org.junit.platform.commons.util.ReflectionUtils.isPrivate;

import java.util.function.Predicate;

public class IsPotentialTestContainer  implements Predicate<Class<?>> {
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    @Override
    public boolean test(Class<?> candidate) {
        // Please do not collapse the following into a single statement.
        if (isPrivate(candidate)) {
            return false;
        }
        if (isAbstract(candidate)) {
            return false;
        }
        if (candidate.isLocalClass()) {
            return false;
        }
        if (candidate.isAnonymousClass()) {
            return false;
        }
        
        return !isInnerClass(candidate);
    }

}
