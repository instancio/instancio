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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

/**
 * Generator spec for IPv4.
 *
 * @since 2.12.0
 */
public interface Ip4GeneratorSpec extends GeneratorSpec<String> {

    /**
     * Generates IPv4 address from
     * <a href="https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing">CIDR notation</a>,
     * for example: {@code fromCidr("192.168.1.0/24")}.
     *
     * @param cidr CIDR notation from which an IP address will be generated
     * @return generator spec
     * @since 2.12.0
     */
    GeneratorSpec<String> fromCidr(String cidr);
}
