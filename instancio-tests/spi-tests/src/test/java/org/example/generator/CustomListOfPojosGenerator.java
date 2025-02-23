/*
 * Copyright 2022-2025 the original author or authors.
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
package org.example.generator;

import lombok.Data;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;

import java.util.ArrayList;
import java.util.List;

public class CustomListOfPojosGenerator implements Generator<List<CustomListOfPojosGenerator.Pojo>> {

    public static @Data class Container {
        List<Pojo> list;
    }

    public static @Data class Pojo {
        Long value;
    }

    @Override
    public List<Pojo> generate(final Random random) {
        List<Pojo> list = new ArrayList<>();
        list.add(new Pojo());
        return list;
    }

    @Override
    public Hints hints() {
        return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);
    }
}
