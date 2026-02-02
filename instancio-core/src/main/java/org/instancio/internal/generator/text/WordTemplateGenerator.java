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
package org.instancio.internal.generator.text;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.WordTemplateSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.text.WordGenerator.WordClass;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static org.instancio.internal.util.ErrorMessageUtils.invalidStringTemplate;

public class WordTemplateGenerator extends AbstractGenerator<String> implements WordTemplateSpec {

    private final Map<String, Pattern> patternCache = new HashMap<>();
    private final WordGenerator wordGenerator;
    private final String template;

    public WordTemplateGenerator(final GeneratorContext context, final String template) {
        super(context);
        this.wordGenerator = new WordGenerator(context);
        this.template = ApiValidator.notNull(template, "'template' must not be null");
    }

    @Override
    public String apiMethod() {
        return "wordTemplate()";
    }

    @Override
    public WordTemplateGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final List<String> templateKeys = StringUtils.getTemplateKeys(template);

        String result = template;

        for (String templateKey : templateKeys) {
            final WordClass wordClass = WordClass.getByKey(templateKey.toLowerCase(Locale.ROOT));
            if (wordClass == null) {
                final String reason = String.format("invalid template key '${%s}'", templateKey);
                throw Fail.withUsageError(invalidStringTemplate(template, reason));
            }

            String word = wordGenerator.wordClass(wordClass).generate(random);

            if (Character.isUpperCase(templateKey.charAt(0))) {
                // if first and last char are uppercase, assume the entire string is uppercase
                if (Character.isUpperCase(templateKey.charAt(templateKey.length() - 1))) {
                    word = word.toUpperCase(Locale.ROOT);
                } else {
                    word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
                }
            }
            result = regexPattern(templateKey).matcher(result).replaceFirst(word);
        }
        return result;
    }

    private Pattern regexPattern(final String templateKey) {
        Pattern p = patternCache.get(templateKey);
        if (p == null) {
            p = Pattern.compile("\\$\\{" + templateKey + '}');
            patternCache.put(templateKey, p);
        }
        return p;
    }
}
