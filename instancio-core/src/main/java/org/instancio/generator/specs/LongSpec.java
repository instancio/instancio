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
package org.instancio.generator.specs;

/**
 * Spec for generating {@link Long} values.
 *
 * @since 2.6.0
 */
public interface LongSpec extends NumberSpec<Long> {

    @Override
    LongSpec min(Long min);

    @Override
    LongSpec max(Long max);

    @Override
    LongSpec range(Long min, Long max);

    @Override
    LongSpec nullable();
}
