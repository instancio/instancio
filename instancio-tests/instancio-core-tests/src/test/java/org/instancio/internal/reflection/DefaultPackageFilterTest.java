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
package org.instancio.internal.reflection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DefaultPackageFilterTest {

    private final PackageFilter filter = new DefaultPackageFilter();

    @ValueSource(strings = {"java.foo", "javax.foo", "com.sun.foo", "sun.foo"})
    @ParameterizedTest
    void excluded(String packageName) {
        assertThat(filter.isExcluded(mockPackage(packageName))).isTrue();
    }


    @ValueSource(strings = {"java", "javax", "com.sun", "sun"})
    @ParameterizedTest
    void notExcluded(String packageName) {
        assertThat(filter.isExcluded(mockPackage(packageName))).isFalse();
    }

    private static Package mockPackage(final String packageName) {
        final Package pkg = Mockito.mock(Package.class);
        when(pkg.getName()).thenReturn(packageName);
        return pkg;
    }
}
