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
package org.instancio.internal.assigners;

import org.instancio.settings.MethodModifier;

import java.lang.reflect.Modifier;

final class AssignerUtil {

    private AssignerUtil() {
        // non-instantiable
    }

    static boolean isExcluded(final int modifiers, final int excludeModifiers) {
        return (modifiers & excludeModifiers) > 0
                || shouldExcludePackagePrivate(modifiers, excludeModifiers);
    }

    private static boolean shouldExcludePackagePrivate(final int modifiers, final int exclusionModifiers) {
        final boolean excludePackagePrivate = (exclusionModifiers & MethodModifier.PACKAGE_PRIVATE) > 0;

        return excludePackagePrivate
                && !Modifier.isPrivate(modifiers)
                && !Modifier.isProtected(modifiers)
                && !Modifier.isPublic(modifiers);
    }
}
