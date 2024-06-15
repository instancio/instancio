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

package org.instancio.test.features.generator.util;

import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Empty test for documentation purposes.
 *
 * <p>The {@code UUIDGenerator} is exposed via {@code Gen}
 * to provide a convenience method for generating a list:
 *
 * <pre>{@code
 * List<UUID> uuids = Instancio.gen().uuid().list(10);
 * }</pre>
 *
 * <p>But it is not exposed via {@code generate()} method
 * since there does not seem to be much value in it:
 *
 * <pre>{@code
 * Instancio.of(Example.class)
 *    .generate(field("id"), gen -> gen.uuid()) // not available!
 *    .creatE();
 * }</pre>
 * <p>
 * Therefore, there's currently nothing to test here.
 */
@FeatureTag(Feature.UNSUPPORTED)
@ExtendWith(InstancioExtension.class)
class UUIDGeneratorTest { // NOSONAR
}