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
package org.instancio.test.features.generator.finance;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class CurrencyGeneratorTest {

    private static final Set<String> SYMBOLS = Set.of(
            "د.إ", "L", "AR$", "A$", "B$", "CA$", "CL$", "CO$", "FJ$", "HK$",
            "MX$", "NZ$", "S$", "US$", "EC$", "₼", "৳", "лв", "د.ب.", "R$",
            "P", "Fr", "¥", "₡", "Kč", "kr", "RD$", "د.ج", "£", "€", "₾",
            "GH₵", "Q", "Ft", "Rp", "₹", "ع.د", "J$", "JD", "KSh", "៛", "₩",
            "KD", "₸", "₭", "ل.ل", "₨", "د.م.", "K", "₮", "Rf", "RM",
            "₦", "﷼", "S/.", "₱", "zł", "₲", "lei", "дин.", "₽", "฿",
            "د.ت", "₺", "TT$", "NT$", "TSh", "₴", "USh", "$U", "₫", "₣",
            "R", "ZK"
    );

    private static String create(final GeneratorSpecProvider<String> spec) {
        return Instancio.of(String.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void code() {
        final String result = create(gen -> gen.finance().currency().code());
        assertThat(result).hasSize(3).matches("[A-Z]{3}");
    }

    @Test
    void symbol() {
        final String result = create(gen -> gen.finance().currency().symbol());
        assertThat(result).isNotBlank();
    }

    @Test
    void allSymbols() {
        final List<String> strings = Instancio.ofList(String.class)
                .size(SYMBOLS.size())
                .generate(allStrings(), gen -> gen.finance().currency().symbol())
                .withUnique(allStrings())
                .create();

        assertThat(strings).containsAll(SYMBOLS);
    }

    @Test
    void nullable() {
        final Stream<String> result = Stream.generate(() -> create(gen -> gen.finance().currency().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }
}
