/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.mode;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.performance.LargeClass;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@FeatureTag(Feature.MODE)
@ExtendWith(InstancioExtension.class)
class StrictModeLargeClassTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, 1000);

    @Test
    @FeatureTag({Feature.ON_COMPLETE, Feature.SCOPE})
    void largeClassWithScopedSelectors() {
        final AtomicInteger callbackCount = new AtomicInteger();
        Instancio.of(LargeClass.class)
                .withNullable(all(
                        all(Foo.class),
                        all(Bar.class),
                        all(Baz.class)))
                .set(field(Item.class, "value").within(scope(List.class), scope(Foo.class)), "foo")
                .set(field(Item.class, "value").within(scope(Bar.class)), "bar")
                .set(field(Item.class, "value").within(scope(Baz.class)), "baz")
                .onComplete(field(Foo.class, "fooValue").within(scope(List.class), scope(Foo.class)), (Item<String> item) -> {
                    assertThat(item.getValue()).isEqualTo("foo");
                    callbackCount.incrementAndGet();
                })
                .onComplete(field(Foo.class, "fooValue").within(scope(Foo[].class)), (Item<String> item) -> {
                    assertThat(item.getValue()).isNotEqualTo("foo");
                    callbackCount.incrementAndGet();
                })
                .onComplete(field(Foo.class, "fooValue").within(scope(Map.class)), (UUID uuid) -> {
                    assertThat(uuid).isNotNull();
                    callbackCount.incrementAndGet();
                })
                .onComplete(field(Bar.class, "barValue").within(scope(Set.class)), (Item<String> item) -> {
                    assertThat(item.getValue()).isEqualTo("bar");
                    callbackCount.incrementAndGet();
                })
                .onComplete(field(Baz.class, "bazValue").within(scope(Map.class)), (Item<String> item) -> {
                    assertThat(item.getValue()).isEqualTo("baz");
                    callbackCount.incrementAndGet();
                })
                .create();

        // Verify roughly the expected number of callbacks
        assertThat(callbackCount.get()).isBetween(200_000, 250_000);
    }
}
