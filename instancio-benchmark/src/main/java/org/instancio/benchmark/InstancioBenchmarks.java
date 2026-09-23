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

import org.instancio.Instancio;
import org.instancio.benchmark.pojo.cyclic.OneToManyWithCrossReferences.ObjectA;
import org.instancio.benchmark.pojo.person.Address;
import org.instancio.benchmark.pojo.person.Person;
import org.instancio.benchmark.pojo.person.Phone;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class InstancioBenchmarks {

    private static final long SEED = -1L;
    private static final int LIST_SIZE = 500;

    @Benchmark
    public void createCyclic(Blackhole blackhole) {
        final ObjectA result = Instancio.of(ObjectA.class)
                .withMaxDepth(10)
                .withSeed(SEED)
                .create();

        blackhole.consume(result);
    }

    @Benchmark
    public void createPerson(Blackhole blackhole) {
        final List<Person> result = Instancio.ofList(Person.class)
                .size(LIST_SIZE)
                .withSeed(SEED)
                .create();

        blackhole.consume(result);
    }

    @Benchmark
    public void createWithSelectors(Blackhole blackhole) {
        final List<Person> result = Instancio.ofList(Person.class)
                .size(LIST_SIZE)
                .withSeed(SEED)
                .ignore(field(Person::getDob))
                .withNullable(field(Person::getPets))
                .subtype(field(Person::getHobbies), LinkedList.class)
                .assign(valueOf(field(Person::getFirstName).atDepth(2)).set("Bart"))
                .generate(field(Person::getLastName), gen -> gen.oneOf("Simpson"))
                .set(field(Address::getCity).within(field(Person::getAddresses).toScope()), "Springfield")
                .set(field(Address::getStreet).within(field(Person::getAddresses).toScope()), "742 Evergreen Terrace")
                .set(all(
                                field(Address::getCountry).within(field(Person::getAddresses).toScope()),
                                field(Phone::getCountryCode).within(field(Address::getPhoneNumbers).toScope())),
                        "US")
                .onComplete(all(UUID.class), uuid -> {
                    // no-op
                })
                .create();

        blackhole.consume(result);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(InstancioBenchmarks.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
