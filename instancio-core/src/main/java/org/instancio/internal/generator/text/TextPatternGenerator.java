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
package org.instancio.internal.generator.text;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.ValueSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.Fail;
import org.instancio.support.Global;

public class TextPatternGenerator extends AbstractGenerator<String>
        implements ValueSpec<String> {

    private static final String ALLOWED_HASHTAGS_MESSAGE = String.format("%nAllowed hashtags:"
            + "%n\t#a - alphanumeric character [a-z, A-Z, 0-9]"
            + "%n\t#c - lower case character [a-z]"
            + "%n\t#C - upper case character [A-Z]"
            + "%n\t#d - digit [0-9]"
            + "%n\t#h - lower case hexadecimal character [a-f, 0-9]"
            + "%n\t#H - upper case hexadecimal character [A-F, 0-9]"
            + "%n\t## - hash symbol escape%n");

    private static final char ALNUM_CHAR = 'a';
    private static final char LCASE_CHAR = 'c';
    private static final char UCASE_CHAR = 'C';
    private static final char LCASE_HEX_CHAR = 'h';
    private static final char UCASE_HEX_CHAR = 'H';
    private static final char DIGIT = 'd';
    private static final char HASH = '#';
    private final String pattern;

    public TextPatternGenerator(final String pattern) {
        this(Global.generatorContext(), pattern);
    }

    public TextPatternGenerator(GeneratorContext context, final String pattern) {
        super(context);
        this.pattern = ApiValidator.notNull(pattern, "Text pattern must not be null");
    }

    @Override
    public String apiMethod() {
        return "pattern()";
    }

    @Override
    public TextPatternGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final StringBuilder res = new StringBuilder(pattern.length());
        final char[] p = pattern.toCharArray();

        int i = 0;
        while (i < p.length) {
            final char c = p[i++];

            if (c == HASH) {
                ApiValidator.isFalse(i == p.length,
                        "invalid text pattern '%s'. Expected a character after the last '#'", pattern);

                final char tag = p[i++];
                if (tag == ALNUM_CHAR) {
                    res.append(random.alphanumericCharacter());
                } else if (tag == LCASE_CHAR) {
                    res.append(random.lowerCaseCharacter());
                } else if (tag == UCASE_CHAR) {
                    res.append(random.upperCaseCharacter());
                } else if (tag == LCASE_HEX_CHAR) {
                    res.append(Character.toLowerCase(upperCaseHexChar(random)));
                } else if (tag == UCASE_HEX_CHAR) {
                    res.append(upperCaseHexChar(random));
                } else if (tag == DIGIT) {
                    res.append(random.digits(1));
                } else if (tag == HASH) {
                    res.append(HASH);
                } else {
                    throw Fail.withUsageError("text pattern '" + pattern
                            + "' contains an invalid hashtag '#" + tag + "'" + ALLOWED_HASHTAGS_MESSAGE);
                }
            } else {
                res.append(c);
            }
        }

        return res.toString();
    }

    private static char upperCaseHexChar(final Random random) {
        return random.trueOrFalse()
                ? random.characterRange('0', '9')
                : random.characterRange('A', 'F');
    }
}
