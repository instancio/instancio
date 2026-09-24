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
package org.instancio.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Global settings are loaded once per JVM, when {@code Global} is initialised,
 * so only a cold start can be measured. To compare versions, run it with
 * a different instancio-core jar on the class path.
 */
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(30)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class GlobalSettingsBenchmarks {

    @Setup
    public void setup() {
        // Initialising logging is not part of loading the settings
        LoggerFactory.getLogger(GlobalSettingsBenchmarks.class);
    }

    @Benchmark
    public Class<?> loadGlobalSettings() throws ClassNotFoundException {
        // By name, since Global's methods differ between versions
        return Class.forName("org.instancio.support.Global");
    }
}
