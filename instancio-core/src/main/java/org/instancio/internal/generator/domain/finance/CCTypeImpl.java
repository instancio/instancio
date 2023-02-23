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
package org.instancio.internal.generator.domain.finance;

import org.instancio.internal.util.CollectionUtils;

import java.util.List;

// Avoid naming CreditCard* to prevent it from appearing in IDE completion
public enum CCTypeImpl {

    CC_MASTERCARD(16, 51, 52, 53, 54, 55),
    CC_VISA(16, 4);

    private final int length;
    private final List<Integer> prefixes;

    CCTypeImpl(final int length, final Integer... prefixes) {
        this.length = length;
        this.prefixes = CollectionUtils.asList(prefixes);
    }

    public int getLength() {
        return length;
    }

    public List<Integer> getPrefixes() {
        return prefixes;
    }
}
