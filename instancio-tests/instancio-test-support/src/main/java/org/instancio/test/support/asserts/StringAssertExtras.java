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
package org.instancio.test.support.asserts;

import org.assertj.core.api.StringAssert;

import java.lang.Character.UnicodeBlock;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// See: http://www.unicode.org/Public/UNIDATA/Blocks.txt
@SuppressWarnings("UnusedReturnValue")
public class StringAssertExtras extends StringAssert {

    private StringAssertExtras(final String actual) {
        super(actual);
    }

    public static StringAssertExtras assertString(final String actual) {
        return new StringAssertExtras(actual);
    }

    public StringAssertExtras hasCodePointCount(final int expected) {
        assertThat(actual.codePointCount(0, actual.length()))
                .as("code points")
                .isEqualTo(expected);
        return this;
    }

    public StringAssertExtras hasUnicodeBlockCount(final int expected) {
        assertThat(getDistinctCodeBlocks()).hasSize(expected);
        return this;
    }

    public StringAssertExtras hasUnicodeBlockCountGreaterThan(final int expected) {
        assertThat(getDistinctCodeBlocks()).hasSizeGreaterThan(expected);
        return this;
    }

    public StringAssertExtras hasCodePointsFrom(final UnicodeBlock... blocks) {
        assertThat(getDistinctCodeBlocks()).containsExactlyInAnyOrder(blocks);
        return this;
    }

    private Set<UnicodeBlock> getDistinctCodeBlocks() {
        return actual.codePoints().mapToObj(UnicodeBlock::of).collect(Collectors.toSet());
    }
}
