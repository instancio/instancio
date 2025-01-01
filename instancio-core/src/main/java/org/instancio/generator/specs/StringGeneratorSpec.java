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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.settings.Keys;
import org.instancio.settings.StringType;

import java.lang.Character.UnicodeBlock;

/**
 * Generator spec for Strings.
 *
 * @since 1.0.1
 */
public interface StringGeneratorSpec extends NullableGeneratorSpec<String> {

    /**
     * Specifies the prefix to prepend to generated strings.
     *
     * @param prefix for generated strings
     * @return spec builder
     * @since 1.0.1
     */
    StringGeneratorSpec prefix(String prefix);

    /**
     * Specifies the suffix to append to generated strings.
     *
     * @param suffix for generated strings
     * @return spec builder
     * @since 2.7.0
     */
    StringGeneratorSpec suffix(String suffix);

    /**
     * Indicates that an empty string can be generated.
     *
     * @return spec builder
     * @since 1.0.1
     */
    StringGeneratorSpec allowEmpty();

    /**
     * Indicates if empty string can be generated.
     *
     * @param isAllowed if {@code true}, empty strings can be generated
     * @return spec builder
     * @since 2.7.0
     */
    StringGeneratorSpec allowEmpty(boolean isAllowed);

    /**
     * Specifies the length of the string to generate as returned
     * by {@link String#length()}, or the number of code points when
     * generating a {@link #unicode(UnicodeBlock...)} string.
     *
     * <p>This method has no effect if {@link #numericSequence()} is specified.
     *
     * @param length exact length to generate
     * @return spec builder
     */
    StringGeneratorSpec length(int length);

    /**
     * Specifies the length of the string to generate as returned
     * by {@link String#length()}, or the number of code points when
     * generating a {@link #unicode(UnicodeBlock...)} string.
     *
     * <p>This method has no effect if {@link #numericSequence()} is specified.
     *
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return spec builder
     */
    StringGeneratorSpec length(int minLength, int maxLength);

    /**
     * Specifies the minimum length of the string to generate as returned
     * by {@link String#length()}, or the number of code points when
     * generating a {@link #unicode(UnicodeBlock...)} string.
     *
     * <p>This method has no effect if {@link #numericSequence()} is specified.
     *
     * @param length minimum length (inclusive)
     * @return spec builder
     * @since 1.0.1
     */
    StringGeneratorSpec minLength(int length);

    /**
     * Specifies the maximum length of the string to generate as returned
     * by {@link String#length()}, or the number of code points when
     * generating a {@link #unicode(UnicodeBlock...)} string.
     *
     * <p>This method has no effect if {@link #numericSequence()} is specified.
     *
     * @param length maximum length (inclusive)
     * @return spec builder
     * @since 1.0.1
     */
    StringGeneratorSpec maxLength(int length);

    /**
     * Generates a lowercase string that consists
     * of characters {@code [a-z]}.
     *
     * <p><b>Note:</b> this method has no effect when
     * used with any of the following methods:
     *
     * <ul>
     *   <li>{@link #digits()}</li>
     *   <li>{@link #numericSequence()}</li>
     *   <li>{@link #unicode(UnicodeBlock...)}</li>
     * </ul>
     *
     * @return spec builder
     */
    StringGeneratorSpec lowerCase();

    /**
     * Generates an uppercase string that consists
     * of characters {@code [A-Z]}.
     *
     * <p><b>Note:</b> this method has no effect when
     * used with any of the following methods:
     *
     * <ul>
     *   <li>{@link #digits()}</li>
     *   <li>{@link #numericSequence()}</li>
     *   <li>{@link #unicode(UnicodeBlock...)}</li>
     * </ul>
     *
     * @return spec builder
     */
    StringGeneratorSpec upperCase();

