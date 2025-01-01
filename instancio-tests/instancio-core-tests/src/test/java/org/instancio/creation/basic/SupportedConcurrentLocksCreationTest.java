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
package org.instancio.creation.basic;

import org.instancio.test.support.pojo.basic.SupportedConcurrentLocksTypes;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@AutoVerificationDisabled
public class SupportedConcurrentLocksCreationTest extends CreationTestTemplate<SupportedConcurrentLocksTypes> {

    @Override
    protected void verify(final SupportedConcurrentLocksTypes result) {
        assertThat(result.getReentrantLock()).isNotNull();
        assertThat(result.getReentrantReadWriteLock()).isNotNull();
        assertThat(result.getStampedLock()).isNotNull();

        // Currently not supported
        assertThat(result.getLock()).isNull();
    }
}
