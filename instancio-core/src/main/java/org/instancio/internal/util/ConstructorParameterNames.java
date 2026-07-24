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
package org.instancio.internal.util;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * Resolves constructor parameter names.
 *
 * <p>This is the canonical description of how parameter names are obtained;
 * other classes involved should refer here rather than restate it.
 *
 * <p>Names are resolved from two sources, in order of preference:
 *
 * <ol>
 *   <li>reflection, if the class was compiled with {@code -parameters},
 *       which emits the {@code MethodParameters} attribute; otherwise</li>
 *   <li>the {@code LocalVariableTable} attribute, emitted when the class
 *       is compiled with debug information ({@code -g} or {@code -g:vars}).</li>
 * </ol>
 *
 * <p>Neither source is guaranteed to be present. Note that {@code javac}
 * on its own provides neither: it defaults to {@code -g:lines,source},
 * which omits the {@code LocalVariableTable}, and does not pass
 * {@code -parameters}. In practice it is the second source that applies,
 * because Maven and Gradle both compile with full debug information by
 * default (Maven's {@code <debug>} and Gradle's {@code options.debug}
 * default to {@code true}, each passing {@code -g}), while neither enables
 * {@code -parameters}. Names are therefore unavailable mainly for classes
 * compiled with {@code -g:none} or {@code -g:lines}, and for obfuscated
 * or minimised class files.
 *
 * <p>Although the {@code LocalVariableTable} is optional debug information,
 * the slot layout it is read against is mandated by the JVM specification
 * (JVMS 2.6.1): slot 0 holds {@code this}, followed by the parameters in
 * declaration order, with {@code long} and {@code double} occupying two
 * slots each. The attribute is therefore either absent or authoritative;
 * it cannot misreport which slot holds which parameter. Resolution fails
 * by returning {@code null}, not by returning incorrect names.
 *
 * @see LocalVariableTableReader
 */
public final class ConstructorParameterNames {

    // Constructor parameter names keyed by constructor descriptor,
    // e.g. "(Ljava/lang/String;I)V". Parsed once per class.
    private static final ClassValue<Map<String, String[]>> LVT_CACHE = new ClassValue<>() {
        @Override
        protected Map<String, String[]> computeValue(final Class<?> type) {
            return LocalVariableTableReader.getConstructorParameterNames(type);
        }
    };

    /**
     * Returns the parameter names of the given constructor,
     * or {@code null} if the names could not be resolved.
     *
     * @param constructor to resolve parameter names for
     * @return parameter names in declaration order, or {@code null} if unavailable
     */
    public static String @Nullable [] resolve(final Constructor<?> constructor) {
        final Parameter[] parameters = constructor.getParameters();

        if (parameters.length == 0) {
            return new String[0];
        }
        if (parameters[0].isNamePresent()) { // javac -parameters
            final String[] names = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                names[i] = parameters[i].getName();
            }
            return names;
        }
        return LVT_CACHE
                .get(constructor.getDeclaringClass())
                .get(getDescriptor(constructor));
    }

    private static String getDescriptor(final Constructor<?> constructor) {
        return MethodType.methodType(void.class, constructor.getParameterTypes()).descriptorString();
    }

    private ConstructorParameterNames() {
        // non-instantiable
    }
}
