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
package org.instancio.internal.generator.text;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.LoremIpsumSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.support.Global;

import static org.instancio.internal.util.StringUtils.capitalise;

public class LoremIpsumGenerator extends AbstractGenerator<String>
        implements LoremIpsumSpec {

    private static final String[] WORD_BANK = {
            "ad", "adipiscing", "aliqua", "aliquip", "amet", "anim", "aute", "cillum", "commodo",
            "consectetur", "consequat", "culpa", "cupidatat", "deserunt", "do", "dolor", "dolore",
            "duis", "ea", "eiusmod", "elit", "enim", "esse", "est", "et", "eu", "ex", "excepteur",
            "exercitation", "fugiat", "id", "in", "incididunt", "ipsum", "irure", "labore", "laboris",
            "laborum", "lorem", "magna", "minim", "mollit", "nisi", "non", "nostrud", "nulla",
            "occaecat", "officia", "pariatur", "proident", "qui", "quis", "reprehenderit", "sed",
            "sint", "sit", "sunt", "tempor", "ullamco", "ut", "velit", "veniam", "voluptate",
    };

    private static final int AVG_WORD_LENGTH = 9;

    private int words = DEFAULT_WORDS;
    private int paragraphs = DEFAULT_PARAGRAPHS;

    public LoremIpsumGenerator() {
        this(Global.generatorContext());
    }

    public LoremIpsumGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "loremIpsum()";
    }

    @Override
    public LoremIpsumGenerator words(final int words) {
        ApiValidator.isTrue(words > 0, "number of words must be greater than zero: %s", words);
        this.words = words;
        return this;
    }

    @Override
    public LoremIpsumGenerator paragraphs(final int paragraphs) {
        ApiValidator.isTrue(paragraphs > 0, "number of paragraphs must be greater than zero: %s", paragraphs);
        this.paragraphs = paragraphs;
        return this;
    }

    @Override
    public LoremIpsumGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        ApiValidator.isTrue(words >= paragraphs,
                "the number of paragraphs (%s) is greater than the number of words (%s)", paragraphs, words);

        final int wordsPerParagraph = words / paragraphs;
        final int remainder = words - wordsPerParagraph * paragraphs;
        final StringBuilder sb = new StringBuilder(words * AVG_WORD_LENGTH);

        for (int p = 0; p < paragraphs; p++) {
            int remainingWords = p == 0 ? wordsPerParagraph + remainder : wordsPerParagraph;

            while (remainingWords > 0) {
                final int sentenceWords = Math.min(remainingWords, random.intRange(4, 20));
                appendSentence(random, sb, sentenceWords);
                remainingWords -= sentenceWords;

                if (remainingWords > 0) {
                    sb.append(' ');
                }
            }

            if (p < paragraphs - 1) {
                sb.append(System.lineSeparator()).append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    private static void appendSentence(final Random random, final StringBuilder sb, final int words) {
        sb.append(capitalise(random.oneOf(WORD_BANK)));
        for (int i = 1; i < words; i++) {
            sb.append(' ').append(random.oneOf(WORD_BANK));
        }
        sb.append('.');
    }

}
