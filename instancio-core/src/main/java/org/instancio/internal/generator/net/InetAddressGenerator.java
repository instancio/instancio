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
package org.instancio.internal.generator.net;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.ExceptionUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressGenerator extends AbstractURIGenerator<InetAddress> {

    public InetAddressGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected InetAddress tryGenerateNonNull(final Random random) {
        try {
            return getLocalHost();
        } catch (UnknownHostException ex) {
            ExceptionUtils.logException("Failed generating InetAddress", ex);
            return null;
        }
    }

    InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
}
