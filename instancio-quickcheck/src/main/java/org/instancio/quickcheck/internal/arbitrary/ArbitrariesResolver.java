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
package org.instancio.quickcheck.internal.arbitrary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.instancio.Instancio;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.quickcheck.api.ForAll;
import org.instancio.quickcheck.api.artbitrary.Arbitrary;
import org.instancio.quickcheck.api.artbitrary.ArbitraryGenerator;
import org.instancio.quickcheck.internal.config.InstancioQuickcheckConfiguration;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.util.ReflectionUtils;

@ExperimentalApi
public class ArbitrariesResolver {
    private final List<Function<Object, ArbitraryGenerator<?>>> generators;
    
    public ArbitrariesResolver(List<Parameter> parameters, InstancioQuickcheckConfiguration configuration) {
        generators = new ArrayList<>(parameters.size());
        for (final Parameter parameter: parameters) {
            final ForAll annotation = parameter.getAnnotation(ForAll.class);
            if (annotation == null || annotation.value() == null || annotation.value().isEmpty()) {
                generators.add(instance -> 
                    Instancio
                        .of(parameter.getType())
                        .withSeed(configuration.seed())::create);
            } else {
                generators.add(instance -> resolveByName(instance, annotation.value())); 
            }
        }
    }

    private ArbitraryGenerator<?> resolveByName(Object testInstance, String value) {
        final List<Method> candidates = ReflectionUtils.findMethods(testInstance.getClass(), method -> method.getName().equals(value));
        for (final Method candidate: candidates) {
            if (Arbitrary.class.isAssignableFrom(candidate.getReturnType())) {
                try {
                    ReflectionUtils.makeAccessible(candidate);
                    final ArbitraryGenerator<?> generator = ((Arbitrary<?>) candidate.invoke(testInstance)).generator();
                    if (generator == null) {
                        throw new ParameterResolutionException("The arbitraries generator for '" + value + 
                            "' in test class instance " + testInstance.getClass().getName() + " returns null");
                    }
                    return generator;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new ParameterResolutionException("Unable to create arbitraries generator '" + value + 
                        "' in test class instance " + testInstance.getClass().getName(), ex);
                }
            }
        }
        
        throw new ParameterResolutionException("Unable to find arbitraries generator '" + value + 
            "' in test class instance " + testInstance.getClass().getName());
    }

    public Object[] resolve(Object testInstance) {
        return generators.stream().map(f -> f.apply(testInstance).generate()).toArray();
    }
}
