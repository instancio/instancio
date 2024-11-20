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
import org.instancio.generator.specs.WordSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class WordGenerator extends AbstractGenerator<String> implements WordSpec {

    private static final Map<WordClass, List<String>> CACHE = new EnumMap<>(WordClass.class);

    private WordClass wordClass;

    public WordGenerator(GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "word()";
    }

    @Override
    public WordGenerator adjective() {
        wordClass = WordClass.ADJECTIVE;
        return this;
    }

    @Override
    public WordGenerator adverb() {
        wordClass = WordClass.ADVERB;
        return this;
    }

    @Override
    public WordGenerator noun() {
        wordClass = WordClass.NOUN;
        return this;
    }

    @Override
    public WordGenerator verb() {
        wordClass = WordClass.VERB;
        return this;
    }

    @Override
    public WordGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final WordClass wc = ObjectUtils.defaultIfNull(
                this.wordClass, () -> random.oneOf(WordClass.values()));

        return getWord(random, wc);
    }

    private String getWord(final Random random, final WordClass wordClass) {
        final List<String> words = getWords(wordClass);
        return random.oneOf(words);
    }

    private List<String> getWords(final WordClass wordClass) {
        List<String> words = CACHE.get(wordClass);

        if (words == null) {
            words = load(wordClass.file);
            CACHE.put(wordClass, words);
        }

        return words;
    }

    private enum WordClass {
        ADJECTIVE("/adjectives.txt"),
        ADVERB("/adverbs.txt"),
        NOUN("/nouns.txt"),
        VERB("/verbs.txt");

        private final String file;

        WordClass(String file) {
            this.file = file;
        }
    }

    private static List<String> load(final String file) {
        final List<String> results = new ArrayList<>(3000);

        try {
            final InputStream in = WordGenerator.class.getResourceAsStream(file);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) { //NOPMD
                    results.add(line);
                }
            }
        } catch (Exception ex) {
            throw Fail.withInternalError("Error loading input stream", ex);
        }
        return results;
    }
}
