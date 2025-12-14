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
package org.instancio.guava.internal.generator;

import com.google.common.net.HostAndPort;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.domain.internet.Ip4Generator;
import org.instancio.internal.util.Verify;

public class GuavaHostAndPortGenerator implements Generator<HostAndPort> {

    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65_535;

    private Ip4Generator ip4Generator;

    @Override
    public void init(final GeneratorContext context) {
        ip4Generator = new Ip4Generator(context);
    }

    @Override
    public HostAndPort generate(final Random random) {
        final String host = Verify.notNull(ip4Generator.generate(random), "host not null");
        final int port = random.intRange(MIN_PORT, MAX_PORT);
        return HostAndPort.fromParts(host, port);
    }

    @Override
    public Hints hints() {
        return Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    }
}
