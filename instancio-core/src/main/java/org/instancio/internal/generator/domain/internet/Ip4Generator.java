/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.generator.domain.internet;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.Ip4Spec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.support.Global;

public class Ip4Generator extends AbstractGenerator<String> implements Ip4Spec {

    private String cidr;

    public Ip4Generator() {
        this(Global.generatorContext());
    }

    public Ip4Generator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "ip4()";
    }

    @Override
    public Ip4Generator fromCidr(final String cidr) {
        this.cidr = ApiValidator.notNull(cidr, "CIDR must not be null");
        return this;
    }

    @Override
    public Ip4Generator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        if (cidr == null) {
            return generateAny(random);
        }
        try {
            return generateFromCidr(random);
        } catch (Exception ex) {
            throw new InstancioApiException(
                    String.format("Error generating IPv4 address from: '%s'", cidr), ex);
        }
    }

    private String generateAny(final Random random) {
        return String.format("%d.%d.%d.%d",
                random.intRange(1, 255),
                random.intRange(0, 255),
                random.intRange(0, 255),
                random.intRange(0, 255));
    }

    // Source: https://stackoverflow.com/questions/62124452
    private String generateFromCidr(final Random random) {
        final String[] cidrValue = cidr.split("/");
        final String[] buf = cidrValue[0].split("\\.");
        final byte[] ip = {
                (byte) Integer.parseInt(buf[0]),
                (byte) Integer.parseInt(buf[1]),
                (byte) Integer.parseInt(buf[2]),
                (byte) Integer.parseInt(buf[3])};

        final int value = 0xffffffff << (32 - Integer.parseInt(cidrValue[1]));
        final byte[] subnet = {
                (byte) (value >>> 24),
                (byte) (value >> 16 & 0xff),
                (byte) (value >> 8 & 0xff),
                (byte) (value & 0xff)
        };

        final byte[] from = new byte[4];
        final byte[] to = new byte[4];
        for (int i = 0; i < to.length; i++) {
            from[i] = (byte) (ip[i] & subnet[i]);
            to[i] = (byte) (ip[i] | ~subnet[i]);
        }

        return String.format("%d.%d.%d.%d",
                random.intRange(Byte.toUnsignedInt(from[0]), Byte.toUnsignedInt(to[0])),
                random.intRange(Byte.toUnsignedInt(from[1]), Byte.toUnsignedInt(to[1])),
                random.intRange(Byte.toUnsignedInt(from[2]), Byte.toUnsignedInt(to[2])),
                random.intRange(Byte.toUnsignedInt(from[3]), Byte.toUnsignedInt(to[3])));
    }
}
