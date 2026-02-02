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
package org.instancio.internal.nodes.resolvers;

import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.nodes.NodeKindResolver;
import org.instancio.internal.util.StringUtils;

import java.util.Optional;

class NodeKindJdkResolver implements NodeKindResolver {

    private static final String[] SYSTEM_PACKAGES = {"java.", "javax."};

    @Override
    public Optional<NodeKind> resolve(final Class<?> targetClass) {
        if (targetClass.isPrimitive() || targetClass.isEnum()) {
            return Optional.of(NodeKind.JDK);
        }

        final Package pkg = targetClass.getPackage();
        if (pkg == null) {
            return Optional.empty();
        }

        return StringUtils.startsWithAny(pkg.getName(), SYSTEM_PACKAGES)
                ? Optional.of(NodeKind.JDK)
                : Optional.empty();
    }
}