    /**
     * Generates a string that consists
     * of characters {@code [A-Za-z]}.
     *
     * <p><b>Note:</b> this method has no effect when
     * used with any of the following methods:
     *
     * <ul>
     *   <li>{@link #digits()}</li>
     *   <li>{@link #numericSequence()}</li>
     *   <li>{@link #unicode(UnicodeBlock...)}</li>
     * </ul>
     *
     * @return spec builder
     */
    StringGeneratorSpec mixedCase();

    /**
     * Generates an alphanumeric string, uppercase by default,
     * that consists of characters {@code [0-9A-Z]}.
     *
     * <p>The equivalent {@link Keys#STRING_TYPE} setting
     * is {@link StringType#ALPHANUMERIC}.
     *
     * @return spec builder
     * @see #upperCase()
     * @see #lowerCase()
     * @see #mixedCase()
     */
    StringGeneratorSpec alphaNumeric();

    /**
     * Generates a string that consists of digits {@code [0-9]}.
     *
     * <p>The equivalent {@link Keys#STRING_TYPE} setting
     * is {@link StringType#DIGITS}.
     *
     * @return spec builder
     */
    StringGeneratorSpec digits();

    /**
     * Generates a hexadecimal string, uppercase by default,
     * that consists of characters {@code [0-9A-F]}
     *
     * <p>The equivalent {@link Keys#STRING_TYPE} setting
     * is {@link StringType#HEX}.
     *
     * @return spec builder
     * @see #upperCase()
     * @see #lowerCase()
     * @see #mixedCase()
     * @since 2.11.0
     */
    StringGeneratorSpec hex();

    /**
     * Generates a string sequence that starts from {@code "1"}
     * and increments by one.
     *
     * <p>The equivalent {@link Keys#STRING_TYPE} setting
     * is {@link StringType#NUMERIC_SEQUENCE}.
     *
     * @return spec builder
     * @since 4.7.0
     */
    @ExperimentalApi
    StringGeneratorSpec numericSequence();

    /**
     * Generates a Unicode string that consists of random
     * code points excluding the following character types:
     *
     * <ul>
     *   <li>{@link Character#PRIVATE_USE}</li>
     *   <li>{@link Character#SURROGATE}</li>
     *   <li>{@link Character#UNASSIGNED}</li>
     * </ul>
     *
     * <p>This method accepts an optional vararg of {@link UnicodeBlock}
     * objects. If no {@code unicodeBlocks} are specified, code points will
     * be generated from the range {@code [0..0x3FFFF]}, for example:
     *
     * <pre>{@code
     * Comment comment = Instancio.of(Comment.class)
     *     .generate(field(Comment::getText), gen -> gen.string().unicode().length(10))
     *     .create();
     *
     * // Sample output:
     * // Comment[text="𪬨鉢𓉰埗ᮓ웝𤈱恼棭騜"]
     * }</pre>
     *
     * <p>If {@code unicodeBlocks} are specified, code points will be
     * chosen from the given Unicode blocks:
     *
     * <pre>{@code
     * Character.UnicodeBlock[] blocks = { Character.UnicodeBlock.EMOTICONS, Character.UnicodeBlock.CYRILLIC };
     *
     * Comment comment = Instancio.of(Comment.class)
     *     .generate(field(Comment::getText), gen -> gen.string().length(10).unicode(blocks))
     *     .create();
     *
     * // Sample output:
     * // Comment[text="ф😬😅😟фҖӝ😲ЭӍ"]
     * }</pre>
     *
     * <p>The equivalent {@link Keys#STRING_TYPE} setting
     * is {@link StringType#UNICODE}.
     *
     * @param blocks Unicode blocks to use when generating strings,
     *               or no argument to generate code points from random blocks
     * @return spec builder
     * @since 4.7.0
     */
    @ExperimentalApi
    StringGeneratorSpec unicode(UnicodeBlock... blocks);

    /**
     * {@inheritDoc}
     */
    @Override
    StringGeneratorSpec nullable();
}
