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
package org.instancio.internal.util;

import org.instancio.internal.util.UnicodeBlocks.BlockRange;
import org.junit.jupiter.api.Test;

import java.lang.Character.UnicodeBlock;

import static org.assertj.core.api.Assertions.assertThat;

class UnicodeBlocksTest {

    private final UnicodeBlocks unicodeBlocks = UnicodeBlocks.getInstance();

    @Test
    void cyrillic() {
        final BlockRange range = unicodeBlocks.getRange(UnicodeBlock.CYRILLIC);

        assertThat(range.min()).isEqualTo(0x0400);
        assertThat(range.max()).isEqualTo(0x04FF);
    }

    @Test
    void emoticons() {
        final BlockRange range = unicodeBlocks.getRange(UnicodeBlock.EMOTICONS);

        assertThat(range.min()).isEqualTo(0x1F600);
        assertThat(range.max()).isEqualTo(0x1F64F);
    }

    @Test
    void tags() {
        final BlockRange range = unicodeBlocks.getRange(UnicodeBlock.TAGS);

        assertThat(range.min()).isEqualTo(0xE0000);
        assertThat(range.max()).isEqualTo(0xE007F);
    }
}
