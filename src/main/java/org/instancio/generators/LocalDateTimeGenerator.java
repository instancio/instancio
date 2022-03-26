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
package org.instancio.generators;

import org.instancio.internal.model.ModelContext;

import java.time.LocalDateTime;

public class LocalDateTimeGenerator extends AbstractRandomGenerator<LocalDateTime> {

    public LocalDateTimeGenerator(final ModelContext<?> context) {
        super(context);
    }

    @Override
    public LocalDateTime generate() {
        return LocalDateTime.now()
                .plusDays(random().intBetween(-3650, 3650))
                .minusSeconds(random().intBetween(0, 60));
    }
}
